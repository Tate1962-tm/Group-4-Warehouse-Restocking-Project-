package com.awrs.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/login", "/app"})
    public String home() {
        return "forward:/index.html";
    }
}
