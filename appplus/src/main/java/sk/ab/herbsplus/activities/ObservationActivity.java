package sk.ab.herbsplus.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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

    private FloatingActionButton fabSave;
    private ImageView observationCamera;
    private ImageView observationGallery;
    private ImageView observationLocation;

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

        fabSave = findViewById(R.id.fab_save);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClickOnObservation();
            }
        });

        observationCamera = findViewById(R.id.observation_camera);
        observationCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCameraPhoto();
            }
        });

        observationGallery = findViewById(R.id.observation_gallery);
        observationGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGalleryPhoto();
            }
        });

        observationLocation = findViewById(R.id.observation_location);
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
                                addMarkerToMap(location.getLatitude(), location.getLongitude(), false);
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
    public void onStart() {
        super.onStart();
        showWizard();
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
                AlertDialog dialogBox = DeleteObservationDialog(observation);
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
                    Toast.makeText(this, R.string.camera_failed, Toast.LENGTH_LONG).show();
                }
                break;
            case AndroidConstants.REQUEST_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    processPhoto(data.getData());
                } else {
                    Toast.makeText(this, R.string.gallery_failed, Toast.LENGTH_LONG).show();
                }
                break;
            case AndroidConstants.REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra(AndroidConstants.STATE_LATITUDE) && data.hasExtra(AndroidConstants.STATE_LONGITUDE)) {
                        double latitude = data.getExtras().getDouble(AndroidConstants.STATE_LATITUDE);
                        double longitude = data.getExtras().getDouble(AndroidConstants.STATE_LONGITUDE);
                        addMarkerToMap(latitude, longitude, true);
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
                                            addMarkerToMap(location.getLatitude(), location.getLongitude(), false);
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (observation.getLatitude() != null && observation.getLongitude() != null) {
            LatLng latLong = new LatLng(observation.getLatitude(), observation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f));
            marker = map.addMarker(new MarkerOptions().position(latLong));
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
        pickPictureIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
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
        String prefix = "unknown_";
        if (observation.getPlant() != null) {
            String[] names = observation.getPlant().toLowerCase().split(" ");
            if (names.length > 1) {
                prefix = names[0].substring(0, 1) + names[1].substring(0, 1) + "_";
            }
        }
        return File.createTempFile(prefix, ".jpg", storageDir);
    }

    private void processPhoto(Uri uri) {
        String dirname = AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                + currentUser.getUid() + AndroidConstants.SEPARATOR
                + observation.getPlant().replace(" ", "_") + AndroidConstants.SEPARATOR;
        String filename = mCurrentPhotoUri.getLastPathSegment();
        String path = this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + dirname + filename;
        File dir = new File(this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + dirname);
        dir.mkdirs();
        try (InputStream inputStream = this.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(path)) {

            if (inputStream != null) {
                ByteStreams.copy(inputStream, outputStream);
                ExifInterface exif = new ExifInterface(path);

                // check duplicates
                for (String photoPath : observation.getPhotoPaths()) {
                    File photoOld = new File(this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + photoPath);
                    if (photoOld.exists()) {
                        ExifInterface exifOld = new ExifInterface(photoOld.getPath());
                        if (exifOld.getDateTime() > -1 && exifOld.getDateTime() == exif.getDateTime()) {
                            File photoNew = new File(path);
                            photoNew.delete();
                            Toast.makeText(this, R.string.photo_duplicate, Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                // fill observation
                observation.getPhotoPaths().add(dirname + filename);
                long timeGPS = exif.getGpsDateTime();
                if (timeGPS > 0) {
                    observation.getDate().setTime(timeGPS);
                } else {
                    long timeLocal = exif.getDateTime();
                    if (timeLocal > 0) {
                        observation.getDate().setTime(timeLocal - TimeZone.getDefault().getOffset(timeLocal));
                    }
                }
                double[] latLong = exif.getLatLong();
                if (latLong != null && latLong[0] != 0 && latLong[1] != 0) {
                    addMarkerToMap(latLong[0], latLong[1], true);
                }
                photoPosition = observation.getPhotoPaths().size() - 1;
                refreshObservation();
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
            saveObservation();
            finish();
        }
    }

    private void saveObservation() {
        if (observation != null && observation.getPhotoPaths() != null && observation.getPhotoPaths().size() > 0
                && observation.getLatitude() != null && observation.getLongitude() != null)  {
            DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
            if (observation.getId() == null) {
                observation.setId(currentUser.getUid() + "_" + observation.getDate().getTime());
            }
            // by user, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .setValue(observation);
            // by user, by plant, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(observation.getPlant())
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .setValue(observation);

            Toast.makeText(this, R.string.observation_saved, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.observation_not_saved, Toast.LENGTH_LONG).show();
        }
    }

    private void deleteObservation(Observation observation) {
        for (String photoPath : observation.getPhotoPaths()) {
            File photoFile = new File( getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + photoPath);
            if (photoFile.exists()) {
                photoFile.delete();
            }
        }
        if (observation.getId() != null) {
            DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
            // by user, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .setValue(null);
            // by user, by plant, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(observation.getPlant())
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .setValue(null);
        }
    }

    private void refreshObservation() {
        TextView observationDate = findViewById(R.id.observation_date);
        observationDate.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(),
                AndroidConstants.DATE_SKELETON), observation.getDate()));

        if (observation.getLatitude() != null && observation.getLongitude() != null) {
            addMarkerToMap(observation.getLatitude(), observation.getLongitude(), false);
        }

        displayPhoto(photoPosition);

        if (observation.getNote() != null) {
            EditText observationNote = findViewById(R.id.observation_note);
            observationNote.setText(observation.getNote());
        }
    }

    private void initializeObservation() {
        final TextView observationDate = findViewById(R.id.observation_date);
        observationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker(observationDate);
            }
        });

        MapView mapView = findViewById(R.id.observation_map);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        ImageView photo = findViewById(R.id.observation_photo);
        photo.setImageResource(android.R.color.transparent);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = dm.widthPixels - Utils.convertDpToPx(20, dm);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            size = size / 2;
        }
        photo.getLayoutParams().width = size;
        photo.getLayoutParams().height = size;

        ImageView prevPhoto = findViewById(R.id.observation_prev_photo);
        prevPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    photoPosition--;
                    if (photoPosition < 0) {
                        photoPosition = observation.getPhotoPaths().size() - 1;
                    }
                    displayPhoto(photoPosition);
                }
            }
        });

        ImageView nextPhoto = findViewById(R.id.observation_next_photo);
        nextPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    photoPosition++;
                    if (photoPosition == observation.getPhotoPaths().size()) {
                        photoPosition = 0;
                    }
                    displayPhoto(photoPosition);
                }
            }
        });

        ImageView deletePhoto = findViewById(R.id.observation_delete_photo);
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    if (observation.getPhotoPaths().size() > 0) {
                        AlertDialog dialogBox = DeletePhotoDialog();
                        dialogBox.show();
                    }
                }
            }
        });

        final EditText observationNote = findViewById(R.id.observation_note);
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

        refreshObservation();
    }

    private void showDateTimePicker(final TextView observationDateTextView) {
        final Calendar observationDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        observationDate.setTime(observation.getDate());
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(ObservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        observation.setDate(date.getTime());
                        observationDateTextView.setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(),
                                AndroidConstants.DATE_SKELETON), observation.getDate()));
                    }
                }, observationDate.get(Calendar.HOUR_OF_DAY), observationDate.get(Calendar.MINUTE), false).show();
            }
        }, observationDate.get(Calendar.YEAR), observationDate.get(Calendar.MONTH), observationDate.get(Calendar.DATE)).show();
    }

    private AlertDialog DeleteObservationDialog(final Observation observation) {
        return new AlertDialog.Builder(this)
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

    private AlertDialog DeletePhotoDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(R.string.photo_delete)
                .setMessage(R.string.photo_delete_question)
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePhoto();
                        dialog.dismiss();
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
        final ImageView photo = findViewById(R.id.observation_photo);
        final TextView counter = findViewById(R.id.observation_photo_counter);

        String counterText = (observation.getPhotoPaths().size() == 0 ? position : position + 1) + " / " + observation.getPhotoPaths().size();
        counter.setText(counterText);

        if (position >= 0 && position < observation.getPhotoPaths().size()) {
            Utils.displayImage(getApplicationContext().getFilesDir(), observation.getPhotoPaths().get(position),
                    photo, ((BaseApp) getApplication()).getOptions());
        } else {
            photo.setImageDrawable(getResources().getDrawable(R.drawable.no_image_available));
        }
    }

    private void deletePhoto() {
        File photoFile = new File( getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + observation.getPhotoPaths().get(photoPosition));
        if (photoFile.exists()) {
            photoFile.delete();
        }
        observation.getPhotoPaths().remove(photoPosition);
        if (photoPosition > 0 && photoPosition == observation.getPhotoPaths().size()) {
            photoPosition--;
        }
        displayPhoto(photoPosition);
    }

    private void addMarkerToMap(Double latitude, Double longitude, boolean rewrite) {
        if (rewrite || observation.getLatitude() == null || observation.getLongitude() == null) {
            observation.setLatitude(latitude);
            observation.setLongitude(longitude);
        }
        if (map != null && observation.getLatitude() != null && observation.getLongitude() != null) {
            LatLng latLong = new LatLng(observation.getLatitude(), observation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f));
            if (marker == null) {
                marker = map.addMarker(new MarkerOptions().position(latLong));
            } else {
                marker.setPosition(latLong);
            }
        }
    }

    private void showWizard() {
        final SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + SpecificConstants.VERSION_2_0_0, false);
        if (showWizard) {
            showWizardCamera();
            editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + SpecificConstants.VERSION_2_0_0, true);
            editor.apply();
        }
    }

    private void showWizardCamera() {
        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(observationCamera))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_camera_title)
                .setContentText(R.string.showcase_camera_message)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showWizardGallery();
                    }
                })
                .build();
    }

    private void showWizardGallery() {
        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(observationGallery))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_gallery_title)
                .setContentText(R.string.showcase_gallery_message)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showWizardLocation();
                    }
                })
                .build();
    }

    private void showWizardLocation() {
        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(observationLocation))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_location_title)
                .setContentText(R.string.showcase_location_message)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showWizardSave();
                    }
                })
                .build();
    }

    private void showWizardSave() {
        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(fabSave))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_save_title)
                .setContentText(R.string.showcase_save_message)
                .build();
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        showcaseView.setButtonPosition(lps);
    }
}
