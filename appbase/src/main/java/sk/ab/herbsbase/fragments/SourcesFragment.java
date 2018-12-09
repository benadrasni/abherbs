package sk.ab.herbsbase.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.tools.Utils;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class SourcesFragment extends Fragment {

    private static final String SOURCE_WIKIPEDIA = "wikipedia.org";
    private static final String SOURCE_LUONTOPORTTI = "luontoportti.com";
    private static final String SOURCE_WIKIMEDIA_COMMONS = "commons.wikimedia.org";
    private static final String SOURCE_WIKIMEDIA_COMMONS_TITLE = "commons";
    private static final String SOURCE_WIKIMEDIA_SPECIES = "species.wikimedia.org";
    private static final String SOURCE_WIKIMEDIA_SPECIES_TITLE = "species";
    private static final String SOURCE_BOTANY = "botany.cz";
    private static final String SOURCE_FLORANORDICA = "floranordica.org";
    private static final String SOURCE_EFLORA = "efloras.org";
    private static final String SOURCE_BERKELEY = "berkeley.edu";
    private static final String SOURCE_HORTIPEDIA = "hortipedia.com";
    private static final String SOURCE_USDA = "plants.usda.gov";
    private static final String SOURCE_USFS = "forestryimages.org";
    private static final String SOURCE_TELABOTANICA = "tela-botanica.org";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity().getBaseContext(), R.layout.plant_card_sources, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            setSources(getView());
            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.setVisibility(v, R.id.plant_source_grid);
                }
            });
        }
    }

    private void setSources(View convertView) {
        final DisplayPlantBaseActivity activity = (DisplayPlantBaseActivity)getActivity();
        FirebasePlant plant = activity.getPlant();
        PlantTranslation plantTranslation = activity.getPlantTranslation();
        PlantTranslation plantTranslationEn = activity.getPlantTranslationEn();

        List<String> sourceUrls = new ArrayList<>();

        // add link to wikipedia
        String wikilink = null;
        if (plantTranslation != null) {
            wikilink = plantTranslation.getWikipedia();
        }
        if (wikilink == null && plantTranslationEn != null) {
            wikilink = plantTranslationEn.getWikipedia();
        }
        if (wikilink != null) {
            sourceUrls.add(wikilink);
        }

        // add link to commons
        String commonsLink = null;
        String speciesLink = null;
        if (plant != null && plant.getWikilinks() != null) {
            commonsLink = plant.getWikilinks().get(Constants.COMMONS);
            speciesLink = plant.getWikilinks().get(Constants.SPECIES);
        }
        if (commonsLink != null) {
            sourceUrls.add(commonsLink);
        }

        // add link to wikispecies
        if (speciesLink != null) {
            sourceUrls.add(speciesLink);
        }

        // add links to sources in language
        List<String> sources = null;
        if (plantTranslation != null) {
            sources = plantTranslation.getSourceUrls();
            if (sources != null) {
                sourceUrls.addAll(sources);
            }
        }

        // add links to sources in English
        if (sources == null && plantTranslationEn != null) {
            sources = plantTranslationEn.getSourceUrls();
            if (sources != null) {
                sourceUrls.addAll(sources);
            }
        }

        // add links to common sources
        if (plant != null && plant.getSourceUrls() != null) {
            sourceUrls.addAll(plant.getSourceUrls());
        }

        GridLayout grid = convertView.findViewById(R.id.plant_source_grid);
        grid.removeAllViews();

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int width = dm.widthPixels - Utils.convertDpToPx(45, dm);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = width / 2;
        }

        int columns = width / (Utils.convertDpToPx(100, dm));
        grid.setColumnCount(columns);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final String url : sourceUrls) {
            View view = inflater.inflate(R.layout.source, null);
            ImageView image = view.findViewById(R.id.source_icon);
            TextView text = view.findViewById(R.id.source_title);

            if (url.contains(SOURCE_WIKIPEDIA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wikipedia, null));
                text.setText(SOURCE_WIKIPEDIA);
            } else if (url.contains(SOURCE_LUONTOPORTTI)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.luontoportti, null));
                text.setText(SOURCE_LUONTOPORTTI);
            } else if (url.contains(SOURCE_WIKIMEDIA_COMMONS)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.commons, null));
                text.setText(SOURCE_WIKIMEDIA_COMMONS_TITLE);
            } else if (url.contains(SOURCE_WIKIMEDIA_SPECIES)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.species, null));
                text.setText(SOURCE_WIKIMEDIA_SPECIES_TITLE);
            } else if (url.contains(SOURCE_BOTANY)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.botany, null));
                text.setText(SOURCE_BOTANY);
            } else if (url.contains(SOURCE_FLORANORDICA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.floranordica, null));
                text.setText(SOURCE_FLORANORDICA);
            } else if (url.contains(SOURCE_EFLORA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.eflora, null));
                text.setText(SOURCE_EFLORA);
            } else if (url.contains(SOURCE_BERKELEY)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.berkeley, null));
                text.setText(SOURCE_BERKELEY);
            } else if (url.contains(SOURCE_HORTIPEDIA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.hortipedia, null));
                text.setText(SOURCE_HORTIPEDIA);
            } else if (url.contains(SOURCE_USDA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.usda, null));
                text.setText(SOURCE_USDA);
            } else if (url.contains(SOURCE_USFS)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.usfs, null));
                text.setText(SOURCE_USFS);
            } else if (url.contains(SOURCE_TELABOTANICA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.tela_botanica, null));
                text.setText(SOURCE_TELABOTANICA);
            } else {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.internet, null));
            }

            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!activity.isDestroyed()) {
                        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                        List<ResolveInfo> activities = activity.getPackageManager().queryIntentActivities(browserIntent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        if (activities.size() > 0) {
                            startActivity(browserIntent);
                        } else {
                            Toast.makeText(activity.getApplicationContext(), "There is no application installed for web browsing.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            grid.addView(view);
        }
    }
}

