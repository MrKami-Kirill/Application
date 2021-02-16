package main.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HtmlParserService {
    
    public static String parseStringFromHtml(String str) {
        if (str == null || str.isBlank()) {
            log.warn("Строка пустая или не заполнена!");
            return null;
        }
        Document html = Jsoup.parse(str);
        String result = html.wholeText();
        log.info("Строка успешно спарсена в " + result);
        return result;
    }
}
