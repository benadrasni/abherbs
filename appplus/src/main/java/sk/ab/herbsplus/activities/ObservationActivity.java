package sk.ab.herbsplus.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.ByteStreams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.tools.Utils;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.entity.ObservationParcel;

/**
 *
 * Created by adrian on 28. 1. 2018.
 */

public class ObservationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker marker;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseUser currentUser;
    private ObservationParcel observation;

    private long mLastClickTime;
    private Uri mCurrentPhotoUri;
    private int photoPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            observation = savedInstanceState.getParcelable(AndroidConstants.STATE_OBSERVATION);
        } else if (getIntent().getExtras() != null) {
            observation = getIntent().getExtras().getParcelable(AndroidConstants.STATE_OBSERVATION);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        photoPosition = 0;

        setContentView(R.layout.observation_activity);

        FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClickOnObservation();
            }
        });

        ImageView observationCamera = (ImageView) findViewById(R.id.observation_camera);
        observationCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCameraPhoto();
            }
        });

        ImageView observationGallery = (ImageView) findViewById(R.id.observation_gallery);
        observationGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGalleryPhoto();
            }
        });

        ImageView observationLocation = (ImageView) findViewById(R.id.observation_location);
        observationLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                observation.setLatitude(location.getLatitude());
                                observation.setLongitude(location.getLongitude());
                            }
                        }
                    });
        }

        initializeObservation();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(observation.getPlant());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        saveObservation(false);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(AndroidConstants.STATE_OBSERVATION, observation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_observation, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_delete_observation:
                AlertDialog dialogBox = DeleteConfirmationDialog(observation);
                dialogBox.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AndroidConstants.REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    processPhoto(mCurrentPhotoUri);
                } else {
                    // Camera failed, check response for error code
                    Toast.makeText(this, R.string.camera_failed, Toast.LENGTH_LONG).show();
                }
                break;
            case AndroidConstants.REQUEST_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    processPhoto(data.getData());
                } else {
                    // Camera failed, check response for error code
                    Toast.makeText(this, R.string.gallery_failed, Toast.LENGTH_LONG).show();
                }
                break;
            case AndroidConstants.REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra(AndroidConstants.STATE_LATITUDE) && data.hasExtra(AndroidConstants.STATE_LONGITUDE)) {
                        double latitude = data.getExtras().getDouble(AndroidConstants.STATE_LATITUDE);
                        double longitude = data.getExtras().getDouble(AndroidConstants.STATE_LONGITUDE);
                        addMarkerToMap(latitude, longitude);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case SpecificConstants.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        if (observation.getLatitude() == null || observation.getLongitude() == null) {
                                            addMarkerToMap(location.getLatitude(), location.getLongitude());
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }

    private void addCameraPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoUri = FileProvider.getUriForFile(this, "sk.ab.herbsplus.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                startActivityForResult(takePictureIntent, AndroidConstants.REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void addGalleryPhoto() {
        Intent pickPictureIntent = new Intent();
        pickPictureIntent.setType("image/*");
        pickPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        if (pickPictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "sk.ab.herbsplus.fileprovider", photoFile);
                pickPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mCurrentPhotoUri = photoURI;
                startActivityForResult(pickPictureIntent, AndroidConstants.REQUEST_PICK_PHOTO);
            }
        }
    }

