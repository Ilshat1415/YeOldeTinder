package ru.liga.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.liga.entities.User;
import ru.liga.utils.ImgWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

@Service
public class ImageEncodingService {

    @SneakyThrows
    public void encodeTheDescription(User user) {
        ImgWriter imgWriter = new ImgWriter();
        String text = user.getDescription().toLowerCase();
        String firstWord = text.substring(0, text.indexOf(' '));
        text = text.substring(text.indexOf(' ') + 1);

        float sizeHeadline = (float) Math.floor(Math.sqrt(60 * 250 * 2 / (1.2 * firstWord.length())));
        float sizeDescription = (float) Math.floor(Math.sqrt(426 * 526 * 2 / (1.2 * text.length())));
        int x = 50;
        int y = 100;

        imgWriter = imgWriter.setColor(Color.BLACK)
                .setFont(Font.decode("Old Standard TT").deriveFont(Font.BOLD, sizeHeadline))
                .write(firstWord, x, y)
                .setFont(Font.decode("Old Standard TT").deriveFont(Font.PLAIN, sizeDescription));

        int size = (int) (526 * 2 / sizeDescription);
        y += (int) sizeDescription * 1.5;
        while (true) {
            if (text.length() > size && text.contains(" ")) {
                imgWriter = imgWriter.write(text.substring(0, size), x, y);
                text = text.substring(size);
            } else {
                imgWriter = imgWriter.write(text, x, y);
                break;
            }
            y += (int) sizeDescription * 1.2;
        }

        File file = File.createTempFile(String.valueOf(user.getId()), ".jpg");
        ImageIO.write(imgWriter.getImage(), "jpg", file);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            String encodedFile = Base64.getEncoder().encodeToString(bytes);
            user.setDescription(encodedFile);
        } finally {
            file.delete();
        }
    }
}
