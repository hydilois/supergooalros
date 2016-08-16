package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.Work;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Work entity.
 */
public interface WorkSearchRepository extends ElasticsearchRepository<Work, Long> {
}
