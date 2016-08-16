package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.QuotaHebdo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the QuotaHebdo entity.
 */
@SuppressWarnings("unused")
public interface QuotaHebdoRepository extends JpaRepository<QuotaHebdo,Long> {

}
