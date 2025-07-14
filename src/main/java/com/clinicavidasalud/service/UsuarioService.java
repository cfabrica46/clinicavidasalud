package com.clinicavidasalud.service;

import com.clinicavidasalud.dto.UsuarioDTO;
import com.clinicavidasalud.entity.Rol;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public boolean registrarUsuario(UsuarioDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            return false; // Email ya registrado
        }

        if (!dto.getPassword().equals(dto.getConfirmar())) {
                return false;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Rol.PACIENTE); // Asignar el rol por defecto

        usuarioRepository.save(usuario);
        return true;
    }
}
