package com.example.resultbot.service.botservice;

import com.example.resultbot.service.UserService;
import com.example.resultbot.service.botservice.util.InlineKeyboardMarkupService;
import com.example.resultbot.service.botservice.util.KeyboardMarkupService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramMessageHandler {

    private final TelegramBotProvider telegramBotProvider;
    private final UserService userService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final MessageFormatterService messageFormatterService;
    private final InlineKeyboardMarkupService inlineKeyboardMarkupService;

    public TelegramMessageHandler(
            TelegramBotProvider telegramBotProvider,
            UserService userService,
            KeyboardMarkupService keyboardMarkupService,
            MessageFormatterService messageFormatterService,
            InlineKeyboardMarkupService inlineKeyboardMarkupService) {
        this.telegramBotProvider = telegramBotProvider;
        this.userService = userService;
        this.keyboardMarkupService = keyboardMarkupService;
        this.messageFormatterService = messageFormatterService;
        this.inlineKeyboardMarkupService = inlineKeyboardMarkupService;
    }

    public SendMessage handleStartCommand(Long chatId) throws TelegramApiException {
        if (!userService.isUserRegistered(chatId)) {
            return sendMessageWithKeyboard(chatId, messageFormatterService.formatWelcomeBackMessage(), keyboardMarkupService.createMainMenu());
        } else {
            return sendMessage(chatId, messageFormatterService.formatNewUserMessage());
        }
    }

    public SendMessage handleReportsCommand(Long chatId) throws TelegramApiException {
        String message = "Qaysi hisobot turini tanlaysiz?";
        InlineKeyboardMarkup reportsMenu = inlineKeyboardMarkupService.createReportsMenu();
        return sendMessageWithKeyboard(chatId, message, reportsMenu);
    }

    public SendMessage handleSettingsCommand(Long chatId) throws TelegramApiException {
        String message = "Sozlamalar va kirish huquqlaridan birini tanlang:";
        InlineKeyboardMarkup settingsMenu = inlineKeyboardMarkupService.createSettingsMenu();
        return sendMessageWithKeyboard(chatId, message, settingsMenu);
    }

    private SendMessage sendMessage(Long chatId, String text) throws TelegramApiException {
        return telegramBotProvider.sendMessageExecute(chatId, text);
    }

    private SendMessage sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) throws TelegramApiException {
        return telegramBotProvider.sendMessageExecute(chatId, text, keyboard);
    }

    private SendMessage sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) throws TelegramApiException {
        return telegramBotProvider.sendMessageExecute(chatId, text, keyboard);
    }
}
