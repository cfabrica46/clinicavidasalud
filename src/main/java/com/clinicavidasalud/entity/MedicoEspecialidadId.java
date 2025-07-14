package com.clinicavidasalud.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MedicoEspecialidadId implements Serializable {

    private Long medicoId;
    private Long especialidadId;

    // Getters, setters, equals y hashCode
    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Long getEspecialidadId() {
        return especialidadId;
    }

    public void setEspecialidadId(Long especialidadId) {
        this.especialidadId = especialidadId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MedicoEspecialidadId)) return false;
        MedicoEspecialidadId that = (MedicoEspecialidadId) o;
        return Objects.equals(medicoId, that.medicoId) &&
               Objects.equals(especialidadId, that.especialidadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicoId, especialidadId);
    }
}
