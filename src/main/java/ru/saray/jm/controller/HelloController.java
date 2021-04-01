package ru.saray.jm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HelloController {

    @GetMapping("/")
    public String welcomePage(Model model) {
        List<String> messages = new ArrayList<>();
        messages.add("Hello! My name is Rimma");
        messages.add("It's my first Spring MVC + Spring Security application in non-boot project");
        messages.add("Click on the link below to log in");
        messages.add("If you wanna check admin page - username: ADMIN, password: ADMIN");
        messages.add("If you wanna check one of user's page - username: USER, password: USER");
        model.addAttribute("messages", messages);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
