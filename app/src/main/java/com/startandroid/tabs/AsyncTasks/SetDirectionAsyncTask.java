package com.startandroid.tabs.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.startandroid.tabs.BuildConfig;
import com.startandroid.tabs.Model.Place;

import java.util.ArrayList;

/**
 * Created by Денис on 17.07.2016.
 */
public class SetDirectionAsyncTask extends AsyncTask<Void, Long, Long> {

    public ProgressDialog dialog;
    // контекст родительского класса
    Context context;
    GoogleMap googleMap;
    Place place;
    Location myLocation;

    // запускаем ProgressBar в момент запуска потока
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Идет остроение маршрута...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    public SetDirectionAsyncTask(Context context, GoogleMap googleMap, Place place, Location location) {
        this.context = context;
        this.googleMap = googleMap;
        this.place = place;
        this.myLocation = location;
    }

    // сама работа потока, SendHttpPost() - наш долгоработающий метод
    protected Long doInBackground(Void... params) {
        GoogleDirection.withServerKey(BuildConfig.GOOGLE_DIRECTION_SERVER_KEY)
                .from(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
                .to(new LatLng(place.getLatitude(),place.getLongitude()))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(context.getApplicationContext(), directionPositionList, 5, Color.RED);
                            googleMap.addPolyline(polylineOptions);
                        } else {
                            Toast.makeText(context, direction.getStatus(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Toast.makeText(context, "Невозможно продлжить маршрут", Toast.LENGTH_LONG).show();
                    }
                });

        return null;
    }

    // как только получили ответ от сервера, выключаем ProgressBar
    protected void onPostExecute(Long unused) {
        super.onPostExecute(unused);
        dialog.dismiss();

    }
}