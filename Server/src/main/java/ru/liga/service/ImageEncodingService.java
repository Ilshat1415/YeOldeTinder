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
        String description = user.getDescription();
        int x = 50;
        int y = 100;
        int lineWidth = 526;

        ImgWriter imgWriter = new ImgWriter();

        String headline;
        String text = "";
        if (description.contains(" ")) {
            headline = description.substring(0, description.indexOf(' '));
            text = description.substring(description.indexOf(' ') + 1);
        } else {
            headline = description;
        }

        float sizeDescription = 32f;
        if (text.length() > 128) {
            sizeDescription -= (text.length() - 128) / 69f;
        }
        float sizeHeadline = sizeDescription * 2f;

        imgWriter = imgWriter.setColor(Color.BLACK)
                .setFont(Font.decode("Old Standard TT").deriveFont(Font.BOLD, sizeHeadline))
                .write(headline, x, y)
                .setFont(Font.decode("Old Standard TT").deriveFont(Font.PLAIN, sizeDescription));

        int numberOfCharacters = (int) (2 * lineWidth / sizeDescription);
        y += (int) sizeDescription * 1.5;
        while (true) {
            if (text.length() > numberOfCharacters && text.contains(" ")) {
                int index = text.lastIndexOf(' ', numberOfCharacters);
                imgWriter = imgWriter.write(text.substring(0, index), x, y);
                text = text.substring(index + 1);
            } else {
                imgWriter = imgWriter.write(text, x, y);
                break;
            }
            y += (int) sizeDescription * 1.15;
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
