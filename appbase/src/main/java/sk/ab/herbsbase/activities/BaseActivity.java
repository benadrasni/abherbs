package sk.ab.herbsbase.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

import sk.ab.common.Constants;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsbase.tools.Utils;

/**
 * User: adrian
 * Date: 11.1.2015
 * <p/>
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected boolean isLoading = false;
    protected View overlay;
    protected DrawerLayout mDrawerLayout;
    protected PropertyListBaseFragment mPropertyMenu;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected AnimationDrawable loadingAnimation;
    protected HashMap<String, String> filter;

    protected int count;

    public FloatingActionButton countButton;
    public TextView countText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeLocale();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        loadingAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.loading, null);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (changeLocale()) {
            Log.i(TAG, "Locale changed in onStart() method.");
            recreate();
        }

        mPropertyMenu.getListView().invalidateViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public FloatingActionButton getCountButton() {
        return countButton;
    }

    public BaseApp getApp() {
        return (BaseApp)getApplication();
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
        mDrawerToggle.syncState();
    }

    public void startLoading() {
        overlay.setVisibility(View.VISIBLE);
        if (countButton != null) {
            countText.setVisibility(View.INVISIBLE);
            countButton.setEnabled(false);
            countButton.setImageDrawable(loadingAnimation);
            loadingAnimation.start();
        }
    }

    public void stopLoading() {
        if (countButton != null) {
            countButton.setEnabled(true);
            countText.setVisibility(View.VISIBLE);
        }
        overlay.setVisibility(View.GONE);
    }

    public HashMap<String, String> getFilter() {
        return filter;
    }

    public int getCounter() {
        return count;
    }

    public PropertyListBaseFragment getMenuFragment() {
        return mPropertyMenu;
    }

    protected void setCountButton() {
        BaseApp app = (BaseApp)getApplication();

        if (app.isLoading()) {
            startLoading();
        } else {
            if (countButton != null) {
                if (count <= Constants.LIST_THRESHOLD && count > 0) {
                    countButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.FABGreen)));
                } else {
                    countButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.MenuWhite)));
                }
                countButton.setImageDrawable(null);
                countText.setText("" + count);
            }
        }
    }

    protected boolean changeLocale() {
        SharedPreferences preferences = getSharedPreferences();
        String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

        boolean isLocaleChanged = false;
        if (!Locale.getDefault().getLanguage().equals(language)) {
            Utils.changeLocale(getBaseContext(), language);
            isLocaleChanged = true;
        }

        return isLocaleChanged;
    }

    public abstract SharedPreferences getSharedPreferences();

    protected abstract PropertyListBaseFragment getNewMenuFragment();
}
