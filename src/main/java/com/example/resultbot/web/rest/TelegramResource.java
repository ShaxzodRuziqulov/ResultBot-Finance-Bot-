package com.example.resultbot.web.rest;

import com.example.resultbot.service.botservice.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/telegram")
public class TelegramResource {
    private final TelegramService telegramService;

    public TelegramResource(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleUpdate(@RequestBody Update update) {
        BotApiMethod<?> response = telegramService.onWebhookUpdateReceived(update);
        if (response != null) {
            return ResponseEntity.ok("OK");

        }
        return ResponseEntity.badRequest().body("Update not processed");
    }
}

