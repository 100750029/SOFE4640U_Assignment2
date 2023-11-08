package com.example.sofe4640u_assignment2;

import java.util.ArrayList;
import java.util.Date;

public class Location {

    // Array for storing information about location
    public static ArrayList<Location> locationArrayList = new ArrayList<>();
    // Variable for editing location
    public static String LOCATION_EDIT_EXTRA =  "locationEdit";
    private int id;

    private String address;

    private Double longitude, latitude;

    private Date delete;

    // Constructor for location
    public Location(int id, String address, Double longitude, Double latitude, Date delete) {
        this.id = id;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.delete = delete;
    }

    public Location(int id, String address, Double longitude, Double latitude) {
        this.id = id;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        delete = null;
    }

    // function for getting location ID
    public static Location getLocationForID(int passedLocationID)
    {
        for (Location location : locationArrayList)
        {
            if(location.getId() == passedLocationID)
                return location;
        }

        return null;
    }


    // Array list for deleted locations
    public static ArrayList<Location> nonDeletedLocation()
    {
        ArrayList<Location> nonDeleted = new ArrayList<>();
        for(Location location : locationArrayList)
        {
            if(location.getDelete() == null)
                nonDeleted.add(location);
        }

        return nonDeleted;
    }

    public int getId() {
        return id;
    } // Getter for location id

    public String getAddress() {
        return address;
    } // Getter for location address

    public Double getLongitude() {
        return longitude;
    } // Getter for location longitude

    public Double getLatitude() {
        return latitude;
    } // Getter for location latitude

    public void setId(int id) {
        this.id = id;
    } // Setter for location id

    public void setAddress(String address) {
        this.address = address;
    } // Setter for location address

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    } // Setter for location longitude

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    } // Setter for location latitude

    public Date getDelete() {
        return delete;
    } // Getter for location delete

    public void setDelete(Date delete) {
        this.delete = delete;
    } // Setter for location delete


}
