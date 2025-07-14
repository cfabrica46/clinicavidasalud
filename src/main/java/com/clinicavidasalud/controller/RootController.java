package com.clinicavidasalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class RootController {

    @GetMapping("/")
    public String homeRoot() {
        return "dashboard";
    }
}