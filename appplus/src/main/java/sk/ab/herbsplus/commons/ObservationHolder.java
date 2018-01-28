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
    private TextView observationDate;
    private MapView mapView;
    private ImageView photo;
    private TextView observationNote;

    private GoogleMap map;
    private double latitude;
    private double longitude;
    private Context context;

    public ObservationHolder(View itemView) {
        super(itemView);
        observationDate = itemView.findViewById(R.id.observation_date);
        photo = itemView.findViewById(R.id.observation_photo);
        mapView = itemView.findViewById(R.id.observation_map);
        observationNote = itemView.findViewById(R.id.observation_note);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        LatLng latLong = new LatLng(latitude, longitude);
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f));
        map.addMarker(new MarkerOptions().position(latLong));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void initializeMapView(Context context, Double latitude, Double longitude) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        mapView.onCreate(null);
        mapView.getMapAsync(this);
    }

    public TextView getObservationDate() {
        return observationDate;
    }

    public MapView getMapView() {
        return mapView;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public TextView getObservationNote() {
        return observationNote;
    }
}
