package com.example.resultbot.web.rest;

import com.example.resultbot.service.botservice.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/telegram")
public class TelegramResource {
    private final TelegramService telegramService;

    public TelegramResource(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    private final Set<String> processedQueries = ConcurrentHashMap.newKeySet();

    private boolean isAlreadyProcessed(String callbackQueryId) {
        return processedQueries.contains(callbackQueryId);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleUpdate(@RequestBody Update update) {
        BotApiMethod<?> response = telegramService.onWebhookUpdateReceived(update);
        if (response != null) {
            return ResponseEntity.ok("OK");

        }
        return ResponseEntity.badRequest().body("Update not processed");
    }

    private void processCallbackQuery(CallbackQuery callbackQuery) {
        // Example: Send a response to the user
        String callbackData = callbackQuery.getData();
        String chatId = callbackQuery.getMessage().getChatId().toString();

        // Prevent recursion
        if ("recursive_call".equals(callbackData)) {
            System.out.println("Recursive call detected. Ignoring...");
            return;
        }

        // Handle valid callback data
        System.out.println("Processing callback data: " + callbackData);
    }
}

