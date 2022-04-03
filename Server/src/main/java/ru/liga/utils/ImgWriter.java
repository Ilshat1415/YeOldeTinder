package ru.liga.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ImgWriter {

    private static Font getFontFromResources(String fontPath) throws URISyntaxException, IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT,
                new File(ImgWriter.class.getResource(fontPath).toURI()));
    }

    static {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(getFontFromResources("/OldStandardTT-Regular.ttf"));
            ge.registerFont(getFontFromResources("/OldStandardTT-Bold.ttf"));
            ge.registerFont(getFontFromResources("/OldStandardTT-Italic.ttf"));
        } catch (URISyntaxException | IOException | FontFormatException e) {
            e.printStackTrace();
        };

    }

    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private final BufferedImage background;
    private BufferedImage img;
    private Graphics graphics;

    public ImgWriter() throws URISyntaxException, IOException {
        background = ImageIO.read(new File(this.getClass().getResource("/prerev-background.jpg").toURI()));
        cleanImage();
    }

    public ImgWriter cleanImage() {
        img = deepCopy(background);
        graphics = img.getGraphics();
        return this;
    }

    public ImgWriter setColor(Color color) {
        graphics.setColor(color);
        return this;
    }

    public ImgWriter setFont(Font font) {
        graphics.setFont(font);
        return this;
    }

    public Font getFont() {
        return graphics.getFont();
    }

    public ImgWriter write(String text, int x, int y) {
        graphics.drawString(text, x, y);
        return this;
    }

    public RenderedImage getImage() {
        return img;
    }

}
