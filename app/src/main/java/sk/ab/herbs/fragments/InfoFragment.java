package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Locale;

import sk.ab.commons.FullScreenImageActivity;
import sk.ab.herbs.BuildConfig;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.tools.Margin;
import sk.ab.tools.Utils;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class InfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity().getBaseContext(), R.layout.plant_card_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity)getActivity();

        final SharedPreferences preferences = displayPlantActivity.getSharedPreferences("sk.ab.herbs",
                Context.MODE_PRIVATE);

        final ImageView translateView = (ImageView) getView().findViewById(R.id.plant_translate);
        final ImageView proposeView = (ImageView) getView().findViewById(R.id.plant_mail);
        final ImageView drawing = (ImageView) getView().findViewById(R.id.plant_background);

        translateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                displayPlantActivity.getTranslation();
            }
        });

        proposeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                displayPlantActivity.proposeTranslation();
            }
        });

        if (displayPlantActivity.isTranslated()) {
            translateView.setVisibility(View.GONE);
        }

        if (preferences.getBoolean(Constants.PROPOSE_TRANSLATION_KEY, false)) {
            proposeView.setVisibility(View.GONE);
        } else {
            proposeView.setVisibility(View.VISIBLE);
        }

        SharedPreferences.Editor editor = preferences.edit();
        Boolean wasShowCase = preferences.getBoolean(Constants.SHOWCASE_DISPLAY_KEY + BuildConfig.VERSION_CODE, false);

        if (!wasShowCase) {
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(proposeView))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_propose_title)
                    .setContentText(R.string.showcase_propose_message)
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            new ShowcaseView.Builder(getActivity())
                                    .withMaterialShowcase()
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .setTarget(new ViewTarget(drawing))
                                    .hideOnTouchOutside()
                                    .setContentTitle(R.string.showcase_fullscreen_title)
                                    .setContentText(R.string.showcase_fullscreen_message)
                                    .build();
                        }

                    })
                    .build();
            editor.putBoolean(Constants.SHOWCASE_DISPLAY_KEY + BuildConfig.VERSION_CODE, true);
            editor.apply();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity)getActivity();

            SharedPreferences preferences = displayPlantActivity.getSharedPreferences("sk.ab.herbs",
                    Context.MODE_PRIVATE);
            String sLanguage = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

            setInfo(displayPlantActivity.getPlant(), Constants.getLanguage(sLanguage));
        }
    }

    public void setInfo(final Plant plant, int language) {
        TextView firstRow = (TextView) getView().findViewById(R.id.first_row);

        firstRow.setText(Html.fromHtml(getResources().getString(R.string.plant_height_from) +
                " <b>" + plant.getHeight_from() + "</b>" + " " + getResources().getString(R.string.plant_height_to) +
                " " + "<b>" + plant.getHeight_to() + "</b>" + " " + Constants.HEIGHT_UNIT + ". <br/>" +
                getResources().getString(R.string.plant_flowering_from) +
                " <b>" + Utils.getMonthName(plant.getFlowering_from() - 1) + "</b>" + " " +
                getResources().getString(R.string.plant_flowering_to) + " " +
                "<b>" + Utils.getMonthName(plant.getFlowering_to() - 1) + "</b>."));

        if (plant.getDescription() != null) {
            TextView upImage = (TextView) getView().findViewById(R.id.up_image);
            upImage.setText(Html.fromHtml(plant.getDescription().getText(language)));
        }

        final StringBuilder text = new StringBuilder();
        String[][] sections = { {getResources().getString(R.string.plant_flowers), plant.getFlower().getText(language)},
                {getResources().getString(R.string.plant_inflorescences), plant.getInflorescence().getText(language)},
                {getResources().getString(R.string.plant_fruits), plant.getFruit().getText(language)},
                {getResources().getString(R.string.plant_leaves), plant.getLeaf().getText(language)},
                {getResources().getString(R.string.plant_stem), plant.getStem().getText(language)},
                {getResources().getString(R.string.plant_habitat), plant.getHabitat().getText(language)},
                {getResources().getString(R.string.plant_toxicity), plant.getToxicity().getText(language)},
                {getResources().getString(R.string.plant_herbalism), plant.getHerbalism().getText(language)}
        };
        final int[][] spanIndex = new int[2][sections.length];

        int nonEmptySections = 0;
        for(int i = 0; i < sections.length; i++ ) {
            if (sections[i][1].length() > 0) {
                spanIndex[0][i] = text.length();
                spanIndex[1][i] = text.length() + sections[i][0].length();
                text.append(sections[i][0]);
                text.append(": ");
                text.append(sections[i][1]);
                text.append(" ");
//                text.append("\n");
                nonEmptySections++;
            }
        }

        final TextView nextToImage = (TextView) getView().findViewById(R.id.next_to_image);
        final ImageView drawing = (ImageView) getView().findViewById(R.id.plant_background);
        drawing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);

                Bundle extras = new Bundle();
                extras.putSerializable("image_url", plant.getBack_url());
                intent.putExtras(extras);
                startActivity(intent);

            }
        });

        final SpannableString ss = new SpannableString(text.toString());
        for(int i = 0; i < nonEmptySections; i++ ) {
            ss.setSpan(new StyleSpan(Typeface.BOLD), spanIndex[0][i], spanIndex[1][i],
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int orientation = getActivity().getResources().getConfiguration().orientation;

        if (plant.getBack_url() != null) {
            ImageLoader.getInstance().displayImage(plant.getBack_url(), drawing,
                    ((HerbsApp)getActivity().getApplication()).getOptions(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    int width = (dm.widthPixels - Utils.convertDpToPx(25, dm))/2;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        width = width/2;
                    }
                    double ratio = (double)loadedImage.getWidth()/(double)loadedImage.getHeight();

                    drawing.getLayoutParams().width = width;
                    drawing.getLayoutParams().height = (int)(drawing.getLayoutParams().width/ratio);

                    int leftMargin = drawing.getLayoutParams().width;
                    int height = drawing.getLayoutParams().height;
                    ss.setSpan(new Margin(height / (int) (nextToImage.getLineHeight() * nextToImage
                            .getLineSpacingMultiplier() + nextToImage.getLineSpacingExtra()),
                            leftMargin), 0, ss.length(), Spanned.SPAN_PARAGRAPH);
                    nextToImage.setText(ss);
                }
            });

        } else {
            nextToImage.setText(ss);
        }
    }
}

