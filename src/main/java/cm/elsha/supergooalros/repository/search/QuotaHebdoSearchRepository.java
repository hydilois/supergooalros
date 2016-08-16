package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.QuotaHebdo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the QuotaHebdo entity.
 */
public interface QuotaHebdoSearchRepository extends ElasticsearchRepository<QuotaHebdo, Long> {
}
