package com.example.resultbot.service.botservice;

import com.example.resultbot.entity.User;
import com.example.resultbot.entity.enumirated.CallbackActions;
import com.example.resultbot.entity.enumirated.State;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.UserRepository;
import com.example.resultbot.service.AuthenticationService;
import com.example.resultbot.service.UserService;
import com.example.resultbot.service.dto.RegisterUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.File;
import java.util.*;

@Service
public class TelegramService extends SpringWebhookBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<Long, RegisterUserDto> userInputs = new HashMap<>();
    private final ReportService reportService;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final MessageFormatterService messageFormatterService;
    private final TelegramMessageHandler telegramMessageHandler;
    private final TelegramBotProvider telegramBotProvider;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    public TelegramService(
            ReportService reportService, UserService userService,
            AuthenticationService authenticationService,
            UserRepository userRepository,
            MessageFormatterService messageFormatterService,
            TelegramMessageHandler telegramMessageHandler,
            TelegramBotProvider telegramBotProvider) {
        super(new SetWebhook());
        this.reportService = reportService;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.messageFormatterService = messageFormatterService;
        this.telegramMessageHandler = telegramMessageHandler;
        this.telegramBotProvider = telegramBotProvider;
    }


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) {
        setWebhook.setUrl(webhookPath);
        log.info("Webhook o'rnatildi: {}", webhookPath);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                return handleTextMessage(update);
            }

            if (update.hasCallbackQuery()) {
                return handleCallback(update);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private BotApiMethod<?> handleTextMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().trim();

        try {
            SendMessage response = processCommand(chatId, messageText, update.getMessage());
            execute(response);
            return response;
        } catch (TelegramApiException e) {
            log.error("Error sending response: {}", e.getMessage(), e);
        }
        return null;
    }

    private SendMessage processCommand(Long chatId, String messageText, Message message) throws TelegramApiException {
        if (messageText.startsWith("/verify")) {
            return handleVerificationCommand(chatId, messageText);
        }

        return switch (messageText.toLowerCase()) {
            case "/start" -> handleStartCommand(chatId);
            case "/register" -> handleRegisterCommand(chatId);
            case "\uD83D\uDCCA hisobotlar" -> handleReportsCommand(chatId);
            case "\u2699\uFE0F sozlamalar va kirish huquqlari" -> handleSettingsCommand(chatId);
            default -> handleUserState(chatId, messageText, message);
        };
    }

    private BotApiMethod<?> handleCallback(Update update) throws TelegramApiException {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        if (callbackQuery == null) {
            log.warn("CallbackQuery null bo‘lishi mumkin, qaytmoqda...");
            return null;
        }

        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String callbackQueryId = callbackQuery.getId();

        log.info("Callback query received: chatId={}, data={}", chatId, callbackData);

        callBackQuery(callbackQueryId);

        reports(callbackData, chatId);

        editMessage(chatId, messageId, callbackQuery);

        return new SendMessage(chatId.toString(), "✅ Amal bajarildi."); // Webhook uchun 200 OK qaytarish
    }

    private void callBackQuery(String callbackQueryId) {
        if (callbackQueryId != null) {
            try {
                answerCallbackQuery(callbackQueryId);
                log.info("Callback query answered successfully.");
            } catch (TelegramApiException e) {
                log.error("AnswerCallbackQuery yuborishda xatolik: {}", e.getMessage());
            }
        }
    }

    private void reports(String callbackData, Long chatId) throws TelegramApiException {
        if ("REPORTS_MONTHLY_INCOME".equals(callbackData)) {
            log.info("Oylik daromadlar faylini jo‘natish boshlanmoqda...");
            execute(sendMessage(chatId, "Oylik daromadlar fayli tayyorlanmoqda..."));

            String filePath = generateMonthlyIncomeReport();
            if (filePath == null || filePath.isEmpty()) {
                execute(sendMessage(chatId, "Faylni yaratishda xatolik yuz berdi."));
                return; // Fayl yo‘q bo‘lsa, davom etmaymiz
            }

            sendExcelReport(chatId, filePath);
        } else {
            log.info("Boshqa callback ma’lumot kelib tushdi: {}", callbackData);
            processCallbackData(callbackData, chatId);
        }
    }

    private void editMessage(Long chatId, Integer messageId, CallbackQuery callbackQuery) {
        if (callbackQuery == null || callbackQuery.getMessage() == null || callbackQuery.getMessage().getReplyMarkup() == null) {
            log.warn("Inline tugma allaqachon o‘chirib bo‘lingan yoki mavjud emas.");
            return;
        }

        try {
            EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
            editMarkup.setChatId(chatId);
            editMarkup.setMessageId(messageId);
            editMarkup.setReplyMarkup(null);

            execute(editMarkup);
            log.info("Inline tugma o‘chirildi.");
        } catch (TelegramApiException e) {
            log.warn("Xabarni tahrirlashda xatolik: {}", e.getMessage());
        }
    }

    private void answerCallbackQuery(String callbackQueryId) throws TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        answer.setText("Amal bajarilmoqda...");
        answer.setShowAlert(false);
        execute(answer);
    }

    private void processCallbackData(String callbackData, Long chatId) throws TelegramApiException {
        try {
            CallbackActions action = CallbackActions.valueOf(callbackData.toUpperCase());
            switch (action) {
                case REPORTS_MONTHLY_INCOME -> handleMonthlyIncomeReport(chatId);
                case REPORTS_MONTHLY_EXPENSE -> execute(sendMessage(chatId, "Oylik xarajatlar hisoblanmoqda..."));
                case REPORTS_ADDITIONAL -> handleAdditionalReports(chatId);
                case SETTINGS_EDIT_PROFILE -> execute(sendMessage(chatId, "Profil ma’lumotlarini o‘zgartirish..."));
                case SETTINGS_VIEW_ACCESS -> execute(sendMessage(chatId, "Joriy kirish huquqlarini ko‘rish..."));
                case SETTINGS_CHANGE_PASSWORD -> execute(sendMessage(chatId, "Parolni o‘zgartirish..."));
                default -> execute(sendMessage(chatId, "Tanlangan amalni tushunmadim."));
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid callback data: {}", callbackData, e);
            execute(sendMessage(chatId, "Tanlangan amalni tushunmadim."));
        } catch (TelegramApiException e) {
            log.error("Error processing callback query: {}", e.getMessage(), e);
        }
    }

    private void handleMonthlyIncomeReport(Long chatId) throws TelegramApiException {
        execute(sendMessage(chatId, "Oylik daromadlar hisoblanmoqda..."));
        String filePath = generateMonthlyIncomeReport();
        sendExcelReport(chatId, filePath);
    }

    private void handleAdditionalReports(Long chatId) throws TelegramApiException {
        execute(sendMessage(chatId, "Qo‘shimcha hisobot turlari tanlanmoqda..."));
        sendAdditionalFilters(chatId);
    }

    private String generateMonthlyIncomeReport() {
        return reportService.generateMonthlyIncomeReport();
    }


    private void sendExcelReport(Long chatId, String filePath) {
        try {
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId.toString());
            sendDocument.setDocument(new InputFile(new File(filePath)));
            sendDocument.setCaption("Oylik daromadlar hisobot fayli");

            execute(sendDocument);
            log.info("Fayl muvaffaqiyatli jo‘natildi: {}", filePath);
        } catch (TelegramApiException e) {
            log.error("Faylni jo‘natishda xatolik: {}", e.getMessage(), e);
        }
    }

    private SendMessage handleRegisterCommand(Long chatId) throws TelegramApiException {
        if (userService.isUserRegistered(chatId)) {
            return handleAlreadyRegisteredMessage(chatId);
        }

        userStates.put(chatId, State.AWAITING_EMAIL);
        return sendMessage(chatId, "Iltimos, emailingizni kiriting:");
    }

    private SendMessage handleUserState(Long chatId, String messageText, Message message) throws TelegramApiException {
        State state = userStates.getOrDefault(chatId, State.NONE);
        return switch (state) {
            case AWAITING_EMAIL -> handleEmailInput(chatId, messageText);
            case AWAITING_PASSWORD -> handlePasswordInput(chatId, messageText, message);
            default -> handleUnknownCommand(chatId);
        };
    }

    private SendMessage handleEmailInput(Long chatId, String email) throws TelegramApiException {
        if (!isValidEmail(email)) {
            return sendMessage(chatId, "Email noto‘g‘ri. Iltimos, to‘g‘ri email kiriting:");
        }

        userInputs.put(chatId, new RegisterUserDto(email));
        userStates.put(chatId, State.AWAITING_PASSWORD);

        return sendMessage(chatId, "Email qabul qilindi. Endi parol kiriting:");
    }

    private SendMessage handlePasswordInput(Long chatId, String password, Message message) throws TelegramApiException {
        RegisterUserDto input = userInputs.get(chatId);
        if (input == null) {
            return sendMessage(chatId, "Xatolik: foydalanuvchi ma'lumotlari topilmadi. Iltimos, qaytadan /register buyrug‘ini kiriting.");
        }

        if (message != null && message.getChat() != null) {
            input.setFirstName(message.getChat().getFirstName());
            input.setLastName(message.getChat().getLastName());
            input.setUserName(message.getChat().getUserName());
        }
        assert message != null;
        input.setPassword(password);
        input.setChatId(chatId);

        authenticationService.signup(input);
        userStates.remove(chatId);
        userInputs.remove(chatId);

        return sendMessage(chatId, "Ro‘yxatdan o‘tish muvaffaqiyatli! Emailingizga tasdiqlash kodi yuborildi.");
    }

    private SendMessage handleVerificationCommand(Long chatId, String messageText) throws TelegramApiException {
        String[] parts = messageText.split(" ");
        if (parts.length < 2) {
            return sendMessage(chatId, "Tasdiqlash kodi noto‘g‘ri formatda. /verify [kod] shaklida kiriting.");
        }

        String verificationCode = parts[1];
        Optional<User> userOptional = userRepository.findByChatId(chatId);

        if (userOptional.isEmpty()) {
            return sendMessage(chatId, "Foydalanuvchi topilmadi.");
        }

        User user = userOptional.get();
        if (user.getVerificationCode().equals(verificationCode)) {
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
            return sendMessage(chatId, "Hisobingiz muvaffaqiyatli tasdiqlandi!");
        } else {
            return sendMessage(chatId, "Tasdiqlash kodi noto‘g‘ri. Iltimos, qayta urinib ko‘ring.");
        }
    }


    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public SendMessage handleUnknownCommand(Long chatId) throws TelegramApiException {
        String message = messageFormatterService.formatUnknownCommandMessage();
        return sendMessage(chatId, message);
    }

    public SendMessage handleStartCommand(Long chatId) throws TelegramApiException {
        return telegramMessageHandler.handleStartCommand(chatId);
    }

    public SendMessage handleAlreadyRegisteredMessage(Long chatId) throws TelegramApiException {
        return sendMessage(chatId, "Siz allaqachon ro‘yxatdan o‘tgan siz.");
    }

    private SendMessage handleSettingsCommand(Long chatId) throws TelegramApiException {
        return telegramMessageHandler.handleSettingsCommand(chatId);
    }

    private SendMessage handleReportsCommand(Long chatId) throws TelegramApiException {
        return telegramMessageHandler.handleReportsCommand(chatId);
    }

    public SendMessage sendMessage(Long chatId, String text) throws TelegramApiException {
        return telegramBotProvider.sendMessageExecute(chatId, text);
    }

    private void sendAdditionalFilters(Long chatId) throws TelegramApiException {
        execute(telegramMessageHandler.createFilterKeyboard(chatId));
    }
}