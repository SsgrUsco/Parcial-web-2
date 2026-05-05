package co.edu.usco.gestion_asignaturas.controller;

import co.edu.usco.gestion_asignaturas.entity.ConsultaMedica;
import co.edu.usco.gestion_asignaturas.entity.Usuario;
import co.edu.usco.gestion_asignaturas.service.ConsultaMedicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class ConsultaMedicaRestController {

    private final ConsultaMedicaService consultaMedicaService;

    public ConsultaMedicaRestController(ConsultaMedicaService consultaMedicaService) {
        this.consultaMedicaService = consultaMedicaService;
    }

    @GetMapping("/consultas")
    @Tag(name = "Consultas Medicas", description = "Servicios web para administrar consultas medicas.")
    @Operation(summary = "Listar consultas", description = "Obtiene todas las consultas medicas registradas. Solo ADMINISTRADOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultas listadas correctamente"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public List<ConsultaMedica> listarConsultas() {
        return consultaMedicaService.listarTodas();
    }

    @GetMapping("/consultas/{id}")
    @Operation(summary = "Obtener consulta", description = "Devuelve la informacion completa de una consulta medica por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ConsultaMedica obtenerConsulta(@PathVariable Long id) {
        return consultaMedicaService.obtenerPorId(id);
    }

    @PostMapping("/consultas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear consulta", description = "Registra una consulta medica con paciente, medico, consultorio y horario.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta creada"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ConsultaMedica crearConsulta(@Valid @RequestBody ConsultaMedica consulta,
                                        @RequestParam Long medicoId,
                                        @RequestParam Long pacienteId) {
        return consultaMedicaService.crear(consulta, medicoId, pacienteId);
    }

    @PutMapping("/consultas/{id}")
    @Operation(summary = "Actualizar consulta", description = "Modifica todos los datos de una consulta medica existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta actualizada"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ConsultaMedica actualizarConsulta(@PathVariable Long id,
                                             @Valid @RequestBody ConsultaMedica consulta,
                                             @RequestParam Long medicoId,
                                             @RequestParam Long pacienteId) {
        return consultaMedicaService.actualizar(id, consulta, medicoId, pacienteId);
    }

    @DeleteMapping("/consultas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar consulta", description = "Elimina una consulta medica por identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consulta eliminada"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public void eliminarConsulta(@PathVariable Long id) {
        consultaMedicaService.eliminar(id);
    }

    @PatchMapping("/consultas/{id}/horario")
    @Operation(summary = "Actualizar horario de la cita", description = "Permite que un PACIENTE actualice el horario de una cita asignada a el.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horario actualizado"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ConsultaMedica actualizarHorario(@PathVariable Long id,
                                            @Parameter(description = "Hora de inicio en formato HH:mm", required = true)
                                            @RequestParam @NotNull String horaInicio,
                                            @Parameter(description = "Hora de fin en formato HH:mm", required = true)
                                            @RequestParam @NotNull String horaFin,
                                            Authentication authentication) {
        return consultaMedicaService.actualizarHorarioPaciente(id, authentication.getName(),
                LocalTime.parse(horaInicio), LocalTime.parse(horaFin));
    }

    @GetMapping("/medicos")
    @Tag(name = "Medicos", description = "Servicios web para administrar y consultar medicos.")
    @Operation(summary = "Listar medicos", description = "Obtiene los medicos disponibles para asignar a una consulta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicos listados"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public List<Usuario> listarMedicos() {
        return consultaMedicaService.listarMedicosParaAdministracion();
    }

    @PostMapping("/medicos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear medico", description = "Registra un medico nuevo en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Medico creado"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Usuario crearMedico(@Valid @RequestBody Usuario medico,
                               @RequestParam String passwordPlano) {
        return consultaMedicaService.crearMedico(medico, passwordPlano);
    }

    @PutMapping("/medicos/{id}")
    @Operation(summary = "Actualizar medico", description = "Modifica nombre, usuario o contrasena de un medico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medico actualizado"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Usuario actualizarMedico(@PathVariable Long id,
                                    @Valid @RequestBody Usuario medico,
                                    @RequestParam(required = false) String passwordPlano) {
        return consultaMedicaService.actualizarMedico(id, medico, passwordPlano);
    }

    @DeleteMapping("/medicos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar medico", description = "Elimina un medico si no tiene consultas asignadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Medico eliminado"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public void eliminarMedico(@PathVariable Long id) {
        consultaMedicaService.eliminarMedico(id);
    }

    @GetMapping("/pacientes")
    @Tag(name = "Pacientes", description = "Servicios web para consultar pacientes registrados.")
    @Operation(summary = "Listar pacientes", description = "Obtiene los pacientes disponibles para asociar a una consulta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pacientes listados"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public List<Usuario> listarPacientes() {
        return consultaMedicaService.listarPacientes();
    }
}
