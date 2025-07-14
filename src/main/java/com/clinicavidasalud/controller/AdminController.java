
package com.clinicavidasalud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/home")
    public String homePaciente() {
        return "admin/home";
    }
}
