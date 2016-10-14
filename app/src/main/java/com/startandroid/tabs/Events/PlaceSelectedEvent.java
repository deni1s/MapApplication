package com.startandroid.tabs.Events;

import com.startandroid.tabs.Model.Place;

/**
 * Created by Денис on 08.07.2016.
 */
public class PlaceSelectedEvent {
    public Place place;

    public PlaceSelectedEvent(Place place) {
        this.place = place;
    }
}
