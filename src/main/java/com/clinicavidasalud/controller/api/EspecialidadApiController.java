package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.entity.Especialidad;
import com.clinicavidasalud.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/especialidades")
@RequiredArgsConstructor
public class EspecialidadApiController {

    private final EspecialidadRepository especialidadRepository;

    // Obtener todas las especialidades
    @GetMapping
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepository.findAll();
    }

    // Crear nueva especialidad
    @PostMapping
    public ResponseEntity<?> crearEspecialidad(@RequestBody Especialidad especialidad) {
        if (especialidad.getNombre() == null || especialidad.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es obligatorio.");
        }
        especialidadRepository.save(especialidad);
        return ResponseEntity.ok().build();
    }

    // Actualizar especialidad
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEspecialidad(@PathVariable Long id, @RequestBody Especialidad nueva) {
        Optional<Especialidad> optional = especialidadRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Especialidad existente = optional.get();
        existente.setNombre(nueva.getNombre());
        especialidadRepository.save(existente);

        return ResponseEntity.ok().build();
    }

    // Eliminar especialidad
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEspecialidad(@PathVariable Long id) {
        if (!especialidadRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        especialidadRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
