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
        }
    }
}
