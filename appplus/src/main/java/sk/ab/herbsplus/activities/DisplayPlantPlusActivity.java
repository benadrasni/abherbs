package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
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
    private FloatingActionButton fabCamera;
    private FloatingActionButton fabGallery;
    private FloatingActionButton fabNote;
    private FloatingActionButton fabLocation;

    //Animations
    Animation showFabCamera;
    Animation hideFabCamera;
    Animation showFabGallery;
    Animation hideFabGallery;
    Animation showFabNote;
    Animation hideFabNote;
    Animation showFabLocation;
    Animation hideFabLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabNote = (FloatingActionButton) findViewById(R.id.fab_note);
        fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);

        fabList = new ArrayList<>();
        fabList.add(fabCamera);
        fabList.add(fabGallery);
        fabList.add(fabNote);
        fabList.add(fabLocation);

        showFabCamera = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_camera_show);
        hideFabCamera = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_camera_hide);
        showFabGallery = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_gallery_show);
        hideFabGallery = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_gallery_hide);
        showFabNote = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_note_show);
        hideFabNote = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_note_hide);
        showFabLocation = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_location_show);
        hideFabLocation = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_location_hide);

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
            float bottomMarginScale = 1.5f;
            for (FloatingActionButton fab : fabList) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
                layoutParams.bottomMargin += (int) (fab.getHeight() * bottomMarginScale);

                AnimationSet animSet = new AnimationSet(false);
                Animation animT = new TranslateAnimation(0f, 0f, 150f, 0f);
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

                bottomMarginScale += 1.5;
            }

//            FrameLayout.LayoutParams layoutParamsCamera = (FrameLayout.LayoutParams) fabCamera.getLayoutParams();
//            layoutParamsCamera.bottomMargin += (int) (fabCamera.getHeight() * 1.5);
//            fabCamera.setVisibility(View.VISIBLE);
//            fabCamera.setLayoutParams(layoutParamsCamera);
//            fabCamera.startAnimation(showFabCamera);
//            fabCamera.setClickable(true);
//
//            FrameLayout.LayoutParams layoutParamsGallery = (FrameLayout.LayoutParams) fabGallery.getLayoutParams();
//            layoutParamsGallery.bottomMargin += (int) (fabGallery.getHeight() * 3.0);
//            fabGallery.setVisibility(View.VISIBLE);
//            fabGallery.setLayoutParams(layoutParamsGallery);
//            fabGallery.startAnimation(showFabGallery);
//            fabGallery.setClickable(true);
//
//            FrameLayout.LayoutParams layoutParamsNote = (FrameLayout.LayoutParams) fabNote.getLayoutParams();
//            layoutParamsNote.bottomMargin += (int) (fabNote.getHeight() * 4.5);
//            fabNote.setVisibility(View.VISIBLE);
//            fabNote.setLayoutParams(layoutParamsNote);
//            fabNote.startAnimation(showFabNote);
//            fabNote.setClickable(true);
//
//            FrameLayout.LayoutParams layoutParamsLocation = (FrameLayout.LayoutParams) fabLocation.getLayoutParams();
//            layoutParamsLocation.bottomMargin += (int) (fabLocation.getHeight() * 6.0);
//            fabLocation.setVisibility(View.VISIBLE);
//            fabLocation.setLayoutParams(layoutParamsLocation);
//            fabLocation.startAnimation(showFabLocation);
//            fabLocation.setClickable(true);

            isFABExpanded = true;
        }
    }


    public void hideFAB() {
        if (isFABExpanded) {
            float bottomMarginScale = 1.5f;
            for (FloatingActionButton fab : fabList) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
                layoutParams.bottomMargin -= (int) (fab.getHeight() * bottomMarginScale);

                AnimationSet animSet = new AnimationSet(false);
                Animation animT = new TranslateAnimation(0f, 0f, -150f, 0f);
                animT.setDuration(1000);
                animT.setInterpolator(new LinearInterpolator());
                animSet.addAnimation(animT);
                Animation animA =  new AlphaAnimation(1f, 0f);
                animA.setDuration(2000);
                animA.setInterpolator(new DecelerateInterpolator());
                animSet.addAnimation(animA);

                fab.setVisibility(View.VISIBLE);
                fab.setLayoutParams(layoutParams);
                fab.startAnimation(animSet);
                fab.setClickable(true);

                bottomMarginScale += 1.5;
            }

//            FrameLayout.LayoutParams layoutParamsCamera = (FrameLayout.LayoutParams) fabCamera.getLayoutParams();
//            layoutParamsCamera.bottomMargin -= (int) (fabCamera.getHeight() * 1.5);
//            fabCamera.setLayoutParams(layoutParamsCamera);
//            fabCamera.startAnimation(hideFabCamera);
//            fabCamera.setClickable(false);
//            fabCamera.setVisibility(View.INVISIBLE);
//
//            FrameLayout.LayoutParams layoutParamsGallery = (FrameLayout.LayoutParams) fabGallery.getLayoutParams();
//            layoutParamsGallery.bottomMargin -= (int) (fabGallery.getHeight() * 3.0);
//            fabGallery.setLayoutParams(layoutParamsGallery);
//            fabGallery.startAnimation(hideFabGallery);
//            fabGallery.setClickable(false);
//            fabGallery.setVisibility(View.INVISIBLE);
//
//            FrameLayout.LayoutParams layoutParamsNote = (FrameLayout.LayoutParams) fabNote.getLayoutParams();
//            layoutParamsNote.bottomMargin -= (int) (fabNote.getHeight() * 4.5);
//            fabNote.setLayoutParams(layoutParamsNote);
//            fabNote.startAnimation(hideFabNote);
//            fabNote.setClickable(false);
//            fabNote.setVisibility(View.INVISIBLE);
//
//            FrameLayout.LayoutParams layoutParamsLocation = (FrameLayout.LayoutParams) fabLocation.getLayoutParams();
//            layoutParamsLocation.bottomMargin -= (int) (fabLocation.getHeight() * 6.0);
//            fabLocation.setLayoutParams(layoutParamsLocation);
//            fabLocation.startAnimation(hideFabLocation);
//            fabLocation.setClickable(false);
//            fabLocation.setVisibility(View.INVISIBLE);

            isFABExpanded = false;
        }
    }

}
