package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.Work;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Work entity.
 */
@SuppressWarnings("unused")
public interface WorkRepository extends JpaRepository<Work,Long> {

}
