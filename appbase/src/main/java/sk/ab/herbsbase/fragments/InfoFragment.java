package sk.ab.herbsbase.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sk.ab.common.Constants;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.Translation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.DisplayPlantActivity;
import sk.ab.herbsbase.commons.FullScreenImageActivity;
import sk.ab.herbsbase.tools.Keys;
import sk.ab.herbsbase.tools.Utils;
import uk.co.deanwild.flowtextview.FlowTextView;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class InfoFragment extends Fragment {

    private ImageView translateView;

    private Plant plant;
    private boolean isTranslated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity().getBaseContext(), R.layout.plant_card_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final SharedPreferences preferences = getActivity().getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);

        plant = ((DisplayPlantActivity)getActivity()).getPlant();
        isTranslated = false;

        translateView = (ImageView) getView().findViewById(R.id.plant_translate);
        translateView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getTranslation(Locale.getDefault().getLanguage());
            }
        });

        final ImageView proposeView = (ImageView) getView().findViewById(R.id.plant_mail);
        proposeView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                proposeTranslation();
            }
        });

        if (preferences.getBoolean(Constants.PROPOSE_TRANSLATION_KEY, false)) {
            proposeView.setVisibility(View.GONE);
        } else {
            proposeView.setVisibility(View.VISIBLE);
        }

        final ImageView drawing = (ImageView) getView().findViewById(R.id.plant_background);
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard = !preferences.getBoolean(Constants.SHOWCASE_DISPLAY_KEY + Constants.VERSION_1_2_7, false);

        if (showWizard) {
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
            editor.putBoolean(Constants.SHOWCASE_DISPLAY_KEY + Constants.VERSION_1_2_7, true);
            editor.apply();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            if (plant.isTranslated(Locale.getDefault().getLanguage())) {
                translateView.setVisibility(View.GONE);
            }

            setInfo(false);
        }
    }

    public void setInfo(boolean withTranslation) {
        final Plant plant = ((DisplayPlantActivity)getActivity()).getPlant();

        final StringBuilder text = new StringBuilder();
        text.append(getResources().getString(R.string.plant_height_from)).append(" <i>").append(plant.getHeightFrom())
                .append("</i>").append(" ").append(getResources().getString(R.string.plant_height_to)).append(" ")
                .append("<i>").append(plant.getHeightTo()).append("</i>").append(" ").append(Constants.HEIGHT_UNIT)
                .append(". <br/>").append(getResources().getString(R.string.plant_flowering_from)).append(" <i>")
                .append(Utils.getMonthName(plant.getFloweringFrom() - 1)).append("</i>").append(" ")
                .append(getResources().getString(R.string.plant_flowering_to)).append(" ").append("<i>")
                .append(Utils.getMonthName(plant.getFloweringTo() - 1)).append("</i>.<br/>");


        String[][] sections = getSections(Locale.getDefault().getLanguage(), withTranslation);
        text.append(sections[0][1]).append("<br/>");

        int[][] spanIndex = new int[2][sections.length];

        for (int i = 1; i < sections.length; i++) {
            if (sections[i][1] != null) {
                spanIndex[0][i] = text.length();
                spanIndex[1][i] = text.length() + sections[i][0].length();
                text.append("<i>").append(sections[i][0]).append("</i>");
                text.append(": ");
                text.append(sections[i][1]);
                text.append("<br/>");
            }
        }

        final ImageView drawing = (ImageView) getView().findViewById(R.id.plant_background);
        drawing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);

                Bundle extras = new Bundle();
                extras.putSerializable("image_url", plant.getIllustrationUrl());
                intent.putExtras(extras);
                startActivity(intent);

            }
        });

        final FlowTextView flowTextView = (FlowTextView) getView().findViewById(R.id.plantInfoLayout);
        flowTextView.setTextColor(R.color.CardText);
        flowTextView.setTextSize(getResources().getDisplayMetrics().scaledDensity * 14.0f);

        final Spanned html = Html.fromHtml(text.toString());

        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int orientation = getActivity().getResources().getConfiguration().orientation;

        if (plant.getIllustrationUrl() != null) {
            ImageLoader.getInstance().displayImage(plant.getIllustrationUrl(), drawing,
                    ((BaseApp) getActivity().getApplication()).getOptions(),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            int width = (dm.widthPixels - Utils.convertDpToPx(25, dm))/2;
                            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                width = width/2;
                            }
                            double ratio = (double)loadedImage.getWidth()/(double)loadedImage.getHeight();

                            drawing.getLayoutParams().width = width;
                            drawing.getLayoutParams().height = (int)(drawing.getLayoutParams().width/ratio);

                            flowTextView.setText(html);
                        }
                    });

        } else {
            flowTextView.setText(html);
        }

    }

    private void getTranslation(String language) {
        if (isTranslated) {
            setInfo(false);
        } else {
            List<String> textToTranslate = new ArrayList<>();

            if (plant.getDescription().get(language) == null && plant.getDescription().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getDescription().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getDescription().get(Constants.LANGUAGE_EN));
            }

            if (plant.getFlower().get(language) == null && plant.getFlower().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getFlower().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getFlower().get(Constants.LANGUAGE_EN));
            }

            if (plant.getInflorescence().get(language) == null && plant.getInflorescence().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getInflorescence().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getInflorescence().get(Constants.LANGUAGE_EN));
            }

            if (plant.getFruit().get(language) == null && plant.getFruit().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getFruit().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getFruit().get(Constants.LANGUAGE_EN));
            }

            if (plant.getLeaf().get(language) == null && plant.getLeaf().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getLeaf().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getLeaf().get(Constants.LANGUAGE_EN));
            }

            if (plant.getStem().get(language) == null && plant.getStem().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getStem().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getStem().get(Constants.LANGUAGE_EN));
            }

            if (plant.getHabitat().get(language) == null && plant.getHabitat().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getHabitat().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getHabitat().get(Constants.LANGUAGE_EN));
            }

            if (plant.getTrivia().get(language) == null && plant.getTrivia().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getTrivia().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getTrivia().get(Constants.LANGUAGE_EN));
            }

            if (plant.getToxicity().get(language) == null && plant.getToxicity().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getToxicity().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getToxicity().get(Constants.LANGUAGE_EN));
            }

            if (plant.getHerbalism().get(language) == null && plant.getHerbalism().get(language + Constants.LANGUAGE_GT_SUFFIX) == null
                    && plant.getHerbalism().get(Constants.LANGUAGE_EN) != null) {
                textToTranslate.add(plant.getHerbalism().get(Constants.LANGUAGE_EN));
            }

            if (textToTranslate.size() > 0) {
                getTranslation(Constants.LANGUAGE_EN, language, textToTranslate);
            } else {
                setInfo(true);
            }
        }
        isTranslated = !isTranslated;
    }

    private void getTranslation(final String source, final String target, final List<String> textToTranslate) {
        final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity) getActivity();
        final BaseApp app = (BaseApp) displayPlantActivity.getApplication();

        displayPlantActivity.startLoading();
        displayPlantActivity.countButton.setVisibility(View.VISIBLE);
        app.getHerbCloudClient().getApiService().getTranslation(displayPlantActivity.getPlant().getPlantId() + "_" +  target)
                .enqueue(new Callback<Translation>() {
                    @Override
                    public void onResponse(Call<Translation> call, Response<Translation> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                setTranslation(target, response.body().getTexts());
                                setInfo(true);
                                displayPlantActivity.stopLoading();
                                displayPlantActivity.countButton.setVisibility(View.GONE);
                            } else {
                                app.getGoogleClient().getApiService().translate(
                                        Keys.TRANSLATE_API_KEY,
                                        source,
                                        target,
                                        textToTranslate).enqueue(new Callback<Map<String, Map<String, List<Map<String, String>>>>>() {
                                    @Override
                                    public void onResponse(Call<Map<String, Map<String, List<Map<String, String>>>>> call, Response<Map<String, Map<String, List<Map<String, String>>>>> response) {
                                        Map<String, Map<String, List<Map<String, String>>>> data = response.body();

                                        List<String> translatedTexts = new ArrayList<>();
                                        List<Map<String, String>> texts = data.get("data").get("translations");
                                        for (Map<String, String> text : texts) {
                                            translatedTexts.add(text.get("translatedText"));
                                        }

                                        setTranslation(target, translatedTexts);
                                        setInfo(true);

                                        Translation translation = new Translation(plant.getPlantId(),
                                                target, translatedTexts);

                                        app.getHerbCloudClient().getApiService().saveTranslation(translation)
                                                .enqueue(new Callback<Translation>() {
                                                    @Override
                                                    public void onResponse(Call<Translation> call, Response<Translation> response) {
                                                        if (response != null) {
                                                            Log.i(this.getClass().getName(), "Translation " +
                                                                    response.body().getTranslationId() + " was saved to the datastore");
                                                        }
                                                        displayPlantActivity.stopLoading();
                                                        displayPlantActivity.countButton.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Translation> call, Throwable t) {
                                                        Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                                                        displayPlantActivity.stopLoading();
                                                        displayPlantActivity.countButton.setVisibility(View.GONE);
                                                    }
                                                });

                                    }

                                    @Override
                                    public void onFailure(Call<Map<String, Map<String, List<Map<String, String>>>>> call, Throwable t) {
                                        Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                                        displayPlantActivity.stopLoading();
                                        displayPlantActivity.countButton.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Translation> call, Throwable t) {
                        Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                    }
                });

    }

    private void setTranslation(String language, List<String> translatedTexts) {
        int i = 0;
        if (translatedTexts.size() > i && plant.getDescription().get(language) == null) {
            plant.getDescription().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getFlower().get(language) == null) {
            plant.getFlower().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getInflorescence().get(language) == null) {
            plant.getInflorescence().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getFruit().get(language) == null) {
            plant.getFruit().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getLeaf().get(language) == null) {
            plant.getLeaf().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getStem().get(language) == null) {
            plant.getStem().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getHabitat().get(language) == null) {
            plant.getHabitat().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getTrivia().get(language) == null) {
            plant.getTrivia().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getToxicity().get(language) == null) {
            plant.getToxicity().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plant.getHerbalism().get(language) == null) {
            plant.getHerbalism().put(language + Constants.LANGUAGE_GT_SUFFIX, translatedTexts.get(i));
        }
    }

    private void proposeTranslation() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", AndroidConstants.EMAIL, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject());
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getEmailBody()));
        startActivity(Intent.createChooser(emailIntent, getEmailSubject()));
    }

    private String getEmailSubject() {
        return getString(R.string.email_subject_prefix) + " " + ((DisplayPlantActivity) getActivity()).getPlant().getName();
    }

    private String getEmailBody() {
        Plant plant = ((DisplayPlantActivity) getActivity()).getPlant();
        String language = Locale.getDefault().getLanguage();

        final StringBuilder text = new StringBuilder();

        text.append(plant.getName());
        text.append("<br/><br/>");
        text.append(getPlantInLanguage(language));
        text.append("<br/><br/>");
        text.append(Locale.getDefault().getDisplayLanguage());
        text.append("<br/><br/>");
        text.append(getPlantPlaceHolder(language));

        return text.toString();
    }

    private String getPlantInLanguage(String language) {
        final StringBuilder text = new StringBuilder(plant.getLabel(language));
        for(String[] section : getSections(language, true) ) {
            text.append("<b>").append(section[0]).append("</b>");
            text.append(": ");
            if (section[1] != null) {
                text.append(section[1]);
            }
            text.append("<br/>");
        }

        return text.toString();
    }

    private String getPlantPlaceHolder(String language) {
        final StringBuilder text = new StringBuilder(plant.getLabel(language));
        for(String[] section : getSections(language, true) ) {
            text.append("<b>").append(section[0]).append("</b>");
            text.append(": <br/>");
            text.append("<i>");
            text.append(getResources().getString(R.string.your_text));
            text.append("</i>");
            text.append("<br/>");
        }

        return text.toString();
    }

    private String[][] getSections(String language, boolean withTranslation) {
        String[][] sections = {
                {"", plant.getDescription().get(language) != null ? plant.getDescription().get(language)
                        : withTranslation && plant.getDescription().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getDescription().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getDescription().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_flowers), plant.getFlower().get(language) != null ? plant.getFlower().get(language)
                        : withTranslation && plant.getFlower().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getFlower().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getFlower().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_inflorescences), plant.getInflorescence().get(language) != null ? plant.getInflorescence().get(language)
                        : withTranslation && plant.getInflorescence().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getInflorescence().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getInflorescence().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_fruits), plant.getFruit().get(language) != null ? plant.getFruit().get(language)
                        : withTranslation && plant.getFruit().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getFruit().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getFruit().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_leaves), plant.getLeaf().get(language) != null ? plant.getLeaf().get(language)
                        : withTranslation && plant.getLeaf().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getLeaf().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getLeaf().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_stem), plant.getStem().get(language) != null ? plant.getStem().get(language)
                        : withTranslation && plant.getStem().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getStem().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getStem().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_habitat), plant.getHabitat().get(language) != null ? plant.getHabitat().get(language)
                        : withTranslation && plant.getHabitat().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getHabitat().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getHabitat().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_toxicity), plant.getToxicity().get(language) != null ? plant.getToxicity().get(language)
                        : withTranslation && plant.getToxicity().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getToxicity().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getToxicity().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_herbalism), plant.getHerbalism().get(language) != null ? plant.getHerbalism().get(language)
                        : withTranslation && plant.getHerbalism().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getHerbalism().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getHerbalism().get(Constants.LANGUAGE_EN)},
                {getResources().getString(R.string.plant_trivia), plant.getTrivia().get(language) != null ? plant.getTrivia().get(language)
                        : withTranslation && plant.getTrivia().get(language + Constants.LANGUAGE_GT_SUFFIX) != null
                        ? plant.getTrivia().get(language + Constants.LANGUAGE_GT_SUFFIX) : plant.getTrivia().get(Constants.LANGUAGE_EN)}
        };

        return sections;
    }

}

