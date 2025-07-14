package com.clinicavidasalud.repository;

import com.clinicavidasalud.entity.NotaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotaClinicaRepository extends JpaRepository<NotaClinica, Long> {
    List<NotaClinica> findByPacienteId(Long pacienteId);
    List<NotaClinica> findByMedicoId(Long medicoId);
    Optional<NotaClinica> findByCitaId(Long citaId);
}