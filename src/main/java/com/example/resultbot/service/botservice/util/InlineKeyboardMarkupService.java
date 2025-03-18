package com.example.resultbot.service.botservice.util;

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


    public InlineKeyboardMarkup getReportDateSelectionKeyboard(String reportType) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(createInlineButton("📅 Bugungacha", "REPORT_PERIOD_TILL_TODAY_" + reportType)));
        rows.add(List.of(createInlineButton("📆 Oxirgi 1 oy", "REPORT_PERIOD_LAST_1_MONTH_" + reportType)));
        rows.add(List.of(createInlineButton("📆 Oxirgi 3 oy", "REPORT_PERIOD_LAST_3_MONTHS_" + reportType)));
        rows.add(List.of(createInlineButton("📆 Oxirgi 1 yil", "REPORT_PERIOD_LAST_1_YEAR_" + reportType)));
        rows.add(List.of(createInlineButton("📅 Aniq sana", "REPORT_PERIOD_CUSTOM_DATE_" + reportType)));

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }


}
