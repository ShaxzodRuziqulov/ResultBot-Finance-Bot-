package com.example.resultbot.service.botservice;

import com.example.resultbot.service.UserService;
import com.example.resultbot.service.botservice.util.InlineKeyboardMarkupService;
import com.example.resultbot.service.botservice.util.KeyboardMarkupService;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class TelegramMessageHandler {
    private final UserService userService;
    private final KeyboardMarkupService keyboardMarkupService;
    private final MessageFormatterService messageFormatterService;
    private final InlineKeyboardMarkupService inlineKeyboardMarkupService;
    private final ReportService reportService;

    public enum ReportType {
        REPORT_MONTHLY_INCOME("REPORT_MONTHLY_INCOME"),
        REPORT_MONTHLY_EXPENSES("REPORT_MONTHLY_EXPENSES"),
        REPORT_CUSTOM("REPORT_CUSTOM");

        private final String value;

        ReportType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public TelegramMessageHandler(
            UserService userService,
            KeyboardMarkupService keyboardMarkupService,
            MessageFormatterService messageFormatterService,
            InlineKeyboardMarkupService inlineKeyboardMarkupService,
            ReportService reportService) {
        this.userService = userService;
        this.keyboardMarkupService = keyboardMarkupService;
        this.messageFormatterService = messageFormatterService;
        this.inlineKeyboardMarkupService = inlineKeyboardMarkupService;
        this.reportService = reportService;
    }

    public SendMessage handleStartCommand(Long chatId) {
        if (userService.isUserRegistered(chatId)) {
            return handleNewUserRegistration(chatId);
        } else {
            return handleWelcomeBack(chatId);
        }
    }

    private SendMessage handleWelcomeBack(Long chatId) {
        String message = messageFormatterService.formatWelcomeBackMessage();
        return sendMessage(chatId, message, keyboardMarkupService.createMainMenu());
    }

    private SendMessage handleNewUserRegistration(Long chatId) {
        String message = messageFormatterService.formatNewUserMessage();
        return sendMessage(chatId, message);
    }

    public SendMessage handleUnknownCommand(Long chatId) {
        String message = messageFormatterService.formatUnknownCommandMessage();
        return sendMessage(chatId, message);
    }

    public SendMessage sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        return message;
    }
    public SendDocument sendDocument(Long chatId, String text) {
        SendDocument message = new SendDocument();
        message.setChatId(chatId.toString());
        message.setCaption(text);
        return message;
    }

    public SendMessage sendMessage(Long chatId, String text, ReplyKeyboardMarkup replyMarkup) {
        SendMessage message = sendMessage(chatId, text);
        message.setReplyMarkup(replyMarkup);
        return message;
    }

    public SendMessage handleReportsCommand(Long chatId) {
//        if (!userService.hasReportAccess(chatId)) {
//            return sendMessage(chatId, "Sizda hisobotlarga kirish huquqi mavjud emas.");
//        }

        InlineKeyboardMarkup keyboard = inlineKeyboardMarkupService.createReportTypesKeyboard();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Quyidagi hisobotlardan birini tanlang:");
        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    public SendMessage handleReportCallback(Long chatId, String callbackData) {
        ReportType reportType = getReportType(callbackData);
        if (reportType == null) {
            return sendMessage(chatId, "Noto‘g‘ri tanlov qilindi.");
        }

        switch (reportType) {
            case REPORT_MONTHLY_INCOME:
                return askForAdditionalFilters(chatId, "Oylik daromadlar hisobotini tanladingiz.");
            case REPORT_MONTHLY_EXPENSES:
                return askForAdditionalFilters(chatId, "Oylik xarajatlar hisobotini tanladingiz.");
            case REPORT_CUSTOM:
                return askForAdditionalFilters(chatId, "Qo‘shimcha hisobotlarni tanladingiz.");
            default:
                return sendMessage(chatId, "Noto‘g‘ri tanlov qilindi.");
        }
    }

    private ReportType getReportType(String callbackData) {
        for (ReportType reportType : ReportType.values()) {
            if (reportType.getValue().equals(callbackData)) {
                return reportType;
            }
        }
        return null;
    }

    private SendMessage askForAdditionalFilters(Long chatId, String reportMessage) {
        if (!validateReportMessage(reportMessage)) {
            return sendMessage(chatId, "Noto‘g‘ri filtr parametrlari kiritildi.");
        }

        String message = reportMessage + "\nIltimos, quyidagi filtrlarni tanlang yoki davrni kiriting:";
        InlineKeyboardMarkup keyboard = inlineKeyboardMarkupService.createFilterKeyboard();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(keyboard);
        return sendMessage;
    }

    private boolean validateReportMessage(String reportMessage) {
        return reportMessage != null && !reportMessage.trim().isEmpty();
    }

    public SendDocument generateAndSendReport(Long chatId, String reportType, Map<String, String> filters) {
        try {
            ByteArrayOutputStream excelFile = reportService.generateReport(reportType, filters);
            InputFile inputFile = new InputFile();
            inputFile.setMedia(new ByteArrayInputStream(excelFile.toByteArray()), reportType + ".xlsx");
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(inputFile);
            sendDocument.setCaption("Sizning hisobot faylingiz tayyor:");
            return sendDocument;
        } catch (Exception e) {
            return sendDocument(chatId, "Hisobotni yaratishda xatolik yuz berdi. Iltimos, keyinroq qayta urinib ko‘ring.");
        }
    }
    public SendMessage handleAlreadyRegisteredMessage(Long chatId) {
        return sendMessage(chatId, "Siz allaqachon ro‘yxatdan o‘tgan siz.");
    }
}
