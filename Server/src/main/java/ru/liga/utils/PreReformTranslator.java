package ru.liga.utils;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class PreReformTranslator {

    private Pattern iPattern = Pattern.compile("(и(?=[ауоыэяюёей]))");
    private Pattern erPattern = Pattern.compile("((\\p{L}+)([[а-я]&&[^ауоыэяюёей]])\\b)");
    private final Map<String, String> yatMap;

    public PreReformTranslator() throws IOException {
        yatMap = getYatFromMap("/yatReplace.txt");
    }

    private String iRule(String toCheck) {
        return toCheck.replaceAll(iPattern.pattern(), "i");
    }

    private String erRule(String toCheck) {
        return toCheck.replaceAll(erPattern.pattern(), "$1ъ");
    }

    private String fiRule(String toCheck) {
        return toCheck.replace("Ф", "Ѳ")
                .replace("ф", "ѳ");
    }

    private String yatRule(String toCheck) {
        String[] checkList = toCheck.split(" +");

        for(String s : checkList) {
            String tmp = s.replaceAll("([^\\p{L}])", "");
            String rs = yatMap.get(tmp);
            if (rs != null) {
                toCheck = toCheck.replace(tmp, rs);
            }
        }

        return toCheck;
    }

    private Map<String, String> getYatFromMap(String path) throws IOException {
        try (BufferedReader bufferedReader =
                     new BufferedReader(
                             new InputStreamReader(
                                     this.getClass().getResourceAsStream(path), StandardCharsets.UTF_8))) {
            Map<String, String> yatMap = new HashMap<>();
            while (bufferedReader.ready()) {
                String s = bufferedReader.readLine();
                String[] spl = s.split(":");
                if (spl.length >= 2) {
                    yatMap.put(spl[0], spl[1]);
                }
            }
            return yatMap;
        }
    }

    public String translate(String str) {
        return iRule(erRule(yatRule(str)));
    }

    public String translateName(String str) {
        return fiRule(translate(str));
    }

}
