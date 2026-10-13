package com.campuscart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPagesController {

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/marketplace-rules")
    public String marketplaceRules() {
        return "marketplace-rules";
    }
}
