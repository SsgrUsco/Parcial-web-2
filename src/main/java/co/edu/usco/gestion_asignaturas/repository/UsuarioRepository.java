package co.edu.usco.gestion_asignaturas.repository;

import co.edu.usco.gestion_asignaturas.entity.Rol;
import co.edu.usco.gestion_asignaturas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRolOrderByNombreCompletoAsc(Rol rol);

    List<Usuario> findAllByOrderByNombreCompletoAsc();
}
