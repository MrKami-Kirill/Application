package main.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class DefaultController {

    @RequestMapping("/")
    public String index(Model model) {
        log.info("Загрузка главной страницы сайта");
        return "index";
    }
}
