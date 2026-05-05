package co.edu.usco.gestion_asignaturas.config;

import co.edu.usco.gestion_asignaturas.entity.Rol;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/login", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
                        .requestMatchers("/api/consultas/*/horario").hasRole(Rol.PACIENTE.name())
                        .requestMatchers("/admin/**", "/api/consultas/**", "/api/medicos/**", "/api/pacientes/**").hasRole(Rol.ADMINISTRADOR.name())
                        .requestMatchers("/medico/**").hasRole(Rol.MEDICO.name())
                        .requestMatchers("/paciente/**").hasRole(Rol.PACIENTE.name())
                        .requestMatchers("/", "/inicio", "/acceso-denegado").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
                .exceptionHandling(exception -> exception.accessDeniedPage("/acceso-denegado"))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                org.springframework.security.core.Authentication authentication)
                    throws IOException, ServletException {
                boolean esRector = authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Rol.ADMINISTRADOR.name()));
                boolean esDocente = authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + Rol.MEDICO.name()));

                if (esRector) {
                    response.sendRedirect("/admin/consultas");
                    return;
                }
                if (esDocente) {
                    response.sendRedirect("/medico/consultas");
                    return;
                }
                response.sendRedirect("/paciente/consultas");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
