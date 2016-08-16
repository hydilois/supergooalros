package cm.elsha.supergooalros.repository.search;

import cm.elsha.supergooalros.domain.Shop;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Shop entity.
 */
public interface ShopSearchRepository extends ElasticsearchRepository<Shop, Long> {
}
