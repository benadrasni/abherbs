package sk.ab.herbsbase.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.common.base.Strings;
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
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.FullScreenImageActivity;
import sk.ab.herbsbase.tools.Keys;
import sk.ab.herbsbase.tools.MyLeadingMarginSpan2;
import sk.ab.herbsbase.tools.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class InfoFragment extends Fragment {

    private DisplayPlantBaseActivity displayPlantBaseActivity;
    private boolean isTranslated;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayPlantBaseActivity = (DisplayPlantBaseActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.plant_card_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final SharedPreferences preferences = ((DisplayPlantBaseActivity)getActivity()).getSharedPreferences();

        final ImageView drawing = (ImageView) getView().findViewById(R.id.plant_background);
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_2_7, false);

        if (showWizard) {
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(drawing))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_fullscreen_title)
                    .setContentText(R.string.showcase_fullscreen_message)
                    .build();
            editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_2_7, true);
            editor.apply();
        }

        isTranslated = true;
        getTranslation();
        showGTSection();
    }

    private void setInfo(boolean withTranslation) {
        final FirebasePlant plant = getPlant();

        final StringBuilder text = new StringBuilder();
        text.append(displayPlantBaseActivity.getResources().getString(R.string.plant_height_from)).append(" <i>").append(plant.getHeightFrom())
                .append("</i>").append(" ").append(displayPlantBaseActivity.getResources().getString(R.string.plant_height_to)).append(" ")
                .append("<i>").append(plant.getHeightTo()).append("</i>").append(" ").append(Constants.HEIGHT_UNIT)
                .append(". <br/>").append(displayPlantBaseActivity.getResources().getString(R.string.plant_flowering_from)).append(" <i>")
                .append(Utils.getMonthName(plant.getFloweringFrom() - 1)).append("</i>").append(" ")
                .append(displayPlantBaseActivity.getResources().getString(R.string.plant_flowering_to)).append(" ").append("<i>")
                .append(Utils.getMonthName(plant.getFloweringTo() - 1)).append("</i>.<br/>");

        final Object[][] sections = getSections(withTranslation);
        text.append(sections[0][1]);

        final TextView description = (TextView)getView().findViewById(R.id.plant_description);
        description.setText(Utils.fromHtml(text.toString()));

        final ImageView drawing = (ImageView)getView().findViewById(R.id.plant_background);
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

        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int orientation = getActivity().getResources().getConfiguration().orientation;

        final LinearLayout layout = (LinearLayout)getView().findViewById(R.id.plant_texts);
        layout.removeAllViews();

        if (plant.getIllustrationUrl() != null) {
            Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS + plant.getIllustrationUrl(),
                    drawing, ((BaseApp) getActivity().getApplication()).getOptions(),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            if (!displayPlantBaseActivity.isDestroyed()) {
                                int width = (dm.widthPixels - Utils.convertDpToPx(25, dm)) / 2;
                                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    width = width / 2;
                                }
                                double ratio = (double) loadedImage.getWidth() / (double) loadedImage.getHeight();
                                int height = (int) (width / ratio);

                                drawing.getLayoutParams().width = width;
                                drawing.getLayoutParams().height = height;

                                int lines = (int) (height/description.getTextSize()) + 1;

                                Paint paint = new Paint();
                                paint.setTextSize(description.getTextSize());

                                for (int i = 1; i < sections.length; i++) {
                                    if (!((String)sections[i][1]).isEmpty()) {
                                        Spanned sectionText = Utils.fromHtml("&nbsp;&nbsp;&nbsp;" + sections[i][1]);

                                        if (getContext() != null) {
                                            SpannableStringBuilder ssb = new SpannableStringBuilder(sectionText);
                                            // add icon
                                            if ((int)sections[i][0] != 0) {
                                                ImageSpan span = new ImageSpan(getContext(), (int)sections[i][0], ImageSpan.ALIGN_BOTTOM);
                                                ssb.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                            }

                                            // add margin for illustration
                                            if (lines > 0) {
                                                MyLeadingMarginSpan2 span = new MyLeadingMarginSpan2(lines, width + 10);
                                                ssb.setSpan(span, 0, sectionText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            }

                                            TextView sectionView = new TextView(getContext());
                                            sectionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                            sectionView.setText(ssb);
                                            layout.addView(sectionView);

                                            List<String> strings = Utils.splitWordsIntoStringsThatFit(ssb.toString(), width, paint);
                                            lines -= strings.size();
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void getTranslation() {
        PlantTranslation plantTranslation = getPlantTranslation();
        PlantTranslation plantTranslationGT = getPlantTranslationGT();
        PlantTranslation plantTranslationEn = getPlantTranslationEn();

        if (plantTranslationGT == null && plantTranslationEn != null) {
            List<String> textToTranslate = new ArrayList<>();

            if ((plantTranslation == null || plantTranslation.getDescription() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getDescription())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getDescription()));
            }

            if ((plantTranslation == null || plantTranslation.getFlower() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getFlower())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getFlower()));
            }

            if ((plantTranslation == null || plantTranslation.getInflorescence() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getInflorescence())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getInflorescence()));
            }

            if ((plantTranslation == null || plantTranslation.getFruit() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getFruit())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getFruit()));
            }

            if ((plantTranslation == null || plantTranslation.getLeaf() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getLeaf())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getLeaf()));
            }

            if ((plantTranslation == null || plantTranslation.getStem() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getStem())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getStem()));
            }

            if ((plantTranslation == null || plantTranslation.getHabitat() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getHabitat())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getHabitat()));
            }

            if ((plantTranslation == null || plantTranslation.getToxicity() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getToxicity())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getToxicity()));
            }

            if ((plantTranslation == null || plantTranslation.getHerbalism() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getHerbalism())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getHerbalism()));
            }

            if ((plantTranslation == null || plantTranslation.getTrivia() == null) && !Strings.isNullOrEmpty(plantTranslationEn.getTrivia())) {
                textToTranslate.add(removeHtmlTags(plantTranslationEn.getTrivia()));
            }

            if (textToTranslate.size() > 0) {
                String baseLanguage = AndroidConstants.LANGUAGE_CS.equals(Locale.getDefault().getLanguage()) ? AndroidConstants.LANGUAGE_SK : AndroidConstants.LANGUAGE_EN;
                getTranslation(baseLanguage, Locale.getDefault().getLanguage(), textToTranslate);
            } else {
                setInfo(false);
            }
        } else {
            setInfo(true);
        }
    }

    private String removeHtmlTags(String text) {
        return text.replace(AndroidConstants.HTML_BOLD, "").replace(AndroidConstants.HTML_BOLD_CLOSE, "");
    }

    private void getTranslation(final String source, final String target, final List<String> textToTranslate) {
        final BaseApp app = (BaseApp) displayPlantBaseActivity.getApplication();

        displayPlantBaseActivity.startLoading();
        displayPlantBaseActivity.countButton.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference mTranslationGTRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.SEPARATOR
                + Locale.getDefault().getLanguage() + AndroidConstants.LANGUAGE_GT_SUFFIX + AndroidConstants.SEPARATOR + getPlant().getName());

        if (BaseApp.isNetworkAvailable(displayPlantBaseActivity.getApplicationContext())) {
            app.getGoogleClient().getApiService().translate(
                    Keys.TRANSLATE_API_KEY,
                    source,
                    target,
                    textToTranslate).enqueue(new Callback<Map<String, Map<String, List<Map<String, String>>>>>() {
                @Override
                public void onResponse(Call<Map<String, Map<String, List<Map<String, String>>>>> call, Response<Map<String, Map<String, List<Map<String, String>>>>> response) {
                    if (!displayPlantBaseActivity.isDestroyed()) {
                        Map<String, Map<String, List<Map<String, String>>>> data = response.body();

                        if (data != null && data.get("data") != null && data.get("data").get("translations") != null) {
                            List<String> translatedTexts = new ArrayList<>();
                            List<Map<String, String>> texts = data.get("data").get("translations");
                            for (Map<String, String> text : texts) {
                                translatedTexts.add(text.get("translatedText"));
                            }

                            setTranslation(translatedTexts);
                            mTranslationGTRef.setValue(getPlantTranslationGT());

                            setInfo(true);
                            showGTSection();
                        }

                        displayPlantBaseActivity.stopLoading();
                        displayPlantBaseActivity.countButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Map<String, List<Map<String, String>>>>> call, Throwable t) {
                    Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                    displayPlantBaseActivity.stopLoading();
                    displayPlantBaseActivity.countButton.setVisibility(View.GONE);
                }
            });
        } else {
            setInfo(false);

            displayPlantBaseActivity.stopLoading();
            displayPlantBaseActivity.countButton.setVisibility(View.GONE);
        }
    }

    private void setTranslation(List<String> translatedTexts) {
        PlantTranslation plantTranslation = getPlantTranslation();
        PlantTranslation plantTranslationEn = getPlantTranslationEn();

        PlantTranslation plantTranslationGT = new PlantTranslation();

        int i = 0;
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getDescription() == null) && plantTranslationEn.getDescription() != null) {
            plantTranslationGT.setDescription(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getFlower() == null) && plantTranslationEn.getFlower() != null) {
            plantTranslationGT.setFlower(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getInflorescence() == null) && plantTranslationEn.getInflorescence() != null) {
            plantTranslationGT.setInflorescence(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getFruit() == null) && plantTranslationEn.getFruit() != null) {
            plantTranslationGT.setFruit(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getLeaf() == null) && plantTranslationEn.getLeaf() != null) {
            plantTranslationGT.setLeaf(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getStem() == null) && plantTranslationEn.getStem() != null) {
            plantTranslationGT.setStem(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getHabitat() == null) && plantTranslationEn.getHabitat() != null) {
            plantTranslationGT.setHabitat(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getToxicity() == null) && plantTranslationEn.getToxicity() != null) {
            plantTranslationGT.setToxicity(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getHerbalism() == null) && plantTranslationEn.getHerbalism() != null) {
            plantTranslationGT.setHerbalism(translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && (plantTranslation == null || plantTranslation.getTrivia() == null) && plantTranslationEn.getTrivia() != null) {
            plantTranslationGT.setTrivia(translatedTexts.get(i));
        }

        setPlantTranslationGT(plantTranslationGT);
    }

    private void improveTranslation() {
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(AndroidConstants.WEB_URL
                + "?plant=" + ((DisplayPlantBaseActivity) getActivity()).getPlant().getName() + "#translate_flower"));
        startActivity(browserIntent);
    }

    private Object[][] getSections(boolean withTranslation) {
        PlantTranslation plantTranslation = getPlantTranslation();
        PlantTranslation plantTranslationGT = getPlantTranslationGT();
        PlantTranslation plantTranslationEn = getPlantTranslationEn();

        Object[][] sections = {
                {0, plantTranslation != null && plantTranslation.getDescription() != null ? plantTranslation.getDescription()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getDescription() != null
                        ? plantTranslationGT.getDescription() : plantTranslationEn != null && plantTranslationEn.getDescription() != null ? plantTranslationEn.getDescription() : ""},
                {R.drawable.ic_flower_black_24dp, plantTranslation != null && plantTranslation.getFlower() != null ? plantTranslation.getFlower()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getFlower() != null
                        ? plantTranslationGT.getFlower() : plantTranslationEn != null && plantTranslationEn.getFlower() != null ? plantTranslationEn.getFlower() : ""},
                {R.drawable.ic_inflorescence_black_24dp, plantTranslation != null && plantTranslation.getInflorescence() != null ? plantTranslation.getInflorescence()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getInflorescence() != null
                        ? plantTranslationGT.getInflorescence() : plantTranslationEn != null && plantTranslationEn.getInflorescence() != null ? plantTranslationEn.getInflorescence() : ""},
                {R.drawable.ic_fruit_black_24dp, plantTranslation != null && plantTranslation.getFruit() != null ? plantTranslation.getFruit()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getFruit() != null
                        ? plantTranslationGT.getFruit() : plantTranslationEn != null && plantTranslationEn.getFruit() != null ? plantTranslationEn.getFruit() : ""},
                {R.drawable.ic_leaf_black_24dp, plantTranslation != null && plantTranslation.getLeaf() != null ? plantTranslation.getLeaf()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getLeaf() != null
                        ? plantTranslationGT.getLeaf() : plantTranslationEn != null && plantTranslationEn.getLeaf() != null ? plantTranslationEn.getLeaf() : ""},
                {R.drawable.ic_stem_black_24dp, plantTranslation != null && plantTranslation.getStem() != null ? plantTranslation.getStem()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getStem() != null
                        ? plantTranslationGT.getStem() : plantTranslationEn != null && plantTranslationEn.getStem() != null ? plantTranslationEn.getStem() : ""},
                {R.drawable.ic_home_black_24dp, plantTranslation != null && plantTranslation.getHabitat() != null ? plantTranslation.getHabitat()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getHabitat() != null
                        ? plantTranslationGT.getHabitat() : plantTranslationEn != null && plantTranslationEn.getHabitat() != null ? plantTranslationEn.getHabitat() : ""},
                {R.drawable.ic_toxicity_black_24dp, plantTranslation != null && plantTranslation.getToxicity() != null ? plantTranslation.getToxicity()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getToxicity() != null
                        ? plantTranslationGT.getToxicity() : plantTranslationEn != null && plantTranslationEn.getToxicity() != null ? plantTranslationEn.getToxicity() : ""},
                {R.drawable.ic_local_pharmacy_black_24dp, plantTranslation != null && plantTranslation.getHerbalism() != null ? plantTranslation.getHerbalism()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getHerbalism() != null
                        ? plantTranslationGT.getHerbalism() : plantTranslationEn != null && plantTranslationEn.getHerbalism() != null ? plantTranslationEn.getHerbalism() : ""},
                {R.drawable.ic_question_mark_black_24dp, plantTranslation != null && plantTranslation.getTrivia() != null ? plantTranslation.getTrivia()
                        : withTranslation && plantTranslationGT != null && plantTranslationGT.getTrivia() != null
                        ? plantTranslationGT.getTrivia() : plantTranslationEn != null && plantTranslationEn.getTrivia() != null ? plantTranslationEn.getTrivia() : ""}
        };

        return sections;
    }

    private void showGTSection() {
        if (getView() != null && getPlantTranslationGT() != null) {
            LinearLayout translationNote = (LinearLayout) getView().findViewById(R.id.translation_note);
            translationNote.setVisibility(View.VISIBLE);

            final Button showOriginal = (Button) getView().findViewById(R.id.show_original);
            showOriginal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTranslated = !isTranslated;
                    setInfo(isTranslated);
                    if (isTranslated) {
                        showOriginal.setText(R.string.show_original);
                    } else {
                        showOriginal.setText(R.string.show_translation);
                    }
                }
            });

            Button improveTranslation = (Button) getView().findViewById(R.id.improve_translation);
            improveTranslation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    improveTranslation();
                }
            });
        }
    }

    private FirebasePlant getPlant() {
        return displayPlantBaseActivity.getPlant();
    }

    private PlantTranslation getPlantTranslation() {
        return displayPlantBaseActivity.getPlantTranslation();
    }

    private PlantTranslation getPlantTranslationGT() {
        return displayPlantBaseActivity.getPlantTranslationGT();
    }

    private void setPlantTranslationGT(PlantTranslation plantTranslation) {
        displayPlantBaseActivity.setPlantTranslationGT(plantTranslation);
    }

    private PlantTranslation getPlantTranslationEn() {
        return displayPlantBaseActivity.getPlantTranslationEn();
    }
}

