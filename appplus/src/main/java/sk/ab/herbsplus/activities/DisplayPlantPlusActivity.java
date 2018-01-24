package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantPlusActivity extends DisplayPlantBaseActivity {

    private static final int RC_SIGN_IN = 123;

    private long mLastClickTime;

    private boolean isFABExpanded = false;

    private List<FloatingActionButton> fabList;
    private FloatingActionButton fabLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);
            fabLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(DisplayPlantPlusActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, SpecificConstants.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
            });
            fabList.add(fabLocation);
        }

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        if (countButton != null) {
            countButton.setVisibility(View.VISIBLE);
            countButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;
                    if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            if (isFABExpanded) {
                                hideFAB();
                            } else {
                                expandFAB();
                            }
                        } else {
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
                                    RC_SIGN_IN);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            //IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                getMenuFragment().manageUserSettings();
                expandFAB();
            } else {
                // Sign in failed, check response for error code
                Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case SpecificConstants.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    hideFABLocation();
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

    private void hideFABLocation() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fabLocation.getLayoutParams();
        AnimationSet animSet = new AnimationSet(false);
        Animation animT;
        float scale = -1 * fabLocation.getHeight() * 6.0f;
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

        fabLocation.setVisibility(View.INVISIBLE);
        fabLocation.setLayoutParams(layoutParams);
        fabLocation.startAnimation(animSet);
        fabLocation.setClickable(false);

        fabList.remove(fabLocation);
    }

    private void addCameraPhoto() {

    }

    private void addGalleryPhoto() {

    }

    private void addNote() {

    }

}
