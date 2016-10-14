package com.startandroid.tabs.Fragments;

/**
 * Created by Денис on 18.06.2016.
 */
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.startandroid.tabs.Adapters.PlaceListAdapter;
import com.startandroid.tabs.Model.Place;
import com.startandroid.tabs.Events.PlaceListUpdatedEvent;
import com.startandroid.tabs.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class PlaceListFragment extends android.support.v4.app.ListFragment {

    ArrayList<Place> placeList;
    ListView listViewPlaces;
    ListAdapter placeListAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, container, false);

        placeList = (ArrayList<Place>) Place.listAll(Place.class);
        listViewPlaces =  (ListView) view.findViewById(android.R.id.list);
        placeListAdapter = new PlaceListAdapter(getActivity(), pushDeletedPlacesBack(placeList));

        listViewPlaces.setAdapter(placeListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    private  ArrayList<Place> pushDeletedPlacesBack(ArrayList<Place> places){
        ArrayList<Place> deletedObjects = new ArrayList<Place>();
        for (int i = 0; i < places.size(); i++) {
            if(places.get(i).isDeleted) {
                deletedObjects.add(places.get(i));
            }
        }

        for(int k = 0; k < places.size(); k++) {
            if(places.get(k).isDeleted) {
                Log.d(places.get(k).getName(), "deleted");
                places.remove(k);
                k--;
            }
        }

        for(int j = 0; j < deletedObjects.size(); j++) {
                places.add(deletedObjects.get(j));
        }
        return places;
    }



    public void onEvent(PlaceListUpdatedEvent messagePlace) {
        placeList = (ArrayList<Place>) Place.listAll(Place.class);

        placeListAdapter = new PlaceListAdapter(getActivity(), pushDeletedPlacesBack(placeList));

        listViewPlaces.setAdapter(placeListAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}