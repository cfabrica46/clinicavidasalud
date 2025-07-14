package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.dto.HorarioDTO;
import com.clinicavidasalud.entity.HorarioAtencion;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.EspecialidadRepository;
import com.clinicavidasalud.repository.HorarioAtencionRepository;
import com.clinicavidasalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/horarios")
@RequiredArgsConstructor
public class HorarioAtencionApiController {

    private final HorarioAtencionRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadRepository especialidadRepository;

    @GetMapping
    public List<HorarioAtencion> listarPorMedico(@RequestParam Long medicoId) {
        return horarioRepository.findByUsuarioIdAndActivoTrue(medicoId);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody HorarioAtencion horario) {
        horario.setActivo(true);
        return ResponseEntity.ok(horarioRepository.save(horario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        horarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
