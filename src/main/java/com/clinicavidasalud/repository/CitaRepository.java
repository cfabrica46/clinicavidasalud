package com.clinicavidasalud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinicavidasalud.model.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {}
