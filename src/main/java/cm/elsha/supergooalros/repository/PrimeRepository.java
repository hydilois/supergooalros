package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.Prime;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Prime entity.
 */
@SuppressWarnings("unused")
public interface PrimeRepository extends JpaRepository<Prime,Long> {

}
