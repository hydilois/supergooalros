package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.Prime;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Prime entity.
 */
public interface PrimeSearchRepository extends ElasticsearchRepository<Prime, Long> {
}
