package com.dentalclinic.DentalClinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // This should be a Thymeleaf, JSP, or HTML file in templates/
    }
}
