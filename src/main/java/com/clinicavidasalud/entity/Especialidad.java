package com.clinicavidasalud.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "especialidad")
    private List<MedicoEspecialidad> medicoEspecialidades;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
