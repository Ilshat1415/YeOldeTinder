package ru.liga.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.liga.entities.User;
import ru.liga.utils.ImgWriter;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Сервис создания закодированных образов анкет
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageEncodingService {
    /**
     * Утилита для втраивания текста в картинку.
     */
    private final ImgWriter imgWriter;

    /**
     * Кодирование описания пользователя в виде образа в формате Base64.
     *
     * @param user пользователь
     */
    public void encodeTheDescription(User user) {
        String description = user.getDescription();

        String headline;
        String text = "";
        if (description.contains(" ")) {
            headline = description.substring(0, description.indexOf(' '));
            text = description.substring(description.indexOf(' ') + 1);

        } else {
            headline = description;
        }

        try {
            File file = File.createTempFile(String.valueOf(user.getId()), ".jpg");
            ImageIO.write(imgWriter.createImageByDescription(headline, text), "jpg", file);

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] bytes = new byte[(int) file.length()];
                fileInputStream.read(bytes);

                user.setDescription(Base64.getEncoder().encodeToString(bytes));
            }
        } catch (IOException e) {
            log.error("Ошибка {}. Создание временного файла. Пользователь: {}",
                    e.getMessage(), user.getId(), e);
        }
    }
}
