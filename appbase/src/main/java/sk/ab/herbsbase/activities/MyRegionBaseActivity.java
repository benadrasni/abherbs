package sk.ab.herbsbase.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.commons.SquareBaseImageButton;

public abstract class MyRegionBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_region_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.my_region_title);
        }

        AppCompatButton acbEurope = findViewById(R.id.acbEurope);
        final LinearLayout layoutEurope = findViewById(R.id.WGSRPD_Europe);

        acbEurope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutEurope.getVisibility() == View.GONE) {
                    layoutEurope.setVisibility(View.VISIBLE);
                } else {
                    layoutEurope.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbAfrica = findViewById(R.id.acbAfrica);
        final LinearLayout layoutAfrica = findViewById(R.id.WGSRPD_Africa);

        acbAfrica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAfrica.getVisibility() == View.GONE) {
                    layoutAfrica.setVisibility(View.VISIBLE);
                } else {
                    layoutAfrica.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbAsiaTemperate = findViewById(R.id.acbAsiaTemperate);
        final LinearLayout layoutAsiaTemperate = findViewById(R.id.WGSRPD_Asia_Temperate);

        acbAsiaTemperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAsiaTemperate.getVisibility() == View.GONE) {
                    layoutAsiaTemperate.setVisibility(View.VISIBLE);
                } else {
                    layoutAsiaTemperate.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbAsiaTropical = findViewById(R.id.acbAsiaTropical);
        final LinearLayout layoutAsiaTropical = findViewById(R.id.WGSRPD_Asia_Tropical);

        acbAsiaTropical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAsiaTropical.getVisibility() == View.GONE) {
                    layoutAsiaTropical.setVisibility(View.VISIBLE);
                } else {
                    layoutAsiaTropical.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbAustralasia = findViewById(R.id.acbAustralasia);
        final LinearLayout layoutAustralasia = findViewById(R.id.WGSRPD_Australasia);

        acbAustralasia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAustralasia.getVisibility() == View.GONE) {
                    layoutAustralasia.setVisibility(View.VISIBLE);
                } else {
                    layoutAustralasia.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbPacific = findViewById(R.id.acbPacific);
        final LinearLayout layoutPacific = findViewById(R.id.WGSRPD_Pacific);

        acbPacific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutPacific.getVisibility() == View.GONE) {
                    layoutPacific.setVisibility(View.VISIBLE);
                } else {
                    layoutPacific.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbNorthernAmerica = findViewById(R.id.acbNorthernAmerica);
        final LinearLayout layoutNorthernAmerica = findViewById(R.id.WGSRPD_Northern_America);

        acbNorthernAmerica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutNorthernAmerica.getVisibility() == View.GONE) {
                    layoutNorthernAmerica.setVisibility(View.VISIBLE);
                } else {
                    layoutNorthernAmerica.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbSouthernAmerica = findViewById(R.id.acbSouthernAmerica);
        final LinearLayout layoutSouthernAmerica = findViewById(R.id.WGSRPD_Southern_America);

        acbSouthernAmerica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutSouthernAmerica.getVisibility() == View.GONE) {
                    layoutSouthernAmerica.setVisibility(View.VISIBLE);
                } else {
                    layoutSouthernAmerica.setVisibility(View.GONE);
                }
            }
        });

        AppCompatButton acbAntarctic = findViewById(R.id.acbAntarctic);
        final LinearLayout layoutAntarctic = findViewById(R.id.WGSRPD_Antarctic);

        acbAntarctic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAntarctic.getVisibility() == View.GONE) {
                    layoutAntarctic.setVisibility(View.VISIBLE);
                } else {
                    layoutAntarctic.setVisibility(View.GONE);
                }
            }
        });
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

    public void setMyRegion(View v) {
        final SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AndroidConstants.MY_REGION_KEY, ((SquareBaseImageButton)v).getValue());
        editor.apply();
        finish();
    }

    public abstract SharedPreferences getSharedPreferences();
}
