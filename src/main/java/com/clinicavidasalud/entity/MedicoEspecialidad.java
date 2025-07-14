package com.clinicavidasalud.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medico_especialidad")
public class MedicoEspecialidad {

    @EmbeddedId
    private MedicoEspecialidadId id;

    @ManyToOne
    @MapsId("medicoId")
    @JoinColumn(name = "medico_id")
    private Usuario medico;

    @ManyToOne
    @MapsId("especialidadId")
    @JoinColumn(name = "especialidad_id")
    private Especialidad especialidad;

    // Getters y setters
    public MedicoEspecialidadId getId() {
        return id;
    }

    public void setId(MedicoEspecialidadId id) {
        this.id = id;
    }

    public Usuario getMedico() {
        return medico;
    }

    public void setMedico(Usuario medico) {
        this.medico = medico;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}
