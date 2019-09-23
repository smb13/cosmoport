package com.space.repository;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ShipRepo extends CrudRepository<Ship, Long> {
    List<Ship> findByIdEquals(Long id);
    List<Ship> findByNameContaining(String name);
    List<Ship> findByPlanetContaining(String planet);
    List<Ship> findByShipTypeEquals(ShipType shipType);
    List<Ship> findByProdDateAfter(Date after);
    List<Ship> findByProdDateBefore(Date bafore);
    List<Ship> findByIsUsed(Boolean isUsed);
    List<Ship> findBySpeedGreaterThanEqual(Double minSpeed);
    List<Ship> findBySpeedLessThanEqual(Double maxSpeed);
    List<Ship> findByCrewSizeGreaterThanEqual(Integer minCrewSize);
    List<Ship> findByCrewSizeLessThanEqual(Integer maxCrewSize);
    List<Ship> findByRatingGreaterThanEqual(Double minCrewSize);
    List<Ship> findByRatingLessThanEqual(Double maxCrewSize);
}
