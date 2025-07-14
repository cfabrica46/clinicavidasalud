package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.entity.MedicoEspecialidad;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.MedicoEspecialidadRepository;
import com.clinicavidasalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoApiController {

    private final MedicoEspecialidadRepository medicoEspecialidadRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Usuario> getMedicosByEspecialidad(@RequestParam Long especialidadId) {
        List<MedicoEspecialidad> relaciones = medicoEspecialidadRepository.findByEspecialidadId(especialidadId);


        return relaciones.stream()
            .map(rel -> usuarioRepository.findById(rel.getMedico().getId()).orElse(null))
            .filter(medico -> medico != null)
            .toList();

    }
}
