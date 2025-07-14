package com.clinicavidasalud.repository;

import com.clinicavidasalud.entity.HorarioAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HorarioAtencionRepository extends JpaRepository<HorarioAtencion, Long> {
    List<HorarioAtencion> findByUsuarioIdAndDiaSemanaAndActivoTrue(Long usuarioId, String diaSemana);
    List<HorarioAtencion> findByUsuarioIdAndActivoTrue(Long usuarioId);
    Optional<HorarioAtencion> findByUsuarioIdAndDiaSemana(Long usuarioId, String diaSemana);

}
