package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.entity.Cita;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.entity.HorarioAtencion;

import com.clinicavidasalud.repository.CitaRepository;
import com.clinicavidasalud.repository.UsuarioRepository;
import com.clinicavidasalud.repository.HorarioAtencionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CitaApiController {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioAtencionRepository horarioAtencionRepository;

    @GetMapping("/horarios")
    public List<String> getHorariosDisponibles(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // Convertir día a español (en mayúsculas)
        String diaSemana = fecha.getDayOfWeek()
                                .getDisplayName(java.time.format.TextStyle.FULL, new Locale("es", "ES"))
                                .toUpperCase(); // Ej: "MARTES"

        List<HorarioAtencion> horarios = horarioAtencionRepository
            .findByUsuarioIdAndDiaSemanaAndActivoTrue(medicoId, diaSemana);

        List<Cita> citasDelDia = citaRepository.findByMedicoIdAndFecha(medicoId, fecha);
        Set<LocalTime> ocupados = citasDelDia.stream().map(Cita::getHora).collect(Collectors.toSet());

        List<String> disponibles = new ArrayList<>();

        for (HorarioAtencion ha : horarios) {
            LocalTime inicio = ha.getHoraInicio();
            LocalTime fin = ha.getHoraFin();

            while (!inicio.isAfter(fin.minusMinutes(59))) {
                if (!ocupados.contains(inicio)) {
                    disponibles.add(inicio.toString());
                }
                inicio = inicio.plusHours(1);
            }
        }

        return disponibles;
    }

    @PostMapping("/citas")
    public ResponseEntity<?> agendarCita(@RequestBody Cita cita) {
        if (cita.getEspecialidad() == null || cita.getEspecialidad().getId() == null) {
            return ResponseEntity.badRequest().body("Especialidad requerida");
        }

        Optional<Usuario> pacienteOpt = usuarioRepository.findById(cita.getPaciente().getId());
        if (pacienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Paciente no encontrado");
        }
        cita.setPaciente(pacienteOpt.get());

        Optional<Usuario> medicoOpt = usuarioRepository.findById(cita.getMedico().getId());
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Médico no encontrado");
        }
        cita.setMedico(medicoOpt.get());

        cita.setEstado("PENDIENTE");

        citaRepository.save(cita);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/horarios/disponibles/{medicoId}")
    public ResponseEntity<List<String>> obtenerDiasDisponibles(@PathVariable Long medicoId) {
        List<HorarioAtencion> horarios = horarioAtencionRepository.findByUsuarioIdAndActivoTrue(medicoId);

        List<String> diasEnIngles = horarios.stream()
            .map(horario -> convertirADiaIngles(horario.getDiaSemana()))
            .distinct()
            .toList();

        return ResponseEntity.ok(diasEnIngles);
    }

    // Traduce "LUNES" → "monday", etc.
    private String convertirADiaIngles(String diaEspanol) {
        return switch (diaEspanol.toUpperCase()) {
            case "LUNES"     -> "monday";
            case "MARTES"    -> "tuesday";
            case "MIERCOLES" -> "wednesday";
            case "JUEVES"    -> "thursday";
            case "VIERNES"   -> "friday";
            case "SABADO"    -> "saturday";
            case "DOMINGO"   -> "sunday";
            default -> throw new IllegalArgumentException("Día no válido: " + diaEspanol);
        };
    }
}
