package sk.ab.herbs.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sk.ab.herbs.R;
import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.AndroidConstants;

/**
 *
 * Created by adrian on 1. 5. 2017.
 */

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.feedback_title);
        }

        initializeReviewButton();

        initializeTranslateButton();

        initializeBuyButton();

        initializeAdsButton();
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

    private void initializeReviewButton() {
        final SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);

        Button submitReview = (Button)findViewById(R.id.contribution_submit_review);
        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_DONE);
                editor.apply();

                Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));
                }
            }
        });
    }

    private void initializeTranslateButton() {
        Button submitTranslateData = findViewById(R.id.contribution_submit_translate_data);
        submitTranslateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(AndroidConstants.WEB_URL
                        + "translate_flower"));
                startActivity(browserIntent);
            }
        });

        Button submitTranslateApp = findViewById(R.id.contribution_submit_translate_app);
        submitTranslateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(AndroidConstants.WEB_URL
                        + "translate_app"));
                startActivity(browserIntent);
            }
        });
    }

    private void initializeBuyButton() {
        Button submitBuy = (Button)findViewById(R.id.contribution_submit_buy);
        submitBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + SpecificConstants.EXTENDED_PACKAGE);
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + SpecificConstants.EXTENDED_PACKAGE)));
                }
            }
        });
    }

    private void initializeAdsButton() {
        final SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);

        final Button submitAds = (Button)findViewById(R.id.contribution_submit_ads);
        final TextView adsText = (TextView) findViewById(R.id.contribution_ads);
        boolean showAds = preferences.getBoolean(SpecificConstants.SHOW_ADS_KEY, true);
        if (showAds) {
            submitAds.setText(getResources().getText(R.string.feedback_disable_ads));
            adsText.setText(getResources().getText(R.string.feedback_ads_allowed));
        } else {
            submitAds.setText(getResources().getText(R.string.feedback_enable_ads));
            adsText.setText(getResources().getText(R.string.feedback_ads_not_allowed));
        }

        submitAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean showAds = preferences.getBoolean(SpecificConstants.SHOW_ADS_KEY, true);
                SharedPreferences.Editor editor = preferences.edit();
                if (showAds) {
                    editor.putBoolean(SpecificConstants.SHOW_ADS_KEY, false);
                    editor.apply();
                    submitAds.setText(getResources().getText(R.string.feedback_enable_ads));
                    adsText.setText(getResources().getText(R.string.feedback_ads_not_allowed));
                } else {
                    editor.remove(SpecificConstants.SHOW_ADS_KEY);
                    editor.apply();
                    submitAds.setText(getResources().getText(R.string.feedback_disable_ads));
                    adsText.setText(getResources().getText(R.string.feedback_ads_allowed));
                }
            }
        });
    }
}
