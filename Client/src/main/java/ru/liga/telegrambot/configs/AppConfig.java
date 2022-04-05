package ru.liga.telegrambot.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.liga.telegrambot.telegram.Bot;
import ru.liga.telegrambot.telegram.TelegramFacade;

@Configuration
public class AppConfig {
    private final BotConfig botConfig;

    public AppConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder()
                .url(botConfig.getBotPath())
                .build();
    }

    @Bean
    public Bot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        Bot bot = new Bot(setWebhook, telegramFacade);
        bot.setBotPath(botConfig.getBotPath());
        bot.setBotUsername(botConfig.getBotUsername());
        bot.setBotToken(botConfig.getBotToken());

        return bot;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
