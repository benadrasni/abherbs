package sk.ab.herbsplus.commons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sk.ab.herbsplus.R;

/**
 *
 * Created by adrian on 25. 1. 2018.
 */

public class ObservationHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    private TextView plantName;
    private TextView observationDate;
    private ImageView edit;
    private ImageView delete;
    private MapView mapView;
    private ImageView prevPhoto;
    private TextView counter;
    private ImageView photo;
    private ImageView nextPhoto;
    private TextView observationNote;

    private double latitude;
    private double longitude;
    private Context context;
    private int photoPosition;

    public ObservationHolder(View itemView) {
        super(itemView);
        plantName = itemView.findViewById(R.id.observation_plant);
        observationDate = itemView.findViewById(R.id.observation_date);
        edit = itemView.findViewById(R.id.observation_edit);
        delete = itemView.findViewById(R.id.observation_delete);
        mapView = itemView.findViewById(R.id.observation_map);
        prevPhoto = itemView.findViewById(R.id.observation_prev_photo);
        counter = itemView.findViewById(R.id.observation_photo_counter);
        nextPhoto = itemView.findViewById(R.id.observation_next_photo);
        photo = itemView.findViewById(R.id.observation_photo);
        observationNote = itemView.findViewById(R.id.observation_note);
        photoPosition = 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        LatLng latLong = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f));
        googleMap.addMarker(new MarkerOptions().position(latLong));
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void initializeMapView(Context context, Double latitude, Double longitude) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    public TextView getPlantName() {
        return plantName;
    }

    public TextView getObservationDate() {
        return observationDate;
    }

    public ImageView getEdit() {
        return edit;
    }

    public ImageView getDelete() {
        return delete;
    }

    public ImageView getPrevPhoto() {
        return prevPhoto;
    }

    public TextView getCounter() {
        return counter;
    }

    public ImageView getNextPhoto() {
        return nextPhoto;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public TextView getObservationNote() {
        return observationNote;
    }

    public int getPhotoPosition() {
        return photoPosition;
    }

    public void setPhotoPosition(int photoPosition) {
        this.photoPosition = photoPosition;
    }

    public void incPosition() {
        photoPosition++;
    }

    public void decPosition() {
        photoPosition--;
    }

}
