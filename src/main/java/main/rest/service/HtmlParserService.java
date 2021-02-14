package main.rest.service;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class HtmlParserService {

    public static String parseStringFromHtml(String str) {
        if (str == null || str.isBlank()) {
            log.info("Строка пустая или не заполнена!");
            return null;
        }
        Document html = Jsoup.parse(str);
        String result = html.wholeText();
        log.info("Строка успешно спарсена в " + result);
        return result;
    }
}
