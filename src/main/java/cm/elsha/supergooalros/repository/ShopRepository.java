package cm.elsha.supergooalros.repository;

import cm.elsha.supergooalros.domain.Shop;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Shop entity.
 */
@SuppressWarnings("unused")
public interface ShopRepository extends JpaRepository<Shop,Long> {

    @Query("select shop from Shop shop where shop.user.login = ?#{principal.username}")
    List<Shop> findByUserIsCurrentUser();

}
