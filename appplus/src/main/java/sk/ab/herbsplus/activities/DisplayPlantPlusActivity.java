package sk.ab.herbsplus.activities;

import android.content.Context;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsbase.fragments.GalleryFragment;
import sk.ab.herbsbase.fragments.InfoFragment;
import sk.ab.herbsbase.fragments.SourcesFragment;
import sk.ab.herbsbase.fragments.TaxonomyFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.fragments.ObservationFragment;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantPlusActivity extends DisplayPlantBaseActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseUser currentUser;
    private Observation observation;

    private long mLastClickTime;
    private boolean isFABExpanded;
    private boolean isFABClicked;
    private Location mLastLocation;
    private Uri mCurrentPhotoUri;

    private CoordinatorLayout mCoordinatorLayout;
    private List<FloatingActionButton> fabList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl);

        isFABExpanded = false;
        isFABClicked = false;
        fabList = new ArrayList<>();
        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCameraPhoto();
            }
        });
        fabList.add(fabCamera);

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGalleryPhoto();
            }
        });
        fabList.add(fabGallery);

        FloatingActionButton fabNote = (FloatingActionButton) findViewById(R.id.fab_note);
        fabNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
        fabList.add(fabNote);

        FloatingActionButton fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });
        fabList.add(fabLocation);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                            }
                        }
                    });
        }

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        if (countButton != null) {
            countButton.setVisibility(View.VISIBLE);
            countButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClickOnObservation();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        saveObservation(false);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AndroidConstants.REQUEST_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    getMenuFragment().manageUserSettings();
                    if (isFABClicked) {
                        expandFAB();
                        isFABClicked = false;
                    }
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Fragment observationFragment = getSupportFragmentManager().findFragmentByTag("Observation");
                    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.detach(observationFragment);
                    ft.attach(observationFragment);
                    ft.commit();
                } else {
                    // Sign in failed, check response for error code
                    Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
                }
                break;
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
                    if (data.hasExtra(AndroidConstants.STATE_LATITUDE)) {
                        double latitude = data.getExtras().getDouble(AndroidConstants.STATE_LATITUDE);
                        double longitude = data.getExtras().getDouble(AndroidConstants.STATE_LONGITUDE);
                        observation.setLatitude(latitude);
                        observation.setLongitude(longitude);
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
                                        if (observation != null && (observation.getLatitude() == null || observation.getLongitude() == null)) {
                                            observation.setLatitude(location.getLatitude());
                                            observation.setLongitude(location.getLongitude());
                                        }
                                    }
                                }
                            });
                }
            }
        }
    }

    @Override
    protected PropertyListBaseFragment getNewMenuFragment() {
        return new PropertyListPlusFragment();
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected void addFragments(FragmentTransaction ft) {
        ft.replace(sk.ab.herbsbase.R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
        ft.replace(sk.ab.herbsbase.R.id.info_fragment, new InfoFragment(), "Info");
        ft.replace(sk.ab.herbsbase.R.id.gallery_fragment, new GalleryFragment(), "Gallery");
        ft.replace(sk.ab.herbsbase.R.id.observation_fragment, new ObservationFragment(), "Observation");
        ft.replace(sk.ab.herbsbase.R.id.sources_fragment, new SourcesFragment(), "Sources");
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    public void expandFAB() {
        if (!isFABExpanded) {
            float marginScale = 1.5f;
            for (FloatingActionButton fab : fabList) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();

                AnimationSet animSet = new AnimationSet(false);
                animSet.setFillAfter(true);
                Animation animT;
                float scale = fab.getHeight() * marginScale;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutParams.bottomMargin += (int) scale;
                    animT = new TranslateAnimation(0f, 0f, scale, 0f);
                } else {
                    layoutParams.rightMargin += (int) scale;
                    animT = new TranslateAnimation(scale, 0f, 0f, 0f);
                }
                animT.setDuration(1000);
                animT.setInterpolator(new LinearInterpolator());
                animSet.addAnimation(animT);
                Animation animA =  new AlphaAnimation(0f, 1f);
                animA.setDuration(2000);
                animA.setInterpolator(new DecelerateInterpolator());
                animSet.addAnimation(animA);

                fab.setVisibility(View.VISIBLE);
                fab.setLayoutParams(layoutParams);
                fab.startAnimation(animSet);
                fab.setClickable(true);

                marginScale += 1.5;
            }
            isFABExpanded = true;
        }
    }

    public void hideFAB() {
        if (isFABExpanded) {
            float marginScale = 1.5f;
            for (FloatingActionButton fab : fabList) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
                AnimationSet animSet = new AnimationSet(false);
                Animation animT;
                float scale = -1 * fab.getHeight() * marginScale;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    layoutParams.bottomMargin += (int) scale;
                    animT = new TranslateAnimation(0f, 0f, scale, 0f);
                } else {
                    layoutParams.rightMargin += (int) scale;
                    animT = new TranslateAnimation(scale, 0f, 0f, 0f);
                }

                animT.setDuration(1000);
                animT.setInterpolator(new LinearInterpolator());
                animSet.addAnimation(animT);
                Animation animA =  new AlphaAnimation(1f, 0f);
                animA.setDuration(2000);
                animA.setInterpolator(new AccelerateInterpolator());
                animSet.addAnimation(animA);

                fab.setVisibility(View.INVISIBLE);
                fab.setLayoutParams(layoutParams);
                fab.startAnimation(animSet);
                fab.setClickable(false);

                marginScale += 1.5;
            }
            isFABExpanded = false;
        }
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
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

    private void addNote() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View noteView = inflater.inflate(R.layout.note_layout,null);
        final PopupWindow mPopupWindow = new PopupWindow(
                noteView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                true
        );
        final EditText note = noteView.findViewById(R.id.note);
        note.setText(observation.getNote());

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                observation.setNote(note.getText().toString());
            }
        });
        mPopupWindow.showAtLocation(mCoordinatorLayout, Gravity.CENTER,0,0);
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
                                Intent intent = new Intent(DisplayPlantPlusActivity.this, MapActivity.class);

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
                + currentUser.getUid() + AndroidConstants.SEPARATOR + getPlant().getName() + AndroidConstants.SEPARATOR;
        String filename = mCurrentPhotoUri.getLastPathSegment();
        String path = this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + dirname + filename;
        File dir = new File(this.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + dirname);
        dir.mkdirs();
        try (InputStream inputStream = this.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(path)) {

            if (inputStream != null) {
                ByteStreams.copy(inputStream, outputStream);
                ExifInterface exif = new ExifInterface(path);

                // fill observation
                observation.getPhotoPaths().add(dirname + filename);
                if (exif.getDateTime() > 0) {
                    observation.setDate(new Date(exif.getDateTime()));
                }
                double[] latLong = exif.getLatLong();
                if (latLong != null) {
                    observation.setLatitude(latLong[0]);
                    observation.setLongitude(latLong[1]);
                }
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
            if (currentUser != null) {
                if (isFABExpanded) {
                    hideFAB();
                    saveObservation(true);
                    countButton.setImageResource(R.drawable.ic_remove_red_eye_black_24dp);
                } else {
                    expandFAB();
                    if (observation == null) {
                        Date date =  new Date();
                        observation = new Observation();
                        observation.setId(currentUser.getUid() + "_" + date.getTime());
                        observation.setDate(date);
                        observation.setPlant(getPlant().getName());
                        observation.setPhotoPaths(new ArrayList<String>());
                    }
                    countButton.setImageResource(R.drawable.ic_save_black_24dp);
                }
            } else {
                isFABClicked = true;
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        AndroidConstants.REQUEST_SIGN_IN);
            }
        }
    }

    private void saveObservation(boolean withMessage) {
        if (observation != null && observation.getPhotoPaths() != null && observation.getPhotoPaths().size() > 0) {
            if (observation.getLatitude() == null || observation.getLongitude() == null) {
                if (mLastLocation != null) {
                    observation.setLatitude(mLastLocation.getLatitude());
                    observation.setLongitude(mLastLocation.getLongitude());
                } else {
                    if (withMessage) {
                        Toast.makeText(this, R.string.observation_not_saved, Toast.LENGTH_LONG).show();
                    }
                    return;
                }
            }

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
}
