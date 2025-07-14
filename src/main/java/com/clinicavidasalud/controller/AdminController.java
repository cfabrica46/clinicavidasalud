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

    @GetMapping("/usuarios")
    public String usuarios() {
        return "admin/usuarios";
    }

    @GetMapping("/especialidad")
    public String especialidad() {
        return "admin/especialidades";
    }

    @GetMapping("/horario")
    public String horario() {
        return "admin/disponibilidad";
    }
}
