package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sk.ab.common.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.herbs.tools.Utils;


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
    private static final String SOURCE_WIKIMEDIA = "wikimedia.org";
    private static final String SOURCE_BOTANY = "botany.cz";
    private static final String SOURCE_FLORANORDICA = "floranordica.org";
    private static final String SOURCE_EFLORA = "efloras.org";
    private static final String SOURCE_BERKELEY = "berkeley.edu";
    private static final String SOURCE_HORTIPEDIA = "hortipedia.com";
    private static final String SOURCE_USDA = "plants.usda.gov";
    private static final String SOURCE_USFS = "forestryimages.org";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        HerbsApp app = (HerbsApp) getActivity().getApplication();
        String language = Locale.getDefault().getLanguage();

        List<String> sourceUrls = new ArrayList<>();
        String wikilink = app.getPlant().getWikilinks().get(language);
        if (wikilink == null) {
            wikilink = app.getPlant().getWikilinks().get(Constants.LANGUAGE_EN);
        }
        if (wikilink != null) {
            sourceUrls.add(wikilink);
        }
        String commonslink = app.getPlant().getWikilinks().get(Constants.COMMONS);
        if (commonslink != null) {
            sourceUrls.add(commonslink);
        }
        List<String> sources = app.getPlant().getSourceUrls().get(language);
        if (sources != null) {
            sourceUrls.addAll(sources);
        }
        sources = app.getPlant().getSourceUrls().get(Constants.LANGUAGE_EN);
        if (sources != null) {
            sourceUrls.addAll(sources);
        }

        GridLayout grid = (GridLayout) convertView.findViewById(R.id.plant_source_grid);
        grid.removeAllViews();

        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int width = dm.widthPixels - Utils.convertDpToPx(45, dm);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = width/2;
        }

        int columns = width/(Utils.convertDpToPx(100, dm));
        grid.setColumnCount(columns);

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final String url : sourceUrls) {
            View view = inflater.inflate(R.layout.source, null);
            ImageView image = (ImageView)view.findViewById(R.id.source_icon);
            TextView text = (TextView)view.findViewById(R.id.source_title);

            if (url.contains(SOURCE_WIKIPEDIA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.wikipedia, null));
                text.setText(SOURCE_WIKIPEDIA);
            } else if (url.contains(SOURCE_LUONTOPORTTI)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.luontoportti, null));
                text.setText(SOURCE_LUONTOPORTTI);
            } else if (url.contains(SOURCE_WIKIMEDIA)) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.commons, null));
                text.setText(SOURCE_WIKIMEDIA);
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
            }

            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                    startActivity(browserIntent);
                }
            });

            grid.addView(view);
        }
    }
}

