package com.startandroid.tabs.Adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.startandroid.tabs.Model.Place;
import com.startandroid.tabs.Events.PlaceListUpdatedEvent;
import com.startandroid.tabs.Events.PlaceSelectedEvent;
import com.startandroid.tabs.R;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PlaceListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    Context context;
    LayoutInflater lInflater;
    ArrayList<Place> objects;
    ArrayList<Place> placeList;


    public PlaceListAdapter(Context context, ArrayList<Place> products) {
        this.context = context;
        objects = products;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return objects.size();
    }



    @Override
    public Place getItem(int position) {
        return objects.get(position);
    }

    public void removeItem(int position) {
        objects.remove(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        final Place place = getItem(position);
        view = createViewForLayout((place.isDeleted) ? R.layout.deleted_item : R.layout.item, lInflater, convertView, parent);
       // if (view == null) {
         //   view = lInflater.inflate(R.layout.item, parent, false);
        //}


        if (place.isDeleted) {
            ((TextView) view.findViewById(R.id.deletedPlaceName)).setText(place.getName());

        } else {
            ((TextView) view.findViewById(R.id.placeName)).setText(place.getName());
            ((TextView) view.findViewById(R.id.latitude)).setText("Широта:" + place.getLatitude() + "");
            ((TextView) view.findViewById(R.id.longitude)).setText("Долгота: " + place.getLongitude() + "");
        }




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PlaceSelectedEvent(getPlace(position)));
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                displayDeletePlaceDialog(position);
        return false;
            }
        });

        return view;
    }

    private View createViewForLayout(Object layoutId, LayoutInflater inflater, View convertView, final ViewGroup parent) {
        View view = null;
        if ((convertView != null) && (convertView.getTag() == layoutId)) {
            view = convertView;
        } else {
            view = inflater.inflate((Integer) layoutId, parent, false);
            view.setTag(layoutId);
        }
        return view;
    }


    Place getPlace(int position) {
        return ((Place) getItem(position));
    }


    private void displayDeletePlaceDialog(final int placePosition)
    {
        final LayoutInflater dialogLayout = LayoutInflater.from(context);
        final View deletePlaceView = dialogLayout.inflate(R.layout.delete_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setView(deletePlaceView);

        Button deleteButton = (Button) deletePlaceView.findViewById(R.id.deleteButton);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePlace(placePosition);
                alertDialog.dismiss();
            }
        });

        // show it
        alertDialog.show();
    }

    private void deletePlace(int placePosition) {
        if (getItem(placePosition).isDeleted) {
            List<Place> pl = Place.find(Place.class, "name = ?", String.valueOf(getItem(placePosition)));
            removeItem(placePosition);
            pl.get(0).delete();
            notifyDataSetChanged();
         } else {
                List<Place> pl = Place.find(Place.class, "name = ?", String.valueOf(getItem(placePosition)));
                pl.get(0).isDeleted = true;
                pl.get(0).save();
                EventBus.getDefault().post(new PlaceListUpdatedEvent(pl.get(0)));
        }
        Toast.makeText(context, "Локация удалена", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

}