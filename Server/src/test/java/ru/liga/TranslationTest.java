package ru.liga;

import org.junit.Test;
import ru.liga.utils.PreReformTranslator;

import static org.assertj.core.api.Assertions.assertThat;

public class TranslationTest {

    @Test
    public void test() {
        PreReformTranslator translator = new PreReformTranslator();
        String test = translator.translateName("федот Федот лем хрен ийкар");
        assertThat(test).isEqualTo("ѳедотъ Ѳедотъ лѣмъ хрѣнъ iйкаръ");
    }
}
