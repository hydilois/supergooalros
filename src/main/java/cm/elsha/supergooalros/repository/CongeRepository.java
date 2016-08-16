package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.Conge;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Conge entity.
 */
@SuppressWarnings("unused")
public interface CongeRepository extends JpaRepository<Conge,Long> {

}
