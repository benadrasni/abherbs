package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import sk.ab.herbs.Constants;
import sk.ab.herbs.commons.BaseFilterFragment;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.FilterPlantsActivity;

public class Habitats extends BaseFilterFragment {

    public Habitats() {
        this.attribute = sk.ab.common.Constants.HABITAT;
        this.title = R.string.habitats;
        this.iconRes = R.drawable.habitats;
        this.layout = R.layout.habitats;
    }

    @Override
    public void onStart() {
        super.onStart();

        FilterPlantsActivity filterPlantsActivity = (FilterPlantsActivity) getActivity();
        final SharedPreferences preferences = filterPlantsActivity
                .getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Boolean wasShowCase = preferences.getBoolean(Constants.SHOWCASE_FILTER_KEY + Constants.VERSION_1_2_7, false);

        if (!wasShowCase) {
            ShowcaseView showcaseView = new ShowcaseView.Builder(filterPlantsActivity)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(filterPlantsActivity.getCountButton()))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_count_button_title)
                    .setContentText(R.string.showcase_count_button_message)
                    .build();

            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
            lps.setMargins(margin, margin, margin, margin);
            showcaseView.setButtonPosition(lps);
            editor.putBoolean(Constants.SHOWCASE_FILTER_KEY + Constants.VERSION_1_2_7, true);
            editor.apply();
        }
    }
}
