package co.edu.usco.gestion_asignaturas.repository;

import co.edu.usco.gestion_asignaturas.entity.ConsultaMedica;
import co.edu.usco.gestion_asignaturas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaMedicaRepository extends JpaRepository<ConsultaMedica, Long> {

    List<ConsultaMedica> findAllByOrderByHoraInicioAsc();

    List<ConsultaMedica> findByMedicoOrderByHoraInicioAsc(Usuario medico);

    List<ConsultaMedica> findByPacienteOrderByHoraInicioAsc(Usuario paciente);

    boolean existsByMedico(Usuario medico);
}
