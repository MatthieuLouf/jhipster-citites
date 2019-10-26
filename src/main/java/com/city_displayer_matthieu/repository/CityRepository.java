package com.city_displayer_matthieu.repository;
import com.city_displayer_matthieu.domain.City;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, Long>, JpaSpecificationExecutor<City> {

    @Query("select city from City city where city.user.login = ?#{principal.username}")
    List<City> findByUserIsCurrentUser();

}
