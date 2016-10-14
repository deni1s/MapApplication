package com.startandroid.tabs.AsyncTasks;


import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

public class LocationDecoder extends AsyncTask<Location, Void, Void>
{

    private Geocoder geocoder;
    private Location location;
    List<android.location.Address> address;

    public LocationDecoder(Geocoder geocoder, Location location) {
        super();
        this.geocoder = geocoder;
        this.location = location;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Location... params) {
        try {
            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        //this method will be running on UI thread

    }

    public List<android.location.Address> getAddress()
    {
        return address;
    }

}