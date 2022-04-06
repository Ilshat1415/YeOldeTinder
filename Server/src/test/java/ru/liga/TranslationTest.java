package ru.liga;

import org.junit.jupiter.api.Test;
import ru.liga.utils.PreReformTranslator;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslationTest {

    @Test
    public void test() throws IOException {
        PreReformTranslator translator = new PreReformTranslator();
        String test = translator.translateName("федот Федот лем хрен ийкар");
        assertEquals(test, "ѳедотъ Ѳедотъ лемъ хренъ iйкаръ");
    }

}
