package sk.ab.herbs.activities;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import sk.ab.herbs.R;
import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.AndroidConstants;

/**
 *
 * Created by adrian on 1. 5. 2017.
 */

public class FeedbackActivity extends AppCompatActivity {

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;

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

        //initializeInAppBilling();
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
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
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
        Button submitTranslate = (Button)findViewById(R.id.contribution_submit_email);
        submitTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = getString(sk.ab.herbsbase.R.string.email_subject_prefix);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", AndroidConstants.EMAIL, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                startActivity(Intent.createChooser(emailIntent, subject));
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

    private void initializeInAppBilling() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }
}
