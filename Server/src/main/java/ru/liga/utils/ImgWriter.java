package ru.liga.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Утилита для встраивания текста в картинку используя шрифт.
 */
@Slf4j
@Service
public class ImgWriter {
    /**
     * Путь к образцу шрифта OldStandardTT - Regular.
     */
    private static final String FONT_REGULAR_PATH = "/data/fonts/OldStandardTT-Regular.ttf";
    /**
     * Путь к образцу шрифта OldStandardTT - Bolt.
     */
    private static final String FONT_BOLT_PATH = "/data/fonts/OldStandardTT-Bold.ttf";
    /**
     * Путь к образцу шрифта OldStandardTT - Italic.
     */
    private static final String FONT_ITALIC_PATH = "/data/fonts/OldStandardTT-Italic.ttf";
    /**
     * Путь к фоновой картинке.
     */
    private static final String BACKGROUND_PATH = "/data/prerev-background.jpg";
    /**
     * Название шрифта.
     */
    private static final String FONT_NAME = "Old Standard TT";
    /**
     * Фоновая картинка.
     */
    private BufferedImage background;

    /**
     * Создание объекта, где регистрируются новые шрифты и считывается фоновая картинка.
     */
    public ImgWriter() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(getFontFromResources(FONT_REGULAR_PATH));
            ge.registerFont(getFontFromResources(FONT_BOLT_PATH));
            ge.registerFont(getFontFromResources(FONT_ITALIC_PATH));

            background = ImageIO.read(new File(this.getClass().getResource(BACKGROUND_PATH).toURI()));

            log.info("Утилита встраивания текста готов к работе.");
        } catch (URISyntaxException e) {
            log.error("Ошибка {}. Преобразование пути в URL", e.getMessage(), e);
        } catch (IOException e) {
            log.error("Ошибка {}. Чтение файла", e.getMessage(), e);
        } catch (FontFormatException e) {
            log.error("Ошибка {}. Создание шрифта", e.getMessage(), e);
        }
    }

    /**
     * Встраивает текст на картинку.
     *
     * @param headline заголовок
     * @param text     текст
     * @return картинку с текстом
     */
    public RenderedImage createImageByDescription(String headline, String text) {
        int x = 50;
        int y = 100;
        int lineWidth = 526;

        BufferedImage img = deepCopy(background);
        Graphics graphics = img.getGraphics();
        graphics.setColor(Color.BLACK);

        float sizeDescription = autoSizeFontByText(text);
        float sizeHeadline = 2f * sizeDescription;

        graphics.setFont(Font.decode(FONT_NAME).deriveFont(Font.BOLD, sizeHeadline));
        graphics.drawString(headline, x, y);
        graphics.setFont(Font.decode(FONT_NAME).deriveFont(Font.PLAIN, sizeDescription));

        y += (int) sizeDescription * 1.5;
        int numberOfCharacters = (int) (2 * lineWidth / sizeDescription);

        while (true) {
            if (text.length() > numberOfCharacters && text.contains(" ")) {
                int index = text.lastIndexOf(' ', numberOfCharacters);
                graphics.drawString(text.substring(0, index), x, y);

                text = text.substring(index + 1);

            } else {
                graphics.drawString(text, x, y);
                break;
            }
            y += (int) sizeDescription * 1.15;
        }

        return img;
    }

    /**
     * Создаёт новый шрифт.
     *
     * @param fontPath путь к файлу с шрифтом
     * @return шрифт
     * @throws URISyntaxException  если не возможно преобразовать в URL
     * @throws IOException         если не читается исходный файл
     * @throws FontFormatException если не удаётся создать шрифт
     */
    private Font getFontFromResources(String fontPath) throws URISyntaxException, IOException, FontFormatException {
        return Font.createFont(
                Font.TRUETYPE_FONT,
                new File(this.getClass().getResource(fontPath).toURI())
        );
    }

    /**
     * Создаёт копию картинки.
     *
     * @param bi исходная картинка
     * @return копия картинки
     */
    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);

        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Подбирает размер шрифта взависимости от длинны текста.
     *
     * @param text текст
     * @return размер шрифта
     */
    private float autoSizeFontByText(String text) {
        float size = 32f;

        if (text.length() > 128) {
            size -= (text.length() - 128) / 69f;
        }

        return size;
    }
}
