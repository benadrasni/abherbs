package sk.ab.herbsplus.activities;

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

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;

/**
 *
 * Created by adrian on 1. 5. 2017.
 */

public class FeedbackPlusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.feedback_title);
        }

        initializeReviewButton();

        initializeTranslateButton();
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

}
