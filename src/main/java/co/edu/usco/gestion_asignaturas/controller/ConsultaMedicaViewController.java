package co.edu.usco.gestion_asignaturas.controller;

import co.edu.usco.gestion_asignaturas.entity.ConsultaMedica;
import co.edu.usco.gestion_asignaturas.entity.Usuario;
import co.edu.usco.gestion_asignaturas.service.ConsultaMedicaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConsultaMedicaViewController {

    private final ConsultaMedicaService consultaMedicaService;

    public ConsultaMedicaViewController(ConsultaMedicaService consultaMedicaService) {
        this.consultaMedicaService = consultaMedicaService;
    }

    @GetMapping("/admin/consultas")
    public String listarParaAdministrador(Model model) {
        model.addAttribute("titulo", "Panel del Administrador");
        model.addAttribute("consultas", consultaMedicaService.listarTodas());
        model.addAttribute("vista", "ADMINISTRADOR");
        return "consultas/lista";
    }

    @GetMapping("/medico/consultas")
    public String listarParaMedico(Model model, Authentication authentication) {
        model.addAttribute("titulo", "Consultas del Medico");
        model.addAttribute("consultas", consultaMedicaService.listarPorMedico(authentication.getName()));
        model.addAttribute("vista", "MEDICO");
        return "consultas/lista";
    }

    @GetMapping("/paciente/consultas")
    public String listarParaPaciente(Model model, Authentication authentication) {
        model.addAttribute("titulo", "Citas del Paciente");
        model.addAttribute("consultas", consultaMedicaService.listarPorPaciente(authentication.getName()));
        model.addAttribute("vista", "PACIENTE");
        model.addAttribute("usuarioActual", authentication.getName());
        return "consultas/lista";
    }

    @GetMapping("/admin/consultas/nueva")
    public String mostrarFormularioNuevaConsulta(Model model) {
        cargarFormularioConsulta(model, new ConsultaMedica(), "Registrar consulta", "/admin/consultas");
        return "consultas/formulario";
    }

    @PostMapping("/admin/consultas")
    public String crearConsulta(@Valid @ModelAttribute("consulta") ConsultaMedica consulta,
                                BindingResult bindingResult,
                                @RequestParam(value = "medicoId", required = false) Long medicoId,
                                @RequestParam(value = "pacienteId", required = false) Long pacienteId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (medicoId == null || pacienteId == null) {
            model.addAttribute("error", "Debes seleccionar un medico y un paciente.");
            cargarFormularioConsulta(model, consulta, "Registrar consulta", "/admin/consultas");
            return "consultas/formulario";
        }
        if (bindingResult.hasErrors()) {
            cargarFormularioConsulta(model, consulta, "Registrar consulta", "/admin/consultas");
            return "consultas/formulario";
        }
        try {
            consultaMedicaService.crear(consulta, medicoId, pacienteId);
            redirectAttributes.addFlashAttribute("mensaje", "Consulta medica creada correctamente.");
            return "redirect:/admin/consultas";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormularioConsulta(model, consulta, "Registrar consulta", "/admin/consultas");
            return "consultas/formulario";
        }
    }

    @GetMapping("/admin/consultas/{id}/editar")
    public String mostrarFormularioEditarConsulta(@PathVariable Long id, Model model) {
        cargarFormularioConsulta(model, consultaMedicaService.obtenerPorId(id), "Editar consulta", "/admin/consultas/" + id);
        return "consultas/formulario";
    }

    @PostMapping("/admin/consultas/{id}")
    public String actualizarConsulta(@PathVariable Long id,
                                     @Valid @ModelAttribute("consulta") ConsultaMedica consulta,
                                     BindingResult bindingResult,
                                     @RequestParam(value = "medicoId", required = false) Long medicoId,
                                     @RequestParam(value = "pacienteId", required = false) Long pacienteId,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (medicoId == null || pacienteId == null) {
            model.addAttribute("error", "Debes seleccionar un medico y un paciente.");
            cargarFormularioConsulta(model, consulta, "Editar consulta", "/admin/consultas/" + id);
            return "consultas/formulario";
        }
        if (bindingResult.hasErrors()) {
            cargarFormularioConsulta(model, consulta, "Editar consulta", "/admin/consultas/" + id);
            return "consultas/formulario";
        }
        try {
            consultaMedicaService.actualizar(id, consulta, medicoId, pacienteId);
            redirectAttributes.addFlashAttribute("mensaje", "Consulta medica actualizada correctamente.");
            return "redirect:/admin/consultas";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormularioConsulta(model, consulta, "Editar consulta", "/admin/consultas/" + id);
            return "consultas/formulario";
        }
    }

    @PostMapping("/admin/consultas/{id}/eliminar")
    public String eliminarConsulta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        consultaMedicaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Consulta medica eliminada correctamente.");
        return "redirect:/admin/consultas";
    }

    @GetMapping("/paciente/consultas/{id}/horario")
    public String mostrarFormularioHorario(@PathVariable Long id, Model model, Authentication authentication) {
        ConsultaMedica consulta = consultaMedicaService.obtenerPorId(id);
        if (!consulta.getPaciente().getUsername().equals(authentication.getName())) {
            return "redirect:/acceso-denegado";
        }
        model.addAttribute("consulta", consulta);
        return "consultas/horario";
    }

    @PostMapping("/paciente/consultas/{id}/horario")
    public String actualizarHorarioPaciente(@PathVariable Long id,
                                            @RequestParam("horaInicio") String horaInicio,
                                            @RequestParam("horaFin") String horaFin,
                                            Authentication authentication,
                                            RedirectAttributes redirectAttributes) {
        try {
            consultaMedicaService.actualizarHorarioPaciente(id, authentication.getName(),
                    java.time.LocalTime.parse(horaInicio), java.time.LocalTime.parse(horaFin));
            redirectAttributes.addFlashAttribute("mensaje", "Horario actualizado correctamente.");
            return "redirect:/paciente/consultas";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/paciente/consultas/" + id + "/horario";
        }
    }

    @GetMapping("/admin/medicos")
    public String listarMedicos(Model model) {
        model.addAttribute("titulo", "Administracion de Medicos");
        model.addAttribute("medicos", consultaMedicaService.listarMedicosParaAdministracion());
        return "medicos/lista";
    }

    @GetMapping("/admin/medicos/nuevo")
    public String mostrarFormularioNuevoMedico(Model model) {
        cargarFormularioMedico(model, new Usuario(), "Registrar medico", "/admin/medicos");
        return "medicos/formulario";
    }

    @PostMapping("/admin/medicos")
    public String crearMedico(@Valid @ModelAttribute("medico") Usuario medico,
                              BindingResult bindingResult,
                              @RequestParam("passwordPlano") String passwordPlano,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (passwordPlano == null || passwordPlano.isBlank()) {
            model.addAttribute("error", "La contrasena del medico es obligatoria.");
            cargarFormularioMedico(model, medico, "Registrar medico", "/admin/medicos");
            return "medicos/formulario";
        }
        if (bindingResult.hasErrors()) {
            cargarFormularioMedico(model, medico, "Registrar medico", "/admin/medicos");
            return "medicos/formulario";
        }
        try {
            consultaMedicaService.crearMedico(medico, passwordPlano);
            redirectAttributes.addFlashAttribute("mensaje", "Medico registrado correctamente.");
            return "redirect:/admin/medicos";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormularioMedico(model, medico, "Registrar medico", "/admin/medicos");
            return "medicos/formulario";
        }
    }

    @GetMapping("/admin/medicos/{id}/editar")
    public String mostrarFormularioEditarMedico(@PathVariable Long id, Model model) {
        cargarFormularioMedico(model, consultaMedicaService.obtenerMedico(id), "Editar medico", "/admin/medicos/" + id);
        return "medicos/formulario";
    }

    @PostMapping("/admin/medicos/{id}")
    public String actualizarMedico(@PathVariable Long id,
                                   @Valid @ModelAttribute("medico") Usuario medico,
                                   BindingResult bindingResult,
                                   @RequestParam(value = "passwordPlano", required = false) String passwordPlano,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarFormularioMedico(model, medico, "Editar medico", "/admin/medicos/" + id);
            return "medicos/formulario";
        }
        try {
            consultaMedicaService.actualizarMedico(id, medico, passwordPlano);
            redirectAttributes.addFlashAttribute("mensaje", "Medico actualizado correctamente.");
            return "redirect:/admin/medicos";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormularioMedico(model, medico, "Editar medico", "/admin/medicos/" + id);
            return "medicos/formulario";
        }
    }

    @PostMapping("/admin/medicos/{id}/eliminar")
    public String eliminarMedico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        consultaMedicaService.eliminarMedico(id);
        redirectAttributes.addFlashAttribute("mensaje", "Medico eliminado correctamente.");
        return "redirect:/admin/medicos";
    }

    private void cargarFormularioConsulta(Model model, ConsultaMedica consulta, String titulo, String action) {
        model.addAttribute("consulta", consulta);
        model.addAttribute("medicos", consultaMedicaService.listarMedicos());
        model.addAttribute("pacientes", consultaMedicaService.listarPacientes());
        model.addAttribute("titulo", titulo);
        model.addAttribute("action", action);
    }

    private void cargarFormularioMedico(Model model, Usuario medico, String titulo, String action) {
        model.addAttribute("medico", medico);
        model.addAttribute("titulo", titulo);
        model.addAttribute("action", action);
    }
}
