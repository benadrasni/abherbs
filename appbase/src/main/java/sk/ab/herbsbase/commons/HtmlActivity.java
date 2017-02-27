package sk.ab.herbsbase.commons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;

/**
 *
 * Created by adrian on 17.2.2015.
 */
public class HtmlActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker tracker = ((BaseApp)getApplication()).getTracker();
        tracker.setScreenName(this.getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        setContentView(R.layout.html_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String title = getIntent().getStringExtra("title");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        WebView html = (WebView) findViewById(R.id.html_text);
        String language = Locale.getDefault().getLanguage();
        if (!language.equals(AndroidConstants.LANGUAGE_SK)) {
            language = AndroidConstants.LANGUAGE_EN;
        }
        if (html != null) {
            switch (title) {
                case AndroidConstants.ITEM_FEEDBACK:
                    html.loadUrl("file:///android_asset/" + language + "_feedback.html");
                    break;
                case AndroidConstants.ITEM_HELP:
                    html.loadUrl("file:///android_asset/" + language + "_help.html");
                    break;
                case AndroidConstants.ITEM_ABOUT:
                    html.loadUrl("file:///android_asset/" + language + "_about.html");
                    break;
            }
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
}
