package com.clinicavidasalud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinicavidasalud.model.Cita;
import com.clinicavidasalud.service.CitaService;

@Controller
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping("/citas")
    public String listarCitas(Model model) {
        model.addAttribute("citas", citaService.obtenerTodas());
        return "citas/listar";
    }

    @PostMapping("/citas/crear")
    public String crearCita(@RequestParam String paciente, @RequestParam String fecha) {
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setFecha(fecha);
        citaService.guardar(cita);
        return "redirect:/citas";
    }
}
