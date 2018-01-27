package sk.ab.herbsplus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsplus.R;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        if (getIntent().getExtras() != null) {
            double latitude = getIntent().getExtras().getDouble(AndroidConstants.STATE_LATITUDE);
            double longitude = getIntent().getExtras().getDouble(AndroidConstants.STATE_LONGITUDE);
            position = new LatLng(latitude, longitude);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(position).title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }
}
