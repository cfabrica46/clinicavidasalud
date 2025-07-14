package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.dto.HorarioDTO;
import com.clinicavidasalud.entity.HorarioAtencion;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.HorarioAtencionRepository;
import com.clinicavidasalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/horario")
@RequiredArgsConstructor
public class HorarioApiController {

    private final HorarioAtencionRepository horarioRepo;
    private final UsuarioRepository usuarioRepo;

    // ✅ Obtener todos los horarios del médico
    @GetMapping("/{medicoId}")
    public ResponseEntity<List<HorarioAtencion>> obtenerHorarios(@PathVariable Long medicoId) {
        List<HorarioAtencion> horarios = horarioRepo.findByUsuarioIdAndActivoTrue(medicoId);
        return ResponseEntity.ok(horarios);
    }

    // ➕ Agregar nuevo horario
    @PostMapping
    public ResponseEntity<?> agregarHorario(@RequestBody HorarioDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findById(dto.getMedicoId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }

        LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

        if (horaInicio.isAfter(horaFin) || horaInicio.equals(horaFin)) {
            return ResponseEntity.badRequest().body("La hora de inicio debe ser menor que la hora de fin.");
        }

        // Verificar si ya hay un horario para ese médico y día
        Optional<HorarioAtencion> existenteOpt = horarioRepo
            .findByUsuarioIdAndDiaSemana(dto.getMedicoId(), dto.getDiaSemana());

        HorarioAtencion horario;

        if (existenteOpt.isPresent()) {
            // Actualizamos el horario existente
            horario = existenteOpt.get();
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);
        } else {
            // Creamos uno nuevo
            horario = new HorarioAtencion();
            horario.setDiaSemana(dto.getDiaSemana());
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);
            horario.setUsuario(usuarioOpt.get());
            horario.setActivo(true);
        }

        HorarioAtencion guardado = horarioRepo.save(horario);
        return ResponseEntity.ok(guardado);
    }


    // 🗑️ Eliminar un horario (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarHorario(@PathVariable Long id) {
        if (!horarioRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        horarioRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ✏️ (Opcional) Actualizar horario existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHorario(@PathVariable Long id, @RequestBody HorarioAtencion nuevo) {
        Optional<HorarioAtencion> existenteOpt = horarioRepo.findById(id);
        if (existenteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        HorarioAtencion existente = existenteOpt.get();
        existente.setDiaSemana(nuevo.getDiaSemana());
        existente.setHoraInicio(nuevo.getHoraInicio());
        existente.setHoraFin(nuevo.getHoraFin());

        horarioRepo.save(existente);
        return ResponseEntity.ok(existente);
    }
}
