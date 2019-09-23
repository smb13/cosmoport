package com.space.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

@Entity
@Table (name = "ship")
public class Ship {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name= "increment", strategy= "increment")
    @Column(name = "id", length = 20, nullable = false)
    private Long id;
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    @Column(name = "planet", length = 50, nullable = false)
    private String planet;
    @Enumerated(EnumType.STRING)
    private ShipType shipType;
    @Column(name = "prodDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date prodDate;

    private Boolean isUsed;
    private Double speed;
    private Integer crewSize;
    private Double rating;

    protected Ship(){}

    public Ship(String name, String planet, ShipType shipType, Date prodDate, Boolean isUsed, Double speed, Integer crewSize, Double rating) {
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.prodDate = prodDate;
        this.isUsed = isUsed;
        this.speed = speed;
        this.crewSize = crewSize;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public Double getSpeed() {
        return speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void calculateRating() {

        this.setRating(BigDecimal.valueOf((80 * speed * (isUsed ? 0.5 : 1)) / (3019 - (prodDate.getYear() + 1900) + 1)).setScale(2, RoundingMode.HALF_UP).doubleValue());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ship)) return false;
        Ship ship = (Ship) o;
        return Objects.equals(id, ship.id) &&
                Objects.equals(name, ship.name) &&
                Objects.equals(planet, ship.planet) &&
                shipType == ship.shipType &&
                Objects.equals(prodDate, ship.prodDate) &&
                Objects.equals(isUsed, ship.isUsed) &&
                Objects.equals(speed, ship.speed) &&
                Objects.equals(crewSize, ship.crewSize) &&
                Objects.equals(rating, ship.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, planet, shipType, prodDate, isUsed, speed, crewSize, rating);
    }

    public static Comparator<Ship> IdComparator = (sh1, sh2) -> (int) (Long.compare(sh1.getId(),sh2.getId()));

    public static Comparator<Ship> SpeedComparator = (sh1, sh2) -> (int) (Double.compare(sh1.getSpeed(), sh2.getSpeed()));

    public static Comparator<Ship> DateComparator = (sh1, sh2) -> (int) (Long.compare(sh1.getProdDate().getTime(), sh2.getProdDate().getTime()));

    public static Comparator<Ship> RaitingComparator = (sh1, sh2) -> (int) (Double.compare(sh1.getRating(), sh2.getRating()));
}
