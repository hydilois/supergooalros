package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.RecetteJournaliere;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the RecetteJournaliere entity.
 */
@SuppressWarnings("unused")
public interface RecetteJournaliereRepository extends JpaRepository<RecetteJournaliere,Long> {

}
