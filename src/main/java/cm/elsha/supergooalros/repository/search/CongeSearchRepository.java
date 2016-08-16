package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.Conge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Conge entity.
 */
public interface CongeSearchRepository extends ElasticsearchRepository<Conge, Long> {
}
