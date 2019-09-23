package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class MainController {
    @Autowired
    private ShipRepo shipRepo;

    @GetMapping(value = "/rest/ships", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity getShips(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "planet", required = false) String planet,
                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                   @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                   @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                   @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                   @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                   @RequestParam(value = "minRating", required = false) Double minRating,
                                   @RequestParam(value = "maxRating", required = false) Double maxRating,
                                   @RequestParam(value = "order", required = false) ShipOrder order,
                                   @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        return new ResponseEntity(filterOrderSips(null, name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize),
                HttpStatus.OK);
    }

    @GetMapping(value = "/rest/ships/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity getShipsCount(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "planet", required = false) String planet,
                                @RequestParam(value = "shipType", required = false) ShipType shipType,
                                @RequestParam(value = "after", required = false) Long after,
                                @RequestParam(value = "before", required = false) Long before,
                                @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                @RequestParam(value = "minRating", required = false) Double minRating,
                                @RequestParam(value = "maxRating", required = false) Double maxRating) {
        Integer count = filterOrderSips(null, name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, ShipOrder.ID, 0, 0).size();
        return new ResponseEntity(count, HttpStatus.OK);
    }

    @PostMapping(value = "/rest/ships/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity createShip(@RequestBody Ship newShip) {
        if ((newShip.getName() == null || newShip.getName().equals("") || newShip.getName().length() > 50) ||
                (newShip.getPlanet() == null || newShip.getPlanet().equals("") || newShip.getPlanet().length() > 50) ||
                (newShip.getShipType() == null) ||
                (newShip.getProdDate() == null || newShip.getProdDate().getTime() < 0 || ((newShip.getProdDate().getYear() + 1900) < 2800 || (newShip.getProdDate().getYear() + 1900) > 3019)) ||
                (newShip.getSpeed() == null || newShip.getSpeed() < 0 || (BigDecimal.valueOf(newShip.getSpeed()).setScale(2, RoundingMode.HALF_UP).doubleValue() < 0.01 || BigDecimal.valueOf(newShip.getSpeed()).setScale(2, RoundingMode.HALF_UP).doubleValue() > 0.99)) ||
                (newShip.getCrewSize() == null || (newShip.getCrewSize() < 1 || newShip.getCrewSize() > 9999))) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if (newShip.getUsed() == null) {
            newShip.setUsed(false);
        }
        newShip.setSpeed(BigDecimal.valueOf(newShip.getSpeed()).setScale(2, RoundingMode.HALF_UP).doubleValue());
        newShip.calculateRating();
        shipRepo.save(newShip);
        return new ResponseEntity(newShip, HttpStatus.OK);
    }

    @GetMapping(value = "/rest/ships/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getShip(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Ship> ships = filterOrderSips(id, null, null, null, null, null, null, null, null, null, null, null, null, ShipOrder.ID, 0, 0);
        if (ships.size() != 1)  {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(ships.get(0), HttpStatus.OK);
    }

    @PostMapping(value = "/rest/ships/{id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateShip(@PathVariable("id") Long id, @RequestBody Ship newShip) {
        if (id == null || id <= 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Ship> ships = filterOrderSips(id, null, null, null, null, null, null, null, null, null, null, null, null, ShipOrder.ID, 0, 0);
        if (ships.size() != 1)  {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Ship updatingShip = ships.get(0);

        if ((newShip.getName() != null && (newShip.getName().equals("") || newShip.getName().length() > 50)) ||
                (newShip.getPlanet() != null && (newShip.getPlanet().equals("") || newShip.getPlanet().length() > 50)) ||
                (newShip.getShipType() != null && !Arrays.asList(ShipType.values()).contains(newShip.getShipType())) ||
                (newShip.getProdDate() != null && (newShip.getProdDate().getTime() < 0 || ((newShip.getProdDate().getYear() + 1900) < 2800 || (newShip.getProdDate().getYear() + 1900) > 3019))) ||
                (newShip.getSpeed() != null && (newShip.getSpeed() < 0 || (BigDecimal.valueOf(newShip.getSpeed()).setScale(2, RoundingMode.HALF_UP).doubleValue() < 0.01 || BigDecimal.valueOf(newShip.getSpeed()).setScale(2, RoundingMode.HALF_UP).doubleValue() > 0.99))) ||
                (newShip.getCrewSize() != null && (newShip.getCrewSize() < 1 || newShip.getCrewSize() > 9999))) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (newShip.getUsed() == null) {
            newShip.setUsed(false);
        }

        if (newShip.getName() != null) {
            updatingShip.setName(newShip.getName());
        }
        if (newShip.getPlanet() != null) {
            updatingShip.setPlanet(newShip.getPlanet());
        }
        if (newShip.getShipType() != null) {
            updatingShip.setShipType(newShip.getShipType());
        }
        if (newShip.getProdDate() != null) {
            updatingShip.setProdDate(newShip.getProdDate());
        }
        if (newShip.getSpeed() != null) {
            updatingShip.setSpeed(newShip.getSpeed());
        }
        if (newShip.getCrewSize() != null) {
            updatingShip.setCrewSize(newShip.getCrewSize());
        }

        updatingShip.calculateRating();
        shipRepo.save(updatingShip);
        return new ResponseEntity(updatingShip, HttpStatus.OK);
    }

    @DeleteMapping(value = "/rest/ships/{id}")
    public ResponseEntity deleteShip(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<Ship> ships = filterOrderSips(id, null, null, null, null, null, null, null, null, null, null, null, null, ShipOrder.ID, 0, 0);
        if (ships.size() != 1)  {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        shipRepo.delete(ships.get(0));
        return new ResponseEntity(HttpStatus.OK);
    }

    private List<Ship> filterOrderSips(Long id, String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                       Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize){
        List<Ship> result = ((List<Ship>) shipRepo.findAll());
        if (id != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByIdEquals(id))::contains).collect(Collectors.toList());
        }
        if (name != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByNameContaining(name))::contains).collect(Collectors.toList());
        }
        if (planet != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByPlanetContaining(planet))::contains).collect(Collectors.toList());
        }
        if (shipType != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByShipTypeEquals(shipType))::contains).collect(Collectors.toList());
        }
        if (after != null) {
            Date date = new Date(after);
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByProdDateAfter(date))::contains).collect(Collectors.toList());
        }
        if (before != null) {
            Date date = new Date(before);
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByProdDateBefore(date))::contains).collect(Collectors.toList());
        }
        if (isUsed != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByIsUsed(isUsed))::contains).collect(Collectors.toList());
        }
        if (minSpeed != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findBySpeedGreaterThanEqual(minSpeed))::contains).collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findBySpeedLessThanEqual(maxSpeed))::contains).collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByCrewSizeGreaterThanEqual(minCrewSize))::contains).collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByCrewSizeLessThanEqual(maxCrewSize))::contains).collect(Collectors.toList());
        }
        if (minRating != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByRatingGreaterThanEqual(minRating))::contains).collect(Collectors.toList());
        }
        if (maxRating != null) {
            result = result.stream().filter(((Collection<Ship>) shipRepo.findByRatingLessThanEqual(maxRating))::contains).collect(Collectors.toList());
        }


        if (order != null) {
            if (order == ShipOrder.ID) {
                result.sort(Ship.IdComparator);
            }
            else if (order == ShipOrder.SPEED) {
                result.sort(Ship.SpeedComparator);
            }
            else if (order == ShipOrder.DATE) {
                result.sort(Ship.DateComparator);
            }
            else if (order == ShipOrder.RATING) {
                result.sort(Ship.RaitingComparator);
            }
        }

        int startIdx;
        int endIdx;
        if (pageSize == 0) {
            startIdx = 0;
            endIdx = result.size();
        } else {
            startIdx = (pageSize * pageNumber);
            endIdx = Math.min(startIdx + pageSize, result.size());
        }
        return result.subList(startIdx, endIdx);
    }


}
