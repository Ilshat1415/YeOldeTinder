package ru.liga.utils;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PreReformTranslator {

    private Pattern iPattern = Pattern.compile("и[ауоыиэяюёей]");
    private Pattern erPattern = Pattern.compile("[^\\sауоыиэяюёей]$");
    private final Map<String, String> yatMap;

    PreReformTranslator() throws IOException {
        yatMap = getYatFromMap("/yatReplace.txt");
    }

    private String iRule(String toCheck) {
        Matcher matcher = iPattern.matcher(toCheck);
        StringBuffer sb = new StringBuffer(toCheck);
        while (matcher.find()) {
            sb.replace(matcher.start(), matcher.start() + 1, "i");
        }
        return sb.toString();
    }

    private String erRule(String toCheck) {
        Matcher matcher = erPattern.matcher(toCheck);
        while (matcher.find()) {
            toCheck += "ъ";
        }
        return toCheck;
    }

    private String fiRule(String toCheck) {
        return toCheck.replace("ф", "ѳ");
    }

    private String yatRule(String toCheck) {
        String rs = yatMap.get(toCheck);
        if (rs == null) {
            return toCheck;
        }
        return rs;
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
