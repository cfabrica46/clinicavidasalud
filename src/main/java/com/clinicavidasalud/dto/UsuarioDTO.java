package com.clinicavidasalud.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String confirmar;
}
