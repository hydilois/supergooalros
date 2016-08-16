package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.Absence;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Absence entity.
 */
@SuppressWarnings("unused")
public interface AbsenceRepository extends JpaRepository<Absence,Long> {

}
