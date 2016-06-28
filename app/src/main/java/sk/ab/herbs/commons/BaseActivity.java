package sk.ab.herbs.commons;

import android.content.Context;
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
import android.view.MenuItem;
import android.view.WindowManager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.analytics.HitBuilders;

import java.util.HashMap;
import java.util.Locale;

import sk.ab.herbs.AndroidConstants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;

/**
 * User: adrian
 * Date: 11.1.2015
 * <p/>
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity {

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
        ((HerbsApp)getApplication()).getTracker().setScreenName(this.getClass().getSimpleName());

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);

        String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        Boolean changeLocale = preferences.getBoolean(AndroidConstants.CHANGE_LOCALE_KEY, false);

        if (changeLocale && !Locale.getDefault().getLanguage().equals(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }

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

    protected void closeDrawer() {
        mDrawerLayout.closeDrawers();
        mDrawerToggle.syncState();
    }

    public void startLoading() {
        ((HerbsApp)getApplication()).setLoading(true);
        if (countButton != null) {
            countButton.setEnabled(false);
            countButton.setImageDrawable(loadingAnimation);
            loadingAnimation.start();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoading() {
        ((HerbsApp)getApplication()).setLoading(false);
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
        HerbsApp app = (HerbsApp)getApplication();

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
}
