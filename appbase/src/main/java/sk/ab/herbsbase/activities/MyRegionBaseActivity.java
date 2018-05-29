package sk.ab.herbsbase.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Map;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.commons.SquareBaseImageButton;

public abstract class MyRegionBaseActivity extends AppCompatActivity {

    private LinearLayout layoutEurope;
    private LinearLayout layoutAfrica;
    private LinearLayout layoutAsiaTemperate;
    private LinearLayout layoutAsiaTropical;
    private LinearLayout layoutAustralasia;
    private LinearLayout layoutPacific;
    private LinearLayout layoutNorthernAmerica;
    private LinearLayout layoutSouthernAmerica;
    private LinearLayout layoutAntarctic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_region_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.my_region_title);
        }

        AppCompatButton acbEurope = findViewById(R.id.acbEurope);
        layoutEurope = findViewById(R.id.WGSRPD_Europe);

        acbEurope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutEurope, 1);
            }
        });

        AppCompatButton acbAfrica = findViewById(R.id.acbAfrica);
        layoutAfrica = findViewById(R.id.WGSRPD_Africa);

        acbAfrica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutAfrica, 2);
            }
        });

        AppCompatButton acbAsiaTemperate = findViewById(R.id.acbAsiaTemperate);
        layoutAsiaTemperate = findViewById(R.id.WGSRPD_Asia_Temperate);

        acbAsiaTemperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutAsiaTemperate, 3);
            }
        });

        AppCompatButton acbAsiaTropical = findViewById(R.id.acbAsiaTropical);
        layoutAsiaTropical = findViewById(R.id.WGSRPD_Asia_Tropical);

        acbAsiaTropical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutAsiaTropical, 4);
            }
        });

        AppCompatButton acbAustralasia = findViewById(R.id.acbAustralasia);
        layoutAustralasia = findViewById(R.id.WGSRPD_Australasia);

        acbAustralasia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutAustralasia, 5);
            }
        });

        AppCompatButton acbPacific = findViewById(R.id.acbPacific);
        layoutPacific = findViewById(R.id.WGSRPD_Pacific);

        acbPacific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutPacific, 6);
            }
        });

        AppCompatButton acbNorthernAmerica = findViewById(R.id.acbNorthernAmerica);
        layoutNorthernAmerica = findViewById(R.id.WGSRPD_Northern_America);

        acbNorthernAmerica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutNorthernAmerica, 7);
            }
        });

        AppCompatButton acbSouthernAmerica = findViewById(R.id.acbSouthernAmerica);
        layoutSouthernAmerica = findViewById(R.id.WGSRPD_Southern_America);

        acbSouthernAmerica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutSouthernAmerica, 8);
            }
        });

        AppCompatButton acbAntarctic = findViewById(R.id.acbAntarctic);
        layoutAntarctic = findViewById(R.id.WGSRPD_Antarctic);

        acbAntarctic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClick(layoutAntarctic, 9);
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

    private void handleClick(LinearLayout layoutFirstLevel, int region) {
        closeSecondLevel(layoutFirstLevel);
        if (layoutFirstLevel.getChildCount() > 0) {
            layoutFirstLevel.removeAllViews();
        } else {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            lp.setMargins(margin, margin, margin, margin);

            Map<String, Integer> secondLevel1 = null;
            int i = 0;
            for (Map<String, Integer> secondLevel : AndroidConstants.wgsrpd.get(region)) {
                i++;
                if (i%2 == 1) {
                    secondLevel1 = secondLevel;
                } else {

                    LinearLayout ll = new LinearLayout(layoutEurope.getContext());
                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.setWeightSum(2);

                    SquareBaseImageButton sbib1 = new SquareBaseImageButton(ll.getContext());
                    sbib1.setLayoutParams(lp);
                    sbib1.setText(secondLevel1.get("text"));
                    sbib1.setBackgroundResource(secondLevel1.get("drawable"));
                    sbib1.setValue(getResources().getString(secondLevel1.get("value")));
                    sbib1.setAddDefaultListener(false);
                    sbib1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setMyRegion(view);
                        }
                    });

                    SquareBaseImageButton sbib2 = new SquareBaseImageButton(ll.getContext());
                    sbib2.setLayoutParams(lp);
                    sbib2.setText(secondLevel.get("text"));
                    sbib2.setBackgroundResource(secondLevel.get("drawable"));
                    sbib2.setValue(getResources().getString(secondLevel.get("value")));
                    sbib2.setAddDefaultListener(false);
                    sbib2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setMyRegion(view);
                        }
                    });

                    ll.addView(sbib1);
                    ll.addView(sbib2);

                    layoutFirstLevel.addView(ll);
                }
            }

            if (i%2 == 1) {
                LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 2);
                lpButton.setMargins(margin, margin, margin, margin);

                LinearLayout ll = new LinearLayout(layoutFirstLevel.getContext());
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                ll.setWeightSum(4);

                View viewBefore = new View(ll.getContext());
                viewBefore.setLayoutParams(lp);

                SquareBaseImageButton sbib1 = new SquareBaseImageButton(ll.getContext());
                sbib1.setLayoutParams(lpButton);
                sbib1.setText(secondLevel1.get("text"));
                sbib1.setBackgroundResource(secondLevel1.get("drawable"));
                sbib1.setValue(getResources().getString(secondLevel1.get("value")));
                sbib1.setAddDefaultListener(false);
                sbib1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setMyRegion(view);
                    }
                });

                View viewAfter = new View(ll.getContext());
                viewAfter.setLayoutParams(lp);

                ll.addView(viewBefore);
                ll.addView(sbib1);
                ll.addView(viewAfter);

                layoutFirstLevel.addView(ll);
            }
        }
    }

    private void closeSecondLevel(LinearLayout currentLayout) {
        if (layoutEurope != currentLayout && layoutEurope.getChildCount() > 0) {
            layoutEurope.removeAllViews();
        }
        if (layoutAfrica != currentLayout && layoutAfrica.getChildCount() > 0) {
            layoutAfrica.removeAllViews();
        }
        if (layoutAsiaTemperate != currentLayout && layoutAsiaTemperate.getChildCount() > 0) {
            layoutAsiaTemperate.removeAllViews();
        }
        if (layoutAsiaTropical != currentLayout && layoutAsiaTropical.getChildCount() > 0) {
            layoutAsiaTropical.removeAllViews();
        }
        if (layoutAustralasia != currentLayout && layoutAustralasia.getChildCount() > 0) {
            layoutAustralasia.removeAllViews();
        }
        if (layoutPacific != currentLayout && layoutPacific.getChildCount() > 0) {
            layoutPacific.removeAllViews();
        }
        if (layoutNorthernAmerica != currentLayout && layoutNorthernAmerica.getChildCount() > 0) {
            layoutNorthernAmerica.removeAllViews();
        }
        if (layoutSouthernAmerica != currentLayout && layoutSouthernAmerica.getChildCount() > 0) {
            layoutSouthernAmerica.removeAllViews();
        }
        if (layoutAntarctic != currentLayout && layoutAntarctic.getChildCount() > 0) {
            layoutAntarctic.removeAllViews();
        }
    }
}
