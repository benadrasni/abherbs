package sk.ab.herbsbase.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import sk.ab.common.Constants;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;
import sk.ab.herbsbase.commons.BaseFilterFragment;

public class Distribution extends BaseFilterFragment {

    private LinearLayout layoutEurope;
    private LinearLayout layoutAfrica;
    private LinearLayout layoutAsiaTemperate;
    private LinearLayout layoutAsiaTropical;
    private LinearLayout layoutAustralasia;
    private LinearLayout layoutPacific;
    private LinearLayout layoutNorthernAmerica;
    private LinearLayout layoutSouthernAmerica;
    private LinearLayout layoutAntarctic;

    public Distribution() {
        this.attribute = Constants.DISTRIBUTION;
        this.title = R.string.distribution;
        this.iconRes = R.drawable.distribution;
        this.layout = R.layout.distribution;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {
            AppCompatButton acbMyRegion = getView().findViewById(R.id.acbMyRegion);
            TextView myRegionText = getView().findViewById(R.id.my_region_value);

            final SharedPreferences preferences = ((FilterPlantsBaseActivity)getActivity()).getSharedPreferences();
            final String myRegion = preferences.getString(AndroidConstants.MY_REGION_KEY, null);
            if (myRegion != null) {
                myRegionText.setText(AndroidConstants.filterResources.get(myRegion));
            }

            acbMyRegion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FilterPlantsBaseActivity activity = (FilterPlantsBaseActivity)getActivity();
                    if (activity != null) {
                        String myRegion = preferences.getString(AndroidConstants.MY_REGION_KEY, null);

                        if (myRegion != null) {
                            activity.addToFilter(myRegion);
                        } else {
                            Intent intent = new Intent(activity, activity.getUserPreferenceActivityClass());
                            startActivity(intent);
                        }
                    }
                }
            });

            AppCompatButton acbEurope = getView().findViewById(R.id.acbEurope);
            layoutEurope = getView().findViewById(R.id.WGSRPD_Europe);

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

            AppCompatButton acbAfrica = getView().findViewById(R.id.acbAfrica);
            layoutAfrica = getView().findViewById(R.id.WGSRPD_Africa);

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

            AppCompatButton acbAsiaTemperate = getView().findViewById(R.id.acbAsiaTemperate);
            layoutAsiaTemperate = getView().findViewById(R.id.WGSRPD_Asia_Temperate);

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

            AppCompatButton acbAsiaTropical = getView().findViewById(R.id.acbAsiaTropical);
            layoutAsiaTropical = getView().findViewById(R.id.WGSRPD_Asia_Tropical);

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

            AppCompatButton acbAustralasia = getView().findViewById(R.id.acbAustralasia);
            layoutAustralasia = getView().findViewById(R.id.WGSRPD_Australasia);

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

            AppCompatButton acbPacific = getView().findViewById(R.id.acbPacific);
            layoutPacific = getView().findViewById(R.id.WGSRPD_Pacific);

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

            AppCompatButton acbNorthernAmerica = getView().findViewById(R.id.acbNorthernAmerica);
            layoutNorthernAmerica = getView().findViewById(R.id.WGSRPD_Northern_America);

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

            AppCompatButton acbSouthernAmerica = getView().findViewById(R.id.acbSouthernAmerica);
            layoutSouthernAmerica = getView().findViewById(R.id.WGSRPD_Southern_America);

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

            AppCompatButton acbAntarctic = getView().findViewById(R.id.acbAntarctic);
            layoutAntarctic = getView().findViewById(R.id.WGSRPD_Antarctic);

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
    }

    @Override
    public void onStart() {
        super.onStart();

        layoutEurope.setVisibility(View.GONE);
        layoutAfrica.setVisibility(View.GONE);
        layoutAsiaTemperate.setVisibility(View.GONE);
        layoutAsiaTropical.setVisibility(View.GONE);
        layoutAustralasia.setVisibility(View.GONE);
        layoutPacific.setVisibility(View.GONE);
        layoutNorthernAmerica.setVisibility(View.GONE);
        layoutSouthernAmerica.setVisibility(View.GONE);
        layoutAntarctic.setVisibility(View.GONE);
    }
}
