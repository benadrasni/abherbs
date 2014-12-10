package sk.ab.herbs.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.tools.DrawableManager;

public class PlantCardsFragment extends ListFragment {
    private static final String THUMBNAIL_DIR = "/.thumbnails";

    private static final int CARD_TAXONOMY = 0;
    private static final int CARD_INFO = 1;
    private static final int CARD_GALLERY = 2;

    private int thumbnail_position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thumbnail_position = 0;
        return inflater.inflate(R.layout.list, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PropertyAdapter adapter = new PropertyAdapter(getActivity());
        adapter.add(CARD_TAXONOMY);
        adapter.add(CARD_INFO);
        adapter.add(CARD_GALLERY);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        switch (position) {
            case CARD_TAXONOMY:
                setVisibility(v, R.id.plant_taxonomy);
                v.invalidate();
                break;
            case CARD_INFO:
                setVisibility(v, R.id.plant_info);
                v.invalidate();
                break;
        }
    }

    public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
        private String urls[];
        private View cardGallery;

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;

            public ViewHolder(View v) {
                super(v);
                mImageView = (ImageView) v.findViewById(R.id.image);
            }
        }

        public ThumbnailAdapter(String[] urls) {
            this.urls = urls;
        }

        @Override
        public ThumbnailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail, parent, false);
            cardGallery = (View) parent.getParent();
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final String url = urls[position];
            DrawableManager.getDrawableManager().fetchDrawableOnThread(getThumbnailUrl(url),
                    holder.mImageView);

            final ImageView imageView = (ImageView) cardGallery.findViewById(R.id.plant_photo);

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumbnail_position = position;
                    DrawableManager.getDrawableManager().fetchDrawableOnThread(url, imageView);
                }
            });
        }

        @Override
        public int getItemCount() {
            return urls.length;
        }

        private String getThumbnailUrl(String url) {
            String result = url;
            if (url.lastIndexOf('/') > -1) {
                result = url.substring(0, url.lastIndexOf('/')) + THUMBNAIL_DIR + url.substring(url.lastIndexOf('/'));
            }
            return result;
        }
    }

    public class PropertyAdapter extends ArrayAdapter<Integer> {

        public PropertyAdapter(Context context) {
            super(context, 0);
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {
            Plant plant = ((DisplayPlantActivity) getActivity()).getPlant();

            if (convertView == null) {
                switch (position) {
                    case CARD_TAXONOMY:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_card_taxonomy, null);
                        View taxonomy = convertView.findViewById(R.id.plant_taxonomy);
                        taxonomy.setVisibility(View.GONE);
                        break;
                    case CARD_INFO:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_card_info, null);
                        View info = convertView.findViewById(R.id.plant_info);
                        info.setVisibility(View.GONE);
                        break;
                    case CARD_GALLERY:
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_card_gallery, parent, false);
                        RecyclerView thumbnails = (RecyclerView) convertView.findViewById(R.id.plant_thumbnails);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        layoutManager.scrollToPosition(thumbnail_position);
                        thumbnails.setLayoutManager(layoutManager);

                        String[] urls = new String[plant.getPhoto_urls().size()];
                        plant.getPhoto_urls().toArray(urls);

                        ThumbnailAdapter adapter = new ThumbnailAdapter(urls);
                        thumbnails.setAdapter(adapter);
                        break;
                }
            }

            switch (position) {
                case CARD_TAXONOMY:
                    TextView species = (TextView) convertView.findViewById(R.id.plant_species);
                    species.setText(plant.getSpecies());
                    TextView species_latin = (TextView) convertView.findViewById(R.id.plant_species_latin);
                    species_latin.setText(plant.getSpecies_latin());
                    TextView namesView = (TextView) convertView.findViewById(R.id.plant_alt_names);
                    StringBuilder names = new StringBuilder();
                    for(String name: plant.getNames()) {
                        names.append(", ");
                        names.append(name);
                    }
                    if (names.length() > 0) {
                        namesView.setText(names.toString().substring(2));
                    } else {
                        namesView.setVisibility(View.GONE);
                    }

                    TextView family = (TextView) convertView.findViewById(R.id.plant_family);
                    family.setText(plant.getFamily());
                    TextView family_latin = (TextView) convertView.findViewById(R.id.plant_family_latin);
                    family_latin.setText(plant.getFamily_latin());

                    TextView order = (TextView) convertView.findViewById(R.id.plant_order);
                    order.setText(plant.getOrder());
                    TextView order_latin = (TextView) convertView.findViewById(R.id.plant_order_latin);
                    order_latin.setText(plant.getOrder_latin());

                    TextView cls = (TextView) convertView.findViewById(R.id.plant_cls);
                    cls.setText(plant.getCls());
                    TextView cls_latin = (TextView) convertView.findViewById(R.id.plant_cls_latin);
                    cls_latin.setText(plant.getCls_latin());

                    TextView phylum = (TextView) convertView.findViewById(R.id.plant_phylum);
                    phylum.setText(plant.getPhylum());
                    TextView phylum_latin = (TextView) convertView.findViewById(R.id.plant_phylum_latin);
                    phylum_latin.setText(plant.getPhylum_latin());

                    TextView branch = (TextView) convertView.findViewById(R.id.plant_branch);
                    branch.setText(plant.getBranch());
                    TextView branch_latin = (TextView) convertView.findViewById(R.id.plant_branch_latin);
                    branch_latin.setText(plant.getBranch_latin());

                    TextView line = (TextView) convertView.findViewById(R.id.plant_line);
                    line.setText(plant.getLine());
                    TextView line_latin = (TextView) convertView.findViewById(R.id.plant_line_latin);
                    line_latin.setText(plant.getLine_latin());

                    TextView subKingdom = (TextView) convertView.findViewById(R.id.plant_subkingdom);
                    subKingdom.setText(plant.getSubkingdom());
                    TextView subKingdom_latin = (TextView) convertView.findViewById(R.id.plant_subkingdom_latin);
                    subKingdom_latin.setText(plant.getSubkingdom_latin());

                    TextView kingdom = (TextView) convertView.findViewById(R.id.plant_kingdom);
                    kingdom.setText(plant.getKingdom());
                    TextView kingdom_latin = (TextView) convertView.findViewById(R.id.plant_kingdom_latin);
                    kingdom_latin.setText(plant.getKingdom_latin());

                    TextView domain = (TextView) convertView.findViewById(R.id.plant_domain);
                    domain.setText(plant.getDomain());
                    TextView domain_latin = (TextView) convertView.findViewById(R.id.plant_domain_latin);
                    domain_latin.setText(plant.getDomain_latin());

                    break;
                case CARD_INFO:
                    ImageView drawing = (ImageView) convertView.findViewById(R.id.plant_background);
                    drawing.setImageResource(android.R.color.transparent);
                    if (plant.getBack_url() != null) {
                        DrawableManager.getDrawableManager().fetchDrawableOnThread(plant.getBack_url(), drawing);
                    }

                    TextView flower = (TextView) convertView.findViewById(R.id.plant_flower);
                    flower.setText(Html.fromHtml(plant.getDescWithHighlight(getResources().getString(R.string.plant_flowers),
                            plant.getFlower())));

                    TextView inflorescence = (TextView) convertView.findViewById(R.id.plant_inflorescence);
                    inflorescence.setText(Html.fromHtml(plant.getDescWithHighlight(getResources().getString(R.string.plant_inflorescences),
                            plant.getInflorescence())));

                    TextView fruit = (TextView) convertView.findViewById(R.id.plant_fruit);
                    fruit.setText(Html.fromHtml(plant.getDescWithHighlight(getResources().getString(R.string.plant_fruits),
                            plant.getFruit())));

                    TextView leaf = (TextView) convertView.findViewById(R.id.plant_leaf);
                    leaf.setText(Html.fromHtml(plant.getDescWithHighlight(getResources().getString(R.string.plant_leaves),
                            plant.getLeaf())));

                    TextView stem = (TextView) convertView.findViewById(R.id.plant_stem);
                    stem.setText(Html.fromHtml(plant.getDescWithHighlight(getResources().getString(R.string.plant_stem),
                            plant.getStem())));

                    TextView habitat = (TextView) convertView.findViewById(R.id.plant_habitat);
                    habitat.setText(Html.fromHtml(plant.getDescWithHighlight(getResources().getString(R.string.plant_habitat),
                            plant.getHabitat())));

                    break;
                case CARD_GALLERY:
                    ImageView image = (ImageView) convertView.findViewById(R.id.plant_photo);
                    if (plant.getPhoto_urls().size() > thumbnail_position && plant.getPhoto_urls().get(thumbnail_position) != null) {
                        DrawableManager.getDrawableManager().fetchDrawableOnThread(plant.getPhoto_urls().get(thumbnail_position), image);
                    } else if (plant.getPhoto_urls().size() > 0 && plant.getPhoto_urls().get(0) != null) {
                        DrawableManager.getDrawableManager().fetchDrawableOnThread(plant.getPhoto_urls().get(0), image);
                    }

                    break;
            }

            return convertView;
        }
    }

    private void setVisibility(View v, int resId) {
        View view = v.findViewById(resId);
        if (view.isShown()) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
