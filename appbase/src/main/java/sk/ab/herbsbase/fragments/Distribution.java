package sk.ab.herbsbase.fragments;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;

import sk.ab.common.Constants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.BaseFilterFragment;

public class Distribution extends BaseFilterFragment {

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
            AppCompatButton acbEurope = getView().findViewById(R.id.acbEurope);
            final LinearLayout layoutEurope = getView().findViewById(R.id.WGSRPD_Europe);

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
            final LinearLayout layoutAfrica = getView().findViewById(R.id.WGSRPD_Africa);

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
            final LinearLayout layoutAsiaTemperate = getView().findViewById(R.id.WGSRPD_Asia_Temperate);

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
            final LinearLayout layoutAsiaTropical = getView().findViewById(R.id.WGSRPD_Asia_Tropical);

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
            final LinearLayout layoutAustralasia = getView().findViewById(R.id.WGSRPD_Australasia);

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
            final LinearLayout layoutPacific = getView().findViewById(R.id.WGSRPD_Pacific);

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
            final LinearLayout layoutNorthernAmerica = getView().findViewById(R.id.WGSRPD_Northern_America);

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
            final LinearLayout layoutSouthernAmerica = getView().findViewById(R.id.WGSRPD_Southern_America);

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
            final LinearLayout layoutAntarctic = getView().findViewById(R.id.WGSRPD_Antarctic);

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
}
