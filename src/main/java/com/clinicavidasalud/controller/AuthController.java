package com.clinicavidasalud.controller;

import com.clinicavidasalud.dto.UsuarioDTO;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.UsuarioRepository;
import com.clinicavidasalud.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrar(@ModelAttribute("usuario") UsuarioDTO usuarioDTO, Model model) {
        boolean registrado = usuarioService.registrarUsuario(usuarioDTO);
        if (!registrado) {
            model.addAttribute("error", "El correo ya está en uso");
            return "auth/register";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/redirect")
    public String redirigirPorRol(Authentication auth, HttpSession session) {

        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario == null) return "redirect:/auth/login";

        // ✅ Guardar info en la sesión manualmente
        session.setAttribute("idUsuario", usuario.getId());
        session.setAttribute("rol", usuario.getRol().name()); // si usas Enum
        session.setAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());

        switch (usuario.getRol()) {
            case PACIENTE: return "redirect:/paciente/home";
            case MEDICO: return "redirect:/medico/home";
            case ADMIN: return "redirect:/admin/home";
            default: return "redirect:/auth/login";
        }
    }
}
