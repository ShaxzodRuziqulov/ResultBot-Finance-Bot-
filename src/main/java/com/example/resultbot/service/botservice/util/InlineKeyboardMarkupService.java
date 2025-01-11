package com.example.resultbot.service.botservice.util;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineKeyboardMarkupService {
    public InlineKeyboardMarkup createReportTypesKeyboard() {
        InlineKeyboardButton incomeButton = new InlineKeyboardButton("📈 Oylik daromadlar");
        incomeButton.setCallbackData("REPORT_MONTHLY_INCOME");

        InlineKeyboardButton expenseButton = new InlineKeyboardButton("📉 Oylik xarajatlar");
        expenseButton.setCallbackData("REPORT_MONTHLY_EXPENSES");

        InlineKeyboardButton customReportButton = new InlineKeyboardButton("📊 Qo‘shimcha hisobotlar");
        customReportButton.setCallbackData("REPORT_CUSTOM");

        return getInlineKeyboardMarkup(incomeButton, expenseButton, customReportButton);
    }



    public InlineKeyboardMarkup createFilterKeyboard() {
        InlineKeyboardButton dateFilter = new InlineKeyboardButton("📅 Davr bo‘yicha");
        dateFilter.setCallbackData("FILTER_DATE");

        InlineKeyboardButton categoryFilter = new InlineKeyboardButton("📂 Xarajat kategoriyasi");
        categoryFilter.setCallbackData("FILTER_CATEGORY");

        InlineKeyboardButton customerFilter = new InlineKeyboardButton("👤 Mijoz bo‘yicha");
        customerFilter.setCallbackData("FILTER_CUSTOMER");

        return getInlineKeyboardMarkup(dateFilter, categoryFilter, customerFilter);
    }
    private InlineKeyboardMarkup getInlineKeyboardMarkup(
            InlineKeyboardButton incomeButton,
            InlineKeyboardButton expenseButton,
            InlineKeyboardButton customReportButton) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(incomeButton));
        rows.add(List.of(expenseButton));
        rows.add(List.of(customReportButton));

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rows);

        return keyboardMarkup;
    }

}
