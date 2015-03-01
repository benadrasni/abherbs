package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.tools.Utils;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_card_sources, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            setSources(((DisplayPlantActivity) getActivity()).getPlant(), getView());
            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.setVisibility(v, R.id.plant_source_grid);
                    final ScrollView scrollview = ((ScrollView) getActivity().findViewById(R.id.scrollview));
                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            });
        }
    }

    private void setSources(Plant plant, View convertView) {
        String[] source_urls = new String[plant.getSource_urls().size()];
        plant.getSource_urls().toArray(source_urls);

        GridLayout grid = (GridLayout) convertView.findViewById(R.id.plant_source_grid);

        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (final String url : source_urls) {
            View view = inflater.inflate(R.layout.source, null);
            ImageView image = (ImageView)view.findViewById(R.id.source_icon);
            TextView text = (TextView)view.findViewById(R.id.source_title);

            if (url.contains(SOURCE_WIKIPEDIA)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.wikipedia));
                text.setText(SOURCE_WIKIPEDIA);
            } else if (url.contains(SOURCE_LUONTOPORTTI)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.luontoportti));
                text.setText(SOURCE_LUONTOPORTTI);
            } else if (url.contains(SOURCE_WIKIMEDIA)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.commons));
                text.setText(SOURCE_WIKIMEDIA);
            } else if (url.contains(SOURCE_BOTANY)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.botany));
                text.setText(SOURCE_BOTANY);
            } else if (url.contains(SOURCE_FLORANORDICA)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.floranordica));
                text.setText(SOURCE_FLORANORDICA);
            } else if (url.contains(SOURCE_EFLORA)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.eflora));
                text.setText(SOURCE_EFLORA);
            } else if (url.contains(SOURCE_BERKELEY)) {
                image.setImageDrawable(getResources().getDrawable(R.drawable.berkeley));
                text.setText(SOURCE_BERKELEY);
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

