package com.clinicavidasalud.repository;

import com.clinicavidasalud.entity.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByMedicoIdAndFecha(Long medicoId, LocalDate fecha);
    List<Cita> findByPacienteId(Long pacienteId); // Puedes mantenerlo si lo usas en otro lado
    List<Cita> findByMedicoIdAndFechaAfter(Long medicoId, LocalDate fecha);
    List<Cita> findByMedicoIdAndFechaBefore(Long medicoId, LocalDate fecha);

    List<Cita> findByPacienteIdAndFecha(Long pacienteId, LocalDate fecha);
    List<Cita> findByPacienteIdAndFechaBefore(Long pacienteId, LocalDate fecha);
    List<Cita> findByPacienteIdAndFechaAfter(Long pacienteId, LocalDate fecha);

    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId AND c.fecha >= :fechaActual ORDER BY c.fecha ASC LIMIT 1")
    Cita findProximaCitaByPacienteId(@Param("pacienteId") Long pacienteId, @Param("fechaActual") LocalDate fechaActual);


}
