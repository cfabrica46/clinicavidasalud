package com.clinicavidasalud.dto;

import com.clinicavidasalud.entity.Cita;

public class CitaVistaDTO {
    private Long id;
    private String hora;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String estado;
    private String nombreEspecialidad;

    public CitaVistaDTO(Cita cita, String nombreEspecialidad) {
        this.id = cita.getId();
        this.hora = cita.getHora().toString();
        this.nombrePaciente = cita.getPaciente().getNombre();
        this.apellidoPaciente = cita.getPaciente().getApellido();
        this.estado = cita.getEstado().toString();
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public Long getId() { return id; }
    public String getHora() { return hora; }
    public String getNombrePaciente() { return nombrePaciente; }
    public String getApellidoPaciente() { return apellidoPaciente; }
    public String getEstado() { return estado; }
    public String getNombreEspecialidad() { return nombreEspecialidad; }
}
