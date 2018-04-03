package sk.ab.herbsplus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import sk.ab.herbs.billingmodule.BasePlayActivity;
import sk.ab.herbsplus.R;

/**
 *
 * Created by adrian on 13. 2. 2018.
 */

public class SubscriptionActivity extends BasePlayActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.subscription);
        }

        ImageView cc0 = findViewById(R.id.cc0);
        cc0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://creativecommons.org/publicdomain/zero/1.0/"));
                startActivity(aboutIntent);
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.subscription_activity;
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
