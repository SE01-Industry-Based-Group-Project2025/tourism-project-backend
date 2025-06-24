package com.sl_tourpal.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sl_tourpal.backend.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
