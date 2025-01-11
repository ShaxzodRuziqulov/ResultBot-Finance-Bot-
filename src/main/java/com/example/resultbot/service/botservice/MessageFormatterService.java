package com.example.resultbot.service.botservice;

import org.springframework.stereotype.Service;

@Service
class MessageFormatterService {
    public String formatWelcomeBackMessage() {
        return "Xush kelibsiz! Siz allaqachon ro‘yxatdan o‘tgan siz.";
    }

    public String formatNewUserMessage() {
        return "Assalomu alaykum! Ro‘yxatdan o‘tish uchun /register buyrug'ini yuboring.";
    }

    public String formatUnknownCommandMessage() {
        return "Noma'lum buyruq. Iltimos, /start yoki /register buyrug‘idan foydalaning.";
    }


}