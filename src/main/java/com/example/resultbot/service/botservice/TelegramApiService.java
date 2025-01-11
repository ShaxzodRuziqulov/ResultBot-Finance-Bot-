package com.example.resultbot.service.botservice;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.ByteArrayOutputStream;
@Service
public interface TelegramApiService {
    void sendMessage(Long chatId, String text);

    void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup);

    void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup);

    void sendDocument(Long chatId, ByteArrayOutputStream document, String filename);
}

