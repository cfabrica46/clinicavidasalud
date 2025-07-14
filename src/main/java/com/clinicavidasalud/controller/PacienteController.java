package com.clinicavidasalud.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinicavidasalud.entity.Cita;
import com.clinicavidasalud.entity.NotaClinica;
import com.clinicavidasalud.entity.Usuario;

import com.clinicavidasalud.repository.UsuarioRepository;
import com.clinicavidasalud.repository.CitaRepository;
import com.clinicavidasalud.repository.NotaClinicaRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PacienteController {

    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final NotaClinicaRepository notaClinicaRepository;

    @GetMapping("/home")
    public String homePaciente(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario != null) {
            model.addAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "paciente/home";
    }

    @GetMapping("/agenda")
    public String agendaPaciente(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        model.addAttribute("paciente", usuario);
        return "paciente/agendar-cita";
    }


    @GetMapping("/citas")
    public String verCitasPaciente(@RequestParam(value = "filtro", required = false, defaultValue = "hoy") String filtro,
                                Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario != null) {
            LocalDate hoy = LocalDate.now();
            List<Cita> citas;

            switch (filtro) {
                case "proximas":
                    citas = citaRepository.findByPacienteId(usuario.getId()).stream()
                            .filter(c -> c.getFecha().isAfter(hoy))
                            .toList();
                    break;
                case "pasadas":
                    citas = citaRepository.findByPacienteId(usuario.getId()).stream()
                            .filter(c -> c.getFecha().isBefore(hoy))
                            .toList();
                    break;
                default: // hoy
                    citas = citaRepository.findByPacienteId(usuario.getId()).stream()
                            .filter(c -> c.getFecha().isEqual(hoy))
                            .toList();
                    break;
            }

            model.addAttribute("citas", citas);
            model.addAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
            model.addAttribute("filtro", filtro);
            model.addAttribute("filtroLabel", switch (filtro) {
                case "proximas" -> "Próximas";
                case "pasadas" -> "Pasadas";
                default -> "Hoy";
            });

        }

        return "paciente/proximas-citas";
    }


    @GetMapping("/citas/exito")
    public String citaExitosa() {
        return "paciente/cita-exitosa";
    }

    @GetMapping("/historial")
    public String verHistorialPaciente(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario != null) {
            List<NotaClinica> historial = notaClinicaRepository.findByPacienteId(usuario.getId());
            model.addAttribute("historial", historial);
            model.addAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
        }
        return "paciente/historial";
    }


    @PostMapping("/citas/cancelar/{id}")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id, Authentication auth) {
        Optional<Cita> optCita = citaRepository.findById(id);
        if (optCita.isPresent()) {
            Cita cita = optCita.get();
            Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
            if (usuario != null && cita.getPaciente().getId().equals(usuario.getId())) {
                cita.setEstado("CANCELADA");
                citaRepository.save(cita);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
