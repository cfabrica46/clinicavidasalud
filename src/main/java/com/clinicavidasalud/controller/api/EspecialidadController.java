package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.entity.Especialidad;
import com.clinicavidasalud.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@RequiredArgsConstructor
public class EspecialidadController {

    private final EspecialidadRepository especialidadRepository;

    @GetMapping
    public List<Especialidad> obtenerTodas() {
        return especialidadRepository.findAll();
    }
}
