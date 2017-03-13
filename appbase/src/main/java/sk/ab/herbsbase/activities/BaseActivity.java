package sk.ab.herbsbase.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.WindowManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.analytics.HitBuilders;

import java.util.HashMap;
import java.util.Locale;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.commons.PropertyListFragment;

/**
 * User: adrian
 * Date: 11.1.2015
 * <p/>
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected DrawerLayout mDrawerLayout;
    protected PropertyListFragment mPropertyMenu;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected AnimationDrawable loadingAnimation;
    protected HitBuilders.ScreenViewBuilder builder;
    protected HashMap<String, String> filter;

    protected int count;

    public FloatingActionButton countButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builder = new HitBuilders.ScreenViewBuilder();
        ((BaseApp)getApplication()).getTracker().setScreenName(this.getClass().getSimpleName());

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
        ((BaseApp)getApplication()).setLoading(true);
        if (countButton != null) {
            countButton.setEnabled(false);
            countButton.setImageDrawable(loadingAnimation);
            loadingAnimation.start();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoading() {
        ((BaseApp)getApplication()).setLoading(false);
        if (countButton != null) {
            countButton.setEnabled(true);
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public HashMap<String, String> getFilter() {
        return filter;
    }

    public int getCounter() {
        return count;
    }

    protected void setCountButton() {
        BaseApp app = (BaseApp)getApplication();

        if (app.isLoading()) {
            startLoading();
        } else {
            if (countButton != null) {
                TextDrawable countDrawable;
                int fontSize = countButton.getHeight() / 3;
                if (count <= AndroidConstants.LIST_THRESHOLD && count > 0) {
                    countButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.FABGreen)));

                    countDrawable = TextDrawable.builder()
                            .beginConfig()
                            .useFont(Typeface.DEFAULT)
                            .textColor(Color.BLACK)
                            .fontSize(fontSize) /* size in px */
                            .bold()
                            .endConfig()
                            .buildRound("" + count, ContextCompat.getColor(getApplicationContext(), R.color.FABGreen));
                } else {
                    countButton.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.MenuWhite)));
                    countDrawable = TextDrawable.builder()
                            .beginConfig()
                            .useFont(Typeface.DEFAULT)
                            .textColor(Color.BLACK)
                            .fontSize(fontSize) /* size in px */
                            .bold()
                            .endConfig()
                            .buildRound("" + count, ContextCompat.getColor(getApplicationContext(), R.color.MenuWhite));
                }
                countButton.setImageDrawable(countDrawable);
            }
        }
    }

    protected boolean changeLocale() {
        SharedPreferences preferences = getSharedPreferences();
        String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        Boolean changeLocale = preferences.getBoolean(AndroidConstants.CHANGE_LOCALE_KEY, false);

        boolean isLocaleChanged = false;
        if (changeLocale && !Locale.getDefault().getLanguage().equals(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = getResources().getConfiguration();
            config.setLocale(locale);
            getBaseContext().createConfigurationContext(config);
            isLocaleChanged = true;
        }

        return isLocaleChanged;
    }

    protected abstract SharedPreferences getSharedPreferences();

    protected PropertyListFragment getMenuFragment() {
        return new PropertyListFragment();
    }
}
