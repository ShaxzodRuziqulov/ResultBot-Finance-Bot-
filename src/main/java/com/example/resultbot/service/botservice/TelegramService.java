package com.example.resultbot.service.botservice;

import com.example.resultbot.entity.User;
import com.example.resultbot.entity.enumirated.State;
import com.example.resultbot.repository.UserRepository;
import com.example.resultbot.service.AuthenticationService;
import com.example.resultbot.service.UserService;
import com.example.resultbot.service.dto.RegisterUserDto;
import com.example.resultbot.service.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TelegramService extends SpringWebhookBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramService.class);
    private final Map<Long, State> userStates = new HashMap<>();
    private final Map<Long, RegisterUserDto> userInputs = new HashMap<>();
    private final TelegramMessageHandler telegramMessageHandler;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    public TelegramService(TelegramMessageHandler telegramMessageHandler, UserService userService,
                           AuthenticationService authenticationService, UserRepository userRepository) {
        super(new SetWebhook());
        this.telegramMessageHandler = telegramMessageHandler;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
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
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            log.warn("Xabar yoki matn yo‘q.");
            return null;
        }

        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().trim();

        SendMessage response = switch (messageText.toLowerCase()) {
            case "/start" -> telegramMessageHandler.handleStartCommand(chatId);
            case "/register" -> handleRegisterCommand(chatId);
            case "/verify" -> handleVerificationCommand(chatId, messageText);
            default -> handleUserState(chatId, messageText, update.getMessage());
        };

        try {
            execute(response);
        } catch (TelegramApiException e) {
            log.error("Javob yuborishda xatolik yuz berdi: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        setWebhook.setUrl(webhookPath);
        log.info("Webhook o'rnatildi: {}", webhookPath);
    }

    private SendMessage handleRegisterCommand(Long chatId) {
        if (!userService.isUserRegistered(chatId)) {
            return telegramMessageHandler.handleAlreadyRegisteredMessage(chatId);
        }

        userStates.put(chatId, State.AWAITING_EMAIL);
        return telegramMessageHandler.sendMessage(chatId, "Iltimos, emailingizni kiriting:");
    }

    private SendMessage handleUserState(Long chatId, String messageText, Message message) {
        State state = userStates.getOrDefault(chatId, State.NONE);
        return switch (state) {
            case AWAITING_EMAIL -> handleEmailInput(chatId, messageText);
            case AWAITING_PASSWORD -> handlePasswordInput(chatId, messageText, message);
            default -> telegramMessageHandler.handleUnknownCommand(chatId);
        };
    }

    private SendMessage handleEmailInput(Long chatId, String email) {
        if (!isValidEmail(email)) {
            return telegramMessageHandler.sendMessage(chatId, "Email noto‘g‘ri. Iltimos, to‘g‘ri email kiriting:");
        }

        userInputs.put(chatId, new RegisterUserDto(email));
        userStates.put(chatId, State.AWAITING_PASSWORD);

        return telegramMessageHandler.sendMessage(chatId, "Email qabul qilindi. Endi parol kiriting:");
    }

    private SendMessage handlePasswordInput(Long chatId, String password, Message message) {
        RegisterUserDto input = userInputs.get(chatId);
        if (input == null) {
            return telegramMessageHandler.sendMessage(chatId, "Xatolik: foydalanuvchi ma'lumotlari topilmadi. Iltimos, qaytadan /register buyrug‘ini kiriting.");
        }

        if (message != null && message.getChat() != null) {
            input.setFirstName(message.getChat().getFirstName());
            input.setLastName(message.getChat().getLastName());
            input.setUserName(message.getChat().getUserName());
        }

        input.setPassword(password);
        input.setChatId(chatId);

        UserDto registeredUser = authenticationService.signup(input);
        userStates.remove(chatId);
        userInputs.remove(chatId);

        return telegramMessageHandler.sendMessage(chatId, "Ro‘yxatdan o‘tish muvaffaqiyatli! Emailingizga tasdiqlash kodi yuborildi.");
    }

    private SendMessage handleVerificationCommand(Long chatId, String messageText) {
        String[] parts = messageText.split(" ");
        if (parts.length < 2) {
            return telegramMessageHandler.sendMessage(chatId, "Tasdiqlash kodi noto‘g‘ri formatda. /verify [kod] shaklida kiriting.");
        }

        String verificationCode = parts[1];
        Optional<User> userOptional = userRepository.findByChatId(chatId);

        if (userOptional.isEmpty()) {
            return telegramMessageHandler.sendMessage(chatId, "Foydalanuvchi topilmadi.");
        }

        try {
            boolean isVerified = authenticationService.verifyCode(userOptional.get().getEmail(), verificationCode);
            if (isVerified) {
                return telegramMessageHandler.sendMessage(chatId, "Hisobingiz tasdiqlandi!");
            }
        } catch (IllegalArgumentException e) {
            return telegramMessageHandler.sendMessage(chatId, "Tasdiqlash kodi noto‘g‘ri. Iltimos, qayta urinib ko‘ring.");
        }

        return telegramMessageHandler.sendMessage(chatId, "Tasdiqlash amalga oshmadi.");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }
}
