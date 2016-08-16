package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.RecetteJournaliere;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the RecetteJournaliere entity.
 */
public interface RecetteJournaliereSearchRepository extends ElasticsearchRepository<RecetteJournaliere, Long> {
}