//    private void addNote() {
//        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//
//        View noteView = inflater.inflate(R.layout.note_layout,null);
//        final PopupWindow mPopupWindow = new PopupWindow(
//                noteView,
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                true
//        );
//        final EditText note = noteView.findViewById(R.id.note);
//        note.setText(observation.getNote());
//
//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                observation.setNote(note.getText().toString());
//            }
//        });
//        mPopupWindow.showAtLocation(mCoordinatorLayout, Gravity.CENTER,0,0);
//    }

    private void addLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, SpecificConstants.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                if (observation.getLatitude() == null || observation.getLongitude() == null) {
                                    observation.setLatitude(location.getLatitude());
                                    observation.setLongitude(location.getLongitude());
                                }
                                Intent intent = new Intent(ObservationActivity.this, MapActivity.class);

                                Bundle extras = new Bundle();
                                extras.putDouble(AndroidConstants.STATE_LATITUDE, observation.getLatitude());
                                extras.putDouble(AndroidConstants.STATE_LONGITUDE, observation.getLongitude());
                                intent.putExtras(extras);
                                startActivityForResult(intent, AndroidConstants.REQUEST_LOCATION);
                            }
                        }
                    });
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(observation.getId() + "_", ".jpg", storageDir);
    }

    private void processPhoto(Uri uri) {
        String dirname = AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                + currentUser.getUid() + AndroidConstants.SEPARATOR + observation.getPlant() + AndroidConstants.SEPARATOR;
        String filename = mCurrentPhotoUri.getLastPathSegment();
        String path = this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + dirname + filename;
        File dir = new File(this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + dirname);
        dir.mkdirs();
        try (InputStream inputStream = this.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(path)) {

            if (inputStream != null) {
                ByteStreams.copy(inputStream, outputStream);
                ExifInterface exif = new ExifInterface(path);

                // fill observation_row
                observation.getPhotoPaths().add(dirname + filename);
                if (exif.getDateTime() > 0) {
                    observation.setDate(new Date(exif.getDateTime()));
                }
                double[] latLong = exif.getLatLong();
                if (latLong != null) {
                    observation.setLatitude(latLong[0]);
                    observation.setLongitude(latLong[1]);
                    addMarkerToMap(latLong[0], latLong[1]);
                }
                photoPosition = observation.getPhotoPaths().size() - 1;
                displayPhoto(photoPosition);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClickOnObservation() {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
            saveObservation(true);
            finish();
        }
    }

    private void saveObservation(boolean withMessage) {
        if (observation != null && observation.getPhotoPaths() != null && observation.getPhotoPaths().size() > 0
                && observation.getLatitude() != null && observation.getLongitude() != null)  {
            DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
            // by user, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                    .child(observation.getId())
                    .setValue(observation);
            // by user, by plant, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(observation.getPlant())
                    .child(observation.getId())
                    .setValue(observation);

            if (withMessage) {
                Toast.makeText(this, R.string.observation_saved, Toast.LENGTH_LONG).show();
            }
        } else {
            if (withMessage) {
                Toast.makeText(this, R.string.observation_not_saved, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void deleteObservation(Observation observation) {
        for (String photoPath : observation.getPhotoPaths()) {
            File photoFile = new File( getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + photoPath);
            if (photoFile.exists()) {
                photoFile.delete();
            }
        }
        DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        // by user, by date
        mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                .child(currentUser.getUid())
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                .child(observation.getId())
                .setValue(null);
        // by user, by plant, by date
        mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                .child(currentUser.getUid())
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                .child(observation.getPlant())
                .child(observation.getId())
                .setValue(null);
    }

    private void initializeObservation() {
        TextView observationDate = (TextView) findViewById(R.id.observation_date);
        observationDate.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(),
                AndroidConstants.DATE_SKELETON), observation.getDate()));

        MapView mapView = (MapView) findViewById(R.id.observation_map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        final ImageView photo = (ImageView) findViewById(R.id.observation_photo);
        final ImageView prevPhoto = (ImageView) findViewById(R.id.observation_prev_photo);
        final ImageView nextPhoto = (ImageView) findViewById(R.id.observation_next_photo);

        photo.setImageResource(android.R.color.transparent);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = dm.widthPixels - Utils.convertDpToPx(20, dm);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            size = size / 2;
        }
        photo.getLayoutParams().width = size;
        photo.getLayoutParams().height = size;

        prevPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    displayPhoto(--photoPosition);
                }
            }
        });

        nextPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    displayPhoto(++photoPosition);
                }
            }
        });

        displayPhoto(photoPosition);

        final EditText observationNote = (EditText) findViewById(R.id.observation_note);
        observationNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                observation.setNote(editable.toString());
            }
        });
        if (observation.getNote() != null) {
            observationNote.setText(observation.getNote());
        }
    }

    private AlertDialog DeleteConfirmationDialog(final Observation observation) {
        return new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(R.string.observation_delete)
                .setMessage(R.string.observation_delete_question)
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteObservation(observation);
                        dialog.dismiss();
                        finish();
                    }

                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void displayPhoto(int position) {
        final ImageView photo = (ImageView) findViewById(R.id.observation_photo);
        final ImageButton prevPhoto = (ImageButton) findViewById(R.id.observation_prev_photo);
        final ImageButton nextPhoto = (ImageButton) findViewById(R.id.observation_next_photo);
        final TextView counter = (TextView) findViewById(R.id.observation_photo_counter);

        prevPhoto.setClickable(position > 0);
        nextPhoto.setClickable(position < observation.getPhotoPaths().size() -1);

        String counterText = (observation.getPhotoPaths().size() == 0 ? position : position + 1) + " / " + observation.getPhotoPaths().size();
        counter.setText(counterText);

        if (position >= 0 && position < observation.getPhotoPaths().size()) {
            Utils.displayImage(getApplicationContext().getFilesDir(), observation.getPhotoPaths().get(position),
                    photo, ((BaseApp) getApplication()).getOptions());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLong = new LatLng(observation.getLatitude(), observation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f));
        marker = map.addMarker(new MarkerOptions().position(latLong));
    }

    private void addMarkerToMap(Double latitude, Double longitude) {
        observation.setLatitude(latitude);
        observation.setLongitude(longitude);
        LatLng latLong = new LatLng(observation.getLatitude(), observation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f));
        marker.setPosition(latLong);
    }
}
