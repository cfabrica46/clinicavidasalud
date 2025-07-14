package com.clinicavidasalud.config;

import com.clinicavidasalud.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    // 👤 Servicio que carga al usuario desde la base de datos
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmail(username)
            .map(usuario -> User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name()) // Usa nombre del enum como rol
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // 🔒 Codificador de contraseñas (BCrypt)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔐 Filtro principal de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/auth/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/paciente/**").hasRole("PACIENTE")
                .requestMatchers("/medico/**").hasRole("MEDICO")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login") // <- Página de login
                .loginProcessingUrl("/login") // <- Procesamiento del POST
                .failureUrl("/auth/login?error=true") // <- Redirección si falla
                .defaultSuccessUrl("/auth/redirect", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            );
        return http.build();
    }
}
