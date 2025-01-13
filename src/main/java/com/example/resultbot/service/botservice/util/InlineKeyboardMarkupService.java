package com.example.resultbot.service.botservice.util;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineKeyboardMarkupService {
    public InlineKeyboardMarkup createReportsMenu() {
        InlineKeyboardButton monthlyIncomeButton = new InlineKeyboardButton("Oylik daromadlar");
        monthlyIncomeButton.setCallbackData("reports_monthly_income");

        InlineKeyboardButton monthlyExpenseButton = new InlineKeyboardButton("Oylik xarajatlar");
        monthlyExpenseButton.setCallbackData("reports_monthly_expense");

        InlineKeyboardButton additionalReportsButton = new InlineKeyboardButton("Qo‘shimcha hisobotlar");
        additionalReportsButton.setCallbackData("reports_additional");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(monthlyIncomeButton, monthlyExpenseButton));
        rows.add(List.of(additionalReportsButton));

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setKeyboard(rows);

        return inlineKeyboard;
    }

    public InlineKeyboardMarkup createSettingsMenu() {
        InlineKeyboardButton editProfileButton = new InlineKeyboardButton("Profil ma’lumotlarini o‘zgartirish");
        editProfileButton.setCallbackData("settings_edit_profile");

        InlineKeyboardButton viewAccessRightsButton = new InlineKeyboardButton("Joriy kirish huquqlarini ko‘rish");
        viewAccessRightsButton.setCallbackData("settings_view_access");

        InlineKeyboardButton changePasswordButton = new InlineKeyboardButton("Parolni o‘zgartirish");
        changePasswordButton.setCallbackData("settings_change_password");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(editProfileButton));
        rows.add(List.of(viewAccessRightsButton));
        rows.add(List.of(changePasswordButton));

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.setKeyboard(rows);

        return inlineKeyboard;
    }


    public InlineKeyboardMarkup createFilterKeyboard() {
        InlineKeyboardButton dateFilter = new InlineKeyboardButton("📅 Davr bo‘yicha");
        dateFilter.setCallbackData("FILTER_DATE");

        InlineKeyboardButton categoryFilter = new InlineKeyboardButton("📂 Xarajat kategoriyasi");
        categoryFilter.setCallbackData("FILTER_CATEGORY");

        InlineKeyboardButton customerFilter = new InlineKeyboardButton("👤 Mijoz bo‘yicha");
        customerFilter.setCallbackData("FILTER_CUSTOMER");
        InlineKeyboardButton periodFilter = new InlineKeyboardButton("Xizmat bo‘yicha");
        periodFilter.setCallbackData("FILTER_BY_SERVICE");
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
