package co.edu.usco.gestion_asignaturas.service;

import co.edu.usco.gestion_asignaturas.entity.ConsultaMedica;
import co.edu.usco.gestion_asignaturas.entity.Rol;
import co.edu.usco.gestion_asignaturas.entity.Usuario;
import co.edu.usco.gestion_asignaturas.repository.ConsultaMedicaRepository;
import co.edu.usco.gestion_asignaturas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.List;

@Service
public class ConsultaMedicaService {

    private final ConsultaMedicaRepository consultaMedicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ConsultaMedicaService(ConsultaMedicaRepository consultaMedicaRepository,
                                 UsuarioRepository usuarioRepository,
                                 PasswordEncoder passwordEncoder) {
        this.consultaMedicaRepository = consultaMedicaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<ConsultaMedica> listarTodas() {
        return consultaMedicaRepository.findAllByOrderByHoraInicioAsc();
    }

    @Transactional(readOnly = true)
    public List<ConsultaMedica> listarPorMedico(String username) {
        return consultaMedicaRepository.findByMedicoOrderByHoraInicioAsc(obtenerUsuarioPorUsername(username));
    }

    @Transactional(readOnly = true)
    public List<ConsultaMedica> listarPorPaciente(String username) {
        return consultaMedicaRepository.findByPacienteOrderByHoraInicioAsc(obtenerUsuarioPorUsername(username));
    }

    @Transactional(readOnly = true)
    public ConsultaMedica obtenerPorId(Long id) {
        return consultaMedicaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro la consulta medica solicitada."));
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarMedicos() {
        return usuarioRepository.findByRolOrderByNombreCompletoAsc(Rol.MEDICO);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPacientes() {
        return usuarioRepository.findByRolOrderByNombreCompletoAsc(Rol.PACIENTE);
    }

    @Transactional
    public ConsultaMedica crear(ConsultaMedica consultaMedica, Long medicoId, Long pacienteId) {
        Usuario paciente = obtenerUsuarioPorRol(pacienteId, Rol.PACIENTE, "paciente");
        consultaMedica.setPaciente(paciente);
        consultaMedica.setNombrePaciente(paciente.getNombreCompleto());
        consultaMedica.setMedico(obtenerUsuarioPorRol(medicoId, Rol.MEDICO, "medico"));
        validarHorario(consultaMedica.getHoraInicio(), consultaMedica.getHoraFin());
        return consultaMedicaRepository.save(consultaMedica);
    }

    @Transactional
    public ConsultaMedica actualizar(Long id, ConsultaMedica datos, Long medicoId, Long pacienteId) {
        ConsultaMedica existente = obtenerPorId(id);
        Usuario paciente = obtenerUsuarioPorRol(pacienteId, Rol.PACIENTE, "paciente");
        existente.setNombrePaciente(paciente.getNombreCompleto());
        existente.setPaciente(paciente);
        existente.setMotivoConsulta(datos.getMotivoConsulta());
        existente.setNumeroConsultorio(datos.getNumeroConsultorio());
        existente.setHoraInicio(datos.getHoraInicio());
        existente.setHoraFin(datos.getHoraFin());
        existente.setMedico(obtenerUsuarioPorRol(medicoId, Rol.MEDICO, "medico"));
        validarHorario(existente.getHoraInicio(), existente.getHoraFin());
        return consultaMedicaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        consultaMedicaRepository.delete(obtenerPorId(id));
    }

    @Transactional
    public ConsultaMedica actualizarHorarioPaciente(Long id, String username, LocalTime horaInicio, LocalTime horaFin) {
        ConsultaMedica consulta = obtenerPorId(id);
        validarHorario(horaInicio, horaFin);
        if (!consulta.getPaciente().getUsername().equals(username)) {
            throw new IllegalStateException("Solo puedes actualizar horarios de tus propias citas.");
        }
        consulta.setHoraInicio(horaInicio);
        consulta.setHoraFin(horaFin);
        return consultaMedicaRepository.save(consulta);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarMedicosParaAdministracion() {
        return usuarioRepository.findByRolOrderByNombreCompletoAsc(Rol.MEDICO);
    }

    @Transactional
    public Usuario crearMedico(Usuario medico, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contrasena del medico es obligatoria.");
        }
        medico.setRol(Rol.MEDICO);
        medico.setPassword(passwordEncoder.encode(rawPassword));
        return usuarioRepository.save(medico);
    }

    @Transactional
    public Usuario actualizarMedico(Long id, Usuario datos, String rawPassword) {
        Usuario medico = obtenerUsuarioPorRol(id, Rol.MEDICO, "medico");
        medico.setNombreCompleto(datos.getNombreCompleto());
        medico.setUsername(datos.getUsername());
        if (rawPassword != null && !rawPassword.isBlank()) {
            medico.setPassword(passwordEncoder.encode(rawPassword));
        }
        return usuarioRepository.save(medico);
    }

    @Transactional
    public void eliminarMedico(Long id) {
        Usuario medico = obtenerUsuarioPorRol(id, Rol.MEDICO, "medico");
        if (consultaMedicaRepository.existsByMedico(medico)) {
            throw new IllegalStateException("No puedes eliminar un medico con consultas asignadas.");
        }
        usuarioRepository.delete(medico);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerMedico(Long id) {
        return obtenerUsuarioPorRol(id, Rol.MEDICO, "medico");
    }

    private Usuario obtenerUsuarioPorRol(Long id, Rol rol, String etiqueta) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el " + etiqueta + " seleccionado."));
        if (usuario.getRol() != rol) {
            throw new IllegalArgumentException("El usuario seleccionado no tiene rol " + rol.name() + ".");
        }
        return usuario;
    }

    private Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro el usuario autenticado."));
    }

    private void validarHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null || !horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de finalizacion debe ser posterior a la hora de inicio.");
        }
    }
}
