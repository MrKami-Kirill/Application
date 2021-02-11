package main.rest.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
public class DefaultController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }
}
