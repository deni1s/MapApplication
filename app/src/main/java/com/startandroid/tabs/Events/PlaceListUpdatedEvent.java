package com.startandroid.tabs.Events;

import com.startandroid.tabs.Model.Place;

public class PlaceListUpdatedEvent {
    public Place place;

    public PlaceListUpdatedEvent(Place place) {
        this.place = place;
    }
}
