package com.clinicavidasalud.controller;

import com.clinicavidasalud.dto.CitaVistaDTO;

import com.clinicavidasalud.entity.Cita;
import com.clinicavidasalud.entity.NotaClinica;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.CitaRepository;
import com.clinicavidasalud.repository.NotaClinicaRepository;
import com.clinicavidasalud.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/medico")
@RequiredArgsConstructor
public class MedicoController {

    private final UsuarioRepository usuarioRepository;
    private final CitaRepository citaRepository;
    private final NotaClinicaRepository notaClinicaRepository;

    @GetMapping("/home")
    public String homeMedico(Model model, Authentication auth) {
        Usuario medico = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (medico != null) {
            model.addAttribute("nombreCompleto", medico.getNombre() + " " + medico.getApellido());
        }
        return "medico/home";
    }

    @GetMapping("/agenda")
    public String agendaMedico(
        @RequestParam(value = "filtro", defaultValue = "hoy") String filtro,
        Model model,
        Authentication auth
    ) {
        Usuario medico = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        List<Cita> citas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        switch (filtro) {
            case "proximas":
                citas = citaRepository.findByMedicoIdAndFechaAfter(medico.getId(), hoy);
                model.addAttribute("filtroLabel", "Próximas");
                break;
            case "pasadas":
                citas = citaRepository.findByMedicoIdAndFechaBefore(medico.getId(), hoy);
                model.addAttribute("filtroLabel", "Pasadas");
                break;
            default:
                citas = citaRepository.findByMedicoIdAndFecha(medico.getId(), hoy);
                model.addAttribute("filtroLabel", "Hoy");
        }

        List<CitaVistaDTO> citasDTO = new ArrayList<>();
        for (Cita cita : citas) {
            Optional<NotaClinica> notaOpt = notaClinicaRepository.findByCitaId(cita.getId());
            String especialidad;
            if (notaOpt.isPresent() && notaOpt.get().getEspecialidad() != null) {
                especialidad = notaOpt.get().getEspecialidad().getNombre();
            } else {
                especialidad = "No asignada";
            }

            citasDTO.add(new CitaVistaDTO(cita, especialidad));
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("citas", citasDTO);
        return "medico/agenda";
    }



    @GetMapping("/historial")
    public String verHistorialMedico(Model model, Authentication auth) {
        Usuario medico = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (medico != null) {
            List<NotaClinica> historial = notaClinicaRepository.findByMedicoId(medico.getId());
            model.addAttribute("historial", historial);
        }

        // Para que el filtro "Hoy" aparezca como seleccionado por defecto
        model.addAttribute("filtro", "hoy");
        model.addAttribute("filtroLabel", "Citas de Hoy");

        return "medico/historial";
    }


    @GetMapping("/nota/ver")
    public String verNotaClinica(@RequestParam("id") Long notaId, Model model) {
        NotaClinica nota = notaClinicaRepository.findById(notaId).orElse(null);
        if (nota == null) {
            return "redirect:/medico/historial"; // o una página de error si deseas
        }
        model.addAttribute("nota", nota);
        return "medico/ver-nota";
    }

    @GetMapping("/nota/nueva")
    public String mostrarFormularioNota(@RequestParam Long citaId, Model model) {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isEmpty()) {
            return "redirect:/medico/agenda?error=cita_no_encontrada";
        }

        Cita cita = citaOpt.get();

        // Buscar si ya existe una nota clínica para esta cita
        NotaClinica nota = notaClinicaRepository.findByCitaId(citaId).orElse(new NotaClinica());

        model.addAttribute("cita", cita);
        model.addAttribute("nota", nota);
        return "medico/nota-clinica-form"; // ruta de la vista que ahora te ayudaré a crear
    }

    @PostMapping("/nota/guardar")
    public String guardarNota(@ModelAttribute NotaClinica nota, @RequestParam Long citaId) {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isEmpty()) {
            return "redirect:/medico/agenda?error=cita_no_encontrada";
        }

        Cita cita = citaOpt.get();

        // Verificar si ya existe una nota clínica para esta cita
        Optional<NotaClinica> notaExistenteOpt = notaClinicaRepository.findByCitaId(citaId);

        NotaClinica notaGuardar;
        if (notaExistenteOpt.isPresent()) {
            // Actualizar la nota existente
            notaGuardar = notaExistenteOpt.get();
            notaGuardar.setDiagnostico(nota.getDiagnostico());
            notaGuardar.setObservaciones(nota.getObservaciones());
            notaGuardar.setReceta(nota.getReceta());
        } else {
            // Crear nueva nota
            nota.setCita(cita);
            nota.setPaciente(cita.getPaciente());
            nota.setMedico(cita.getMedico());
            nota.setFecha(LocalDate.now());
            notaGuardar = nota;
        }

        notaClinicaRepository.save(notaGuardar);

        // Marcar cita como atendida si aún no lo está
        if (!"ATENDIDA".equals(cita.getEstado())) {
            cita.setEstado("ATENDIDA");
            citaRepository.save(cita);
        }

        return "redirect:/medico/agenda?filtro=hoy";
    }

    @GetMapping("/horario")
    public String verGestionHorarios(HttpSession session, Model model) {
        Long medicoId = (Long) session.getAttribute("idUsuario");
        if (medicoId == null) {
            return "auth/login";
        }

        model.addAttribute("medicoId", medicoId);
        return "medico/horario"; // Asegúrate que este HTML esté en templates/medico/horario.html
    }

}
