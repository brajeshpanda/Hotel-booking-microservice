package com.propertyservice.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "property")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "number_of_beds")
    private int numberOfBeds;

    @Column(name = "number_of_rooms")
    private int numberOfRooms;

    @Column(name = "number_of_bathrooms")
    private int numberOfBathrooms;

    @Column(name = "number_of_guests_allowed")
    private int numberOfGuestAllowed;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "gst_number")
    private String gstNumber;

    // ---------- LOCATION -------------

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    // ---------- ROOMS -------------

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Rooms> rooms = new ArrayList<>();

    // ---------- PHOTOS (IMPORTANT) -------------

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyPhotos> photos = new ArrayList<>();

    // ===========================================================
    // GETTERS & SETTERS
    // ===========================================================

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(int numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(int numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public int getNumberOfGuestAllowed() {
        return numberOfGuestAllowed;
    }

    public void setNumberOfGuestAllowed(int numberOfGuestAllowed) {
        this.numberOfGuestAllowed = numberOfGuestAllowed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Rooms> getRooms() {
        return rooms;
    }

    public void setRooms(List<Rooms> rooms) {
        this.rooms = rooms;
    }

    public List<PropertyPhotos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PropertyPhotos> photos) {
        this.photos = photos;
    }
}
