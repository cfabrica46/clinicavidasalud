package com.clinicavidasalud.dto;

import lombok.Data;

@Data
public class HorarioDTO {
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Long medicoId;
}
