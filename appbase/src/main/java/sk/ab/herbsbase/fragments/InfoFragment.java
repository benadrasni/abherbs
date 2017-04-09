package sk.ab.herbsbase.fragments;

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
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity().getBaseContext(), R.layout.plant_card_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final SharedPreferences preferences = ((DisplayPlantActivity)getActivity()).getSharedPreferences();

        final ImageView drawing = (ImageView) getView().findViewById(R.id.plant_background);
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard = !preferences.getBoolean(Constants.SHOWCASE_DISPLAY_KEY + Constants.VERSION_1_2_7, false);

        if (showWizard) {
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(drawing))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_fullscreen_title)
                    .setContentText(R.string.showcase_fullscreen_message)
                    .build();
            editor.putBoolean(Constants.SHOWCASE_DISPLAY_KEY + Constants.VERSION_1_2_7, true);
            editor.apply();
        }

        getTranslation();
    }

    @Override
    public void onStart() {
        super.onStart();
        setInfo(true);
    }

    public void setInfo(boolean withTranslation) {
        final FirebasePlant plant = getPlant();

        final StringBuilder text = new StringBuilder();
        text.append(getResources().getString(R.string.plant_height_from)).append(" <i>").append(plant.getHeightFrom())
                .append("</i>").append(" ").append(getResources().getString(R.string.plant_height_to)).append(" ")
                .append("<i>").append(plant.getHeightTo()).append("</i>").append(" ").append(Constants.HEIGHT_UNIT)
                .append(". <br/>").append(getResources().getString(R.string.plant_flowering_from)).append(" <i>")
                .append(Utils.getMonthName(plant.getFloweringFrom() - 1)).append("</i>").append(" ")
                .append(getResources().getString(R.string.plant_flowering_to)).append(" ").append("<i>")
                .append(Utils.getMonthName(plant.getFloweringTo() - 1)).append("</i>.<br/>");


        String[][] sections = getSections(withTranslation);
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

        final Spanned html = Utils.fromHtml(text.toString());

        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int orientation = getActivity().getResources().getConfiguration().orientation;

        if (plant.getIllustrationUrl() != null) {
            Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS + plant.getIllustrationUrl(),
                    drawing, ((BaseApp) getActivity().getApplication()).getOptions(),
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

    private void getTranslation() {
        PlantTranslation plantTranslation = getPlantTranslation();
        PlantTranslation plantTranslationGT = getPlantTranslationGT();
        PlantTranslation plantTranslationEn = getPlantTranslationEn();

        if (plantTranslationGT == null && plantTranslationEn != null) {
            List<String> textToTranslate = new ArrayList<>();

            if (plantTranslation != null && plantTranslation.getDescription() == null && plantTranslationEn.getDescription() != null) {
                textToTranslate.add(plantTranslationEn.getDescription());
            }

            if (plantTranslation != null && plantTranslation.getFlower() == null && plantTranslationEn.getFlower() != null) {
                textToTranslate.add(plantTranslationEn.getFlower());
            }

            if (plantTranslation != null && plantTranslation.getInflorescence() == null && plantTranslationEn.getInflorescence() != null) {
                textToTranslate.add(plantTranslationEn.getInflorescence());
            }

            if (plantTranslation != null && plantTranslation.getFruit() == null && plantTranslationEn.getFruit() != null) {
                textToTranslate.add(plantTranslationEn.getFruit());
            }

            if (plantTranslation != null && plantTranslation.getLeaf() == null && plantTranslationEn.getLeaf() != null) {
                textToTranslate.add(plantTranslationEn.getLeaf());
            }

            if (plantTranslation != null && plantTranslation.getStem() == null && plantTranslationEn.getStem() != null) {
                textToTranslate.add(plantTranslationEn.getStem());
            }

            if (plantTranslation != null && plantTranslation.getHabitat() == null && plantTranslationEn.getHabitat() != null) {
                textToTranslate.add(plantTranslationEn.getHabitat());
            }

            if (plantTranslation != null && plantTranslation.getToxicity() == null && plantTranslationEn.getToxicity() != null) {
                textToTranslate.add(plantTranslationEn.getToxicity());
            }

            if (plantTranslation != null && plantTranslation.getHerbalism() == null && plantTranslationEn.getHerbalism() != null) {
                textToTranslate.add(plantTranslationEn.getHerbalism());
            }

            if (plantTranslation != null && plantTranslation.getTrivia() == null && plantTranslationEn.getTrivia() != null) {
                textToTranslate.add(plantTranslationEn.getTrivia());
            }

            if (textToTranslate.size() > 0) {
                getTranslation(Constants.LANGUAGE_EN, Locale.getDefault().getLanguage(), textToTranslate);
            }
        }
    }

    private void getTranslation(final String source, final String target, final List<String> textToTranslate) {
        final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity) getActivity();
        final BaseApp app = (BaseApp) displayPlantActivity.getApplication();

        displayPlantActivity.startLoading();
        displayPlantActivity.countButton.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mTranslationGTRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.FIREBASE_SEPARATOR
                + Locale.getDefault().getLanguage() + AndroidConstants.LANGUAGE_GT_SUFFIX + AndroidConstants.FIREBASE_SEPARATOR + getPlant().getName());

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

                setTranslation(translatedTexts);
                mTranslationGTRef.setValue(getPlantTranslationGT());

                setInfo(true);

                displayPlantActivity.stopLoading();
                displayPlantActivity.countButton.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Map<String, Map<String, List<Map<String, String>>>>> call, Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                displayPlantActivity.stopLoading();
                displayPlantActivity.countButton.setVisibility(View.GONE);
            }
        });
    }

    private void setTranslation(List<String> translatedTexts) {
        PlantTranslation plantTranslation = getPlantTranslation();
        PlantTranslation plantTranslationGT = getPlantTranslationGT();
        PlantTranslation plantTranslationEn = getPlantTranslationEn();

        int i = 0;
        if (translatedTexts.size() > i && plantTranslation.getDescription() == null && plantTranslationEn.getDescription() != null) {
            plantTranslationGT.setDescription(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getFlower() == null && plantTranslationEn.getFlower() != null) {
            plantTranslationGT.setFlower(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getInflorescence() == null && plantTranslationEn.getInflorescence() != null) {
            plantTranslationGT.setInflorescence(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getFruit() == null && plantTranslationEn.getFruit() != null) {
            plantTranslationGT.setFruit(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getLeaf() == null && plantTranslationEn.getLeaf() != null) {
            plantTranslationGT.setLeaf(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getStem() == null && plantTranslationEn.getStem() != null) {
            plantTranslationGT.setStem(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getHabitat() == null && plantTranslationEn.getHabitat() != null) {
            plantTranslationGT.setHabitat(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getToxicity() == null && plantTranslationEn.getToxicity() != null) {
            plantTranslationGT.setToxicity(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getHerbalism() == null && plantTranslationEn.getHerbalism() != null) {
            plantTranslationGT.setHerbalism(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && plantTranslation.getTrivia() == null && plantTranslationEn.getTrivia() != null) {
            plantTranslationGT.setTrivia(translatedTexts.get(i));
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
        final StringBuilder text = new StringBuilder();

        text.append(getPlant().getName());
        text.append("<br/><br/>");
        text.append(getPlantInLanguage());
        text.append("<br/><br/>");
        text.append(Locale.getDefault().getDisplayLanguage());
        text.append("<br/><br/>");
        text.append(getPlantPlaceHolder());

        return text.toString();
    }

    private String getPlantInLanguage() {
        final StringBuilder text = new StringBuilder(getPlant().getName());
        for(String[] section : getSections(true) ) {
            text.append("<b>").append(section[0]).append("</b>");
            text.append(": ");
            if (section[1] != null) {
                text.append(section[1]);
            }
            text.append("<br/>");
        }

        return text.toString();
    }

    private String getPlantPlaceHolder() {
        final StringBuilder text = new StringBuilder(getPlant().getName());
        for(String[] section : getSections(true) ) {
            text.append("<b>").append(section[0]).append("</b>");
            text.append(": <br/>");
            text.append("<i>");
            text.append(getResources().getString(R.string.your_text));
            text.append("</i>");
            text.append("<br/>");
        }

        return text.toString();
    }

    private String[][] getSections(boolean withTranslation) {
        PlantTranslation plantTranslation = getPlantTranslation();
        PlantTranslation plantTranslationGT = getPlantTranslationGT();
        PlantTranslation plantTranslationEn = getPlantTranslationEn();

        String[][] sections = {
                {"Description", plantTranslation != null && plantTranslation.getDescription() != null ? plantTranslation.getDescription()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getDescription() != null
                        ? plantTranslationGT.getDescription() : plantTranslationEn != null && plantTranslationEn.getDescription() != null ? plantTranslationEn.getDescription() : ""},
                {getResources().getString(R.string.plant_flowers), plantTranslation != null && plantTranslation.getFlower() != null ? plantTranslation.getFlower()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getFlower() != null
                        ? plantTranslationGT.getFlower() : plantTranslationEn != null && plantTranslationEn.getFlower() != null ? plantTranslationEn.getFlower() : ""},
                {getResources().getString(R.string.plant_inflorescences), plantTranslation != null && plantTranslation.getInflorescence() != null ? plantTranslation.getInflorescence()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getInflorescence() != null
                        ? plantTranslationGT.getInflorescence() : plantTranslationEn != null && plantTranslationEn.getInflorescence() != null ? plantTranslationEn.getInflorescence() : ""},
                {getResources().getString(R.string.plant_fruits), plantTranslation != null && plantTranslation.getFruit() != null ? plantTranslation.getFruit()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getFruit() != null
                        ? plantTranslationGT.getFruit() : plantTranslationEn != null && plantTranslationEn.getFruit() != null ? plantTranslationEn.getFruit() : ""},
                {getResources().getString(R.string.plant_leaves), plantTranslation != null && plantTranslation.getLeaf() != null ? plantTranslation.getLeaf()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getLeaf() != null
                        ? plantTranslationGT.getLeaf() : plantTranslationEn != null && plantTranslationEn.getLeaf() != null ? plantTranslationEn.getLeaf() : ""},
                {getResources().getString(R.string.plant_stem), plantTranslation != null && plantTranslation.getStem() != null ? plantTranslation.getStem()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getStem() != null
                        ? plantTranslationGT.getStem() : plantTranslationEn != null && plantTranslationEn.getStem() != null ? plantTranslationEn.getStem() : ""},
                {getResources().getString(R.string.plant_habitat), plantTranslation != null && plantTranslation.getHabitat() != null ? plantTranslation.getHabitat()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getHabitat() != null
                        ? plantTranslationGT.getHabitat() : plantTranslationEn != null && plantTranslationEn.getHabitat() != null ? plantTranslationEn.getHabitat() : ""},
                {getResources().getString(R.string.plant_toxicity), plantTranslation != null && plantTranslation.getToxicity() != null ? plantTranslation.getToxicity()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getToxicity() != null
                        ? plantTranslationGT.getToxicity() : plantTranslationEn != null && plantTranslationEn.getToxicity() != null ? plantTranslationEn.getToxicity() : ""},
                {getResources().getString(R.string.plant_herbalism), plantTranslation != null && plantTranslation.getHerbalism() != null ? plantTranslation.getHerbalism()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getHerbalism() != null
                        ? plantTranslationGT.getHerbalism() : plantTranslationEn != null && plantTranslationEn.getHerbalism() != null ? plantTranslationEn.getHerbalism() : ""},
                {getResources().getString(R.string.plant_trivia), plantTranslation != null && plantTranslation.getTrivia() != null ? plantTranslation.getTrivia()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getTrivia() != null
                        ? plantTranslationGT.getTrivia() : plantTranslationEn != null && plantTranslationEn.getTrivia() != null ? plantTranslationEn.getTrivia() : ""}
        };

        return sections;
    }

    private FirebasePlant getPlant() {
        return ((DisplayPlantActivity)getActivity()).getPlant();
    }

    private PlantTranslation getPlantTranslation() {
        return ((DisplayPlantActivity)getActivity()).getPlantTranslation();
    }

    private PlantTranslation getPlantTranslationGT() {
        return ((DisplayPlantActivity)getActivity()).getPlantTranslationGT();
    }

    private PlantTranslation getPlantTranslationEn() {
        return ((DisplayPlantActivity)getActivity()).getPlantTranslationEn();
    }
}

