package com.clinicavidasalud.controller.api;

import com.clinicavidasalud.entity.Rol;
import com.clinicavidasalud.entity.Usuario;
import com.clinicavidasalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsuarioApiController {

    private final UsuarioRepository usuarioRepository;

    // Obtener todos los usuarios
    @GetMapping("/usuarios")
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    // Crear un nuevo usuario
    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getEmail() == null || usuario.getPassword() == null) {
            return ResponseEntity.badRequest().body("Datos incompletos");
        }

        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    // Actualizar un usuario existente
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Optional<Usuario> optional = usuarioRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario existente = optional.get();
        existente.setNombre(usuarioActualizado.getNombre());
        existente.setEmail(usuarioActualizado.getEmail());
        existente.setRol(usuarioActualizado.getRol());

        if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isBlank()) {
            existente.setPassword(usuarioActualizado.getPassword());
        }

        usuarioRepository.save(existente);
        return ResponseEntity.ok().build();
    }

    // Eliminar un usuario
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/medicos")
    public List<Usuario> obtenerMedicos() {
        return usuarioRepository.findByRol(Rol.MEDICO);
    }

}
