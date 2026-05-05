package co.edu.usco.gestion_asignaturas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String root() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String inicio(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        boolean esRector = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMINISTRADOR"));
        boolean esDocente = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO"));
        if (esRector) {
            return "redirect:/admin/consultas";
        }
        if (esDocente) {
            return "redirect:/medico/consultas";
        }
        return "redirect:/paciente/consultas";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
