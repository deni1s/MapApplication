package com.startandroid.tabs.Model;
import android.location.Address;

import com.orm.SugarRecord;

import java.util.List;

public class Place extends SugarRecord {
    private String name;
    private double latitude;
    private double longitude;
    public boolean isDeleted;
    private List<android.location.Address> adress;

    public Place() {
    }

    public Place(String name, double latitude, double longitude,  List<android.location.Address> adress) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adress = adress;
        this.isDeleted = isDeleted;
        isDeleted = false;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Address> getAdress() {
        return adress;
    }

    @Override
    public String toString() {
        return name;
    }

}




