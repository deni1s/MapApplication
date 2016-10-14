package com.startandroid.tabs.Fragments;

import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.startandroid.tabs.AsyncTasks.LocationDecoder;
import com.startandroid.tabs.AsyncTasks.SetDirectionAsyncTask;
import com.startandroid.tabs.Model.Place;
import com.startandroid.tabs.Events.PlaceListUpdatedEvent;
import com.startandroid.tabs.Events.PlaceSelectedEvent;
import com.startandroid.tabs.AsyncTasks.setDirectionTask;
import com.startandroid.tabs.R;

import java.util.List;

import de.greenrobot.event.EventBus;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    Location currentLocation;
    CircularProgressView progressView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_layout, container, false);
        createMapView();
        RadioButton save = (RadioButton) view.findViewById(R.id.saveButton);
        progressView = (CircularProgressView) view.findViewById(R.id.progress_view);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPlaceNameDialog(googleMap.getMyLocation());
            }
        });

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

    public void onEvent(PlaceSelectedEvent messagePlace) {
        googleMap.clear();
        updateMap();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()))
                .zoom(13)
                .tilt(20) //угол наклона карты
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                1500, null);
        SetDirectionAsyncTask setDirection = new SetDirectionAsyncTask(getContext(), googleMap, messagePlace.place, googleMap.getMyLocation());
        setDirection.execute();
    }

    public void onEvent(PlaceListUpdatedEvent messagePlace) {
      googleMap.clear();
        updateMap();
    }


    private void createMapView() {
        try {
            mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.mapView);
            mapFragment.getMapAsync(this);
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }

    private void updateMap()
    {
        List<Place> placeList = Place.listAll(Place.class);
        for (int i = 0; i < placeList.size(); i++) {
            if(!placeList.get(i).isDeleted)
            addMarker(placeList.get(i));
        }
    }


    private void ShowPlaceNameDialog(final Location location)
    {
        LayoutInflater dialogLayout = LayoutInflater.from(getContext());
        View nameView = dialogLayout.inflate(R.layout.dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set dialog.xml to alertdialog builder
        alertDialogBuilder.setView(nameView);

        final EditText userInput = (EditText)nameView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveNewPlace(userInput.getText().toString(), location);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        //
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        currentLocation = googleMap.getMyLocation();
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        updateMap();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Location temp = new Location(LocationManager.GPS_PROVIDER);
                temp.setLatitude(latLng.latitude);
                temp.setLongitude(latLng.longitude);
                ShowPlaceNameDialog(temp);
            }
        });
    }

    private void saveNewPlace(String placeName, Location location) {
        LocationDecoder toAddressDecoder = new LocationDecoder(new Geocoder(getContext()), googleMap.getMyLocation());
       // LocationDecode locationDecoder = new LocationDecoder(googleMap.getMyLocation(), gCoder);
        Place place = new Place(placeName, location.getLatitude(), location.getLongitude(), toAddressDecoder.getAddress());
        place.save();
        EventBus.getDefault().post(new PlaceListUpdatedEvent(place));
        addMarker(place);
        Toast.makeText(getActivity(), "Локация сохранена", Toast.LENGTH_LONG).show();
    }

    private void addMarker(Place place) {

        /** Make sure that the map has been initialised **/
        if (place.getAdress() != null && place.getAdress().size() > 0) {
            if (null != googleMap) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getAdress().get(0).getAddressLine(0))
                );
            }
        } else if (null != googleMap) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLatitude(), place.getLongitude()))
                    .title(place.getName())
            );
        }
    }

}