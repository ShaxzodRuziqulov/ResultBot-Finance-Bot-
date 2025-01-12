package com.example.resultbot.service.botservice;

import com.example.resultbot.entity.User;
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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.*;

@Service
public class TelegramService extends SpringWebhookBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<Long, RegisterUserDto> userInputs = new HashMap<>();
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
            UserService userService,
            AuthenticationService authenticationService,
            UserRepository userRepository,
            MessageFormatterService messageFormatterService,
            TelegramMessageHandler telegramMessageHandler, TelegramBotProvider telegramBotProvider) {
        super(new SetWebhook());
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
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText().trim();

            try {
                SendMessage response;
                if (messageText.startsWith("/verify")) {
                    response = handleVerificationCommand(chatId, messageText);
                } else {
                    response = switch (messageText.toLowerCase()) {
                        case "/start" -> handleStartCommand(chatId);
                        case "/register" -> handleRegisterCommand(chatId);
                        case "📊 hisobotlar" -> handleReportsCommand(chatId);
                        case "⚙️ sozlamalar va kirish huquqlari" -> handleSettingsCommand(chatId);
                        default -> handleUserState(chatId, messageText, update.getMessage());
                    };
                }

                execute(response);
                return response;

            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.error("Javob yuborishda xatolik yuz berdi: {}", e.getMessage());
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            try {
                switch (callbackData) {

                    case "reports_monthly_income" -> execute(sendMessage(chatId, "Oylik daromadlar hisoblanmoqda..."));
                    case "reports_monthly_expense" -> execute(sendMessage(chatId, "Oylik xarajatlar hisoblanmoqda..."));
                    case "reports_additional" ->
                            execute(sendMessage(chatId, "Qo‘shimcha hisobot turlari tanlanmoqda..."));
                    case "settings_edit_profile" ->
                            execute(sendMessage(chatId, "Profil ma’lumotlarini o‘zgartirish..."));
                    case "settings_view_access" -> execute(sendMessage(chatId, "Joriy kirish huquqlarini ko‘rish..."));
                    case "settings_change_password" -> execute(sendMessage(chatId, "Parolni o‘zgartirish..."));
                    default -> {
                        execute(sendMessage(chatId, "Tanlangan amalni tushunmadim."));
                        log.info("Kelgan callbackData: {}", callbackData);
                    }
                }
            } catch (TelegramApiException e) {
                log.error("Callback query-ni qayta ishlashda xatolik: {}", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        return null;
    }


    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        setWebhook.setUrl(webhookPath);
        log.info("Webhook o'rnatildi: {}", webhookPath);
    }

    private SendMessage handleRegisterCommand(Long chatId) throws TelegramApiException {
        if (!userService.isUserRegistered(chatId)) {
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
}
