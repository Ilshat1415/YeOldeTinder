package ru.liga.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Переводчик на старословянский язык.
 */
@Slf4j
@Service
public class PreReformTranslator {
    /**
     * Путь к файлу yatReplace.
     */
    private static final String YAT_REPLACE_PATH = "/data/yatReplace.txt";
    /**
     * Паттерн для вставки i.
     */
    private static final Pattern I_PATTERN = Pattern.compile("(и(?=[ауоыэяюёей]))");
    /**
     * Паттерн для вставки ер.
     */
    private static final Pattern ER_PATTERN = Pattern.compile(
            "([цкнгшщзхфвпрлджчсмтбЦКНГШЩЗХФВПРЛДЖЧСМТБ])(?=[^а-яА-ЯѣiѲѳ]|$)"
    );
    /**
     * Карта слов с ять.
     */
    private final Map<String, String> yatMap = new HashMap<>();

    public PreReformTranslator() {
        fillMap(YAT_REPLACE_PATH);

        log.info("Переводчик готов к работе.");
    }

    /**
     * Перевод имени.
     *
     * @param name имя
     * @return переведённое имя
     */
    public String translateName(String name) {
        return fiRule(translate(name));
    }

    /**
     * Переводит текст.
     *
     * @param text текст
     * @return переведённый текст
     */
    public String translate(String text) {
        return iRule(erRule(yatRule(text)));
    }

    /**
     * Считывает в карту данные с файла.
     *
     * @param path путь к файлу
     * @return карта слов с ять
     */
    private void fillMap(String path) {
        try (InputStreamReader reader = new InputStreamReader(
                this.getClass().getResourceAsStream(path),
                StandardCharsets.UTF_8
        );
             BufferedReader bufferedReader = new BufferedReader(reader)) {

            while (bufferedReader.ready()) {
                String s = bufferedReader.readLine();
                String[] spl = s.split(":");

                if (spl.length >= 2) {
                    yatMap.put(spl[0], spl[1]);
                }
            }
        } catch (IOException e) {
            log.error("Ошибка {}. Чтение файла yatReplace", e.getMessage(), e);
        }
    }

    /**
     * Подстановка ять для определённых слов.
     *
     * @param toCheck проверяемый текст
     * @return результат перевода
     */
    private String yatRule(String toCheck) {
        String text = toCheck;
        for (String word : yatMap.keySet()) {
            text = text.replaceAll(word, yatMap.get(word));
            text = text.replaceAll(
                    word.substring(0, 1).toUpperCase() + word.substring(1),
                    yatMap.get(word).substring(0, 1).toUpperCase() + yatMap.get(word).substring(1)
            );
        }
        return text;
    }

    /**
     * Подстановка ер для определённых слов.
     *
     * @param toCheck проверяемый текст
     * @return результат перевода
     */
    private String erRule(String toCheck) {
        return toCheck.replaceAll(ER_PATTERN.pattern(), "$1ъ");
    }

    /**
     * Подстановка i для определённых слов.
     *
     * @param toCheck проверяемый текст
     * @return результат перевода
     */
    private String iRule(String toCheck) {
        return toCheck.replaceAll(I_PATTERN.pattern(), "i");
    }

    /**
     * Замена ф на ѳ для имен.
     *
     * @param toCheck проверяемый текст
     * @return результат перевода
     */
    private String fiRule(String toCheck) {
        return toCheck.replace("Ф", "Ѳ")
                .replace("ф", "ѳ");
    }
}
