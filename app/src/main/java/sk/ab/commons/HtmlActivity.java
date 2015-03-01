package sk.ab.commons;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.tools.Utils;

/**
 *
 * Created by adrian on 17.2.2015.
 */
public class HtmlActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker tracker = ((HerbsApp)getApplication()).getTracker();
        tracker.setScreenName(this.getClass().getSimpleName());
        tracker.send(new HitBuilders.AppViewBuilder().build());

        setContentView(R.layout.html_text);

        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        int title = getIntent().getIntExtra("title", R.string.help);
        getSupportActionBar().setTitle(title);

        WebView html = (WebView) findViewById(R.id.html_text);
        System.out.println("LOCALE: " + Locale.getDefault().getLanguage());
        switch (title) {
            case R.string.donation:
                html.loadUrl("file:///android_asset/" + Locale.getDefault().getLanguage() + "_donation.html");
                break;
            case R.string.feedback:
                html.loadUrl("file:///android_asset/" + Locale.getDefault().getLanguage() + "_feedback.html");
                break;
            case R.string.help:
                html.loadUrl("file:///android_asset/" + Locale.getDefault().getLanguage() + "_help.html");
                break;
            case R.string.about:
                html.loadUrl("file:///android_asset/" + Locale.getDefault().getLanguage() + "_about.html");
                break;
        }
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);
        Utils.changeLocale(this, language);
    }
}
