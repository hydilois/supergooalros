package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.Employe;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Employe entity.
 */
@SuppressWarnings("unused")
public interface EmployeRepository extends JpaRepository<Employe,Long> {

}
