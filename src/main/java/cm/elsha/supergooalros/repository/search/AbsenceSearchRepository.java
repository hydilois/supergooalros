package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.Absence;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Absence entity.
 */
public interface AbsenceSearchRepository extends ElasticsearchRepository<Absence, Long> {
}
