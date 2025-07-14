package com.clinicavidasalud.repository;

import com.clinicavidasalud.entity.MedicoEspecialidad;
import com.clinicavidasalud.entity.MedicoEspecialidadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoEspecialidadRepository extends JpaRepository<MedicoEspecialidad, MedicoEspecialidadId> {
    List<MedicoEspecialidad> findByEspecialidadId(Long especialidadId);
}
