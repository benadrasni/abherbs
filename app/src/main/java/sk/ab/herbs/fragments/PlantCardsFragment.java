package sk.ab.herbs.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.tools.Margin;
import sk.ab.tools.Utils;

public class PlantCardsFragment extends ListFragment {
    private static final String THUMBNAIL_DIR = "/.thumbnails";

    private static final int CARD_TAXONOMY = 0;
    private static final int CARD_INFO = 1;
    private static final int CARD_GALLERY = 2;
    private static final int CARD_SOURCES = 3;

    private static final int INFO_SECTIONS = 6;

    private int thumbnail_position;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thumbnail_position = 0;
        return inflater.inflate(R.layout.list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PropertyAdapter adapter = new PropertyAdapter(getActivity());
        adapter.add(CARD_TAXONOMY);
        adapter.add(CARD_INFO);
        adapter.add(CARD_GALLERY);
        adapter.add(CARD_SOURCES);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        switch (position) {
            case CARD_TAXONOMY:
                setVisibility(v, R.id.plant_taxonomy);
                v.invalidate();
                break;
            case CARD_SOURCES:
                setVisibility(v, R.id.plant_sources);
                v.invalidate();
                break;
        }
        lv.setSelection(position);
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
            ImageLoader.getInstance().displayImage(getThumbnailUrl(url), holder.mImageView,
                    ((HerbsApp)getActivity().getApplication()).getOptions());

            final ImageView imageView = (ImageView) cardGallery.findViewById(R.id.plant_photo);

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumbnail_position = position;
                    ImageLoader.getInstance().displayImage(url, imageView,
                            ((HerbsApp)getActivity().getApplication()).getOptions());
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

            switch (position) {
                case CARD_TAXONOMY:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_card_taxonomy, parent, false);

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
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_card_info, parent, false);

                    TextView firstRow = (TextView) convertView.findViewById(R.id.first_row);
                    StringBuilder firstRowText = new StringBuilder();
                    firstRowText.append(plant.getDescWithHighlight(getResources().getString(R.string.plant_height),
                            ""+plant.getHeight_from()+"-"+plant.getHeight_to())+" "+Constants.HEIGHT_UNIT+"   ");
                    firstRowText.append(plant.getDescWithHighlight(getResources().getString(R.string.plant_flowering),
                            ""+ Utils.getMonthName(plant.getFlowering_from()-1)+"-"+Utils.getMonthName(plant.getFlowering_to() - 1)));
                    firstRow.setText(Html.fromHtml(firstRowText.toString()));

                    TextView upImage = (TextView) convertView.findViewById(R.id.up_image);
                    upImage.setText(Html.fromHtml(plant.getDescription()));

                    final int[][] spanIndex = new int[2][INFO_SECTIONS];
                    final StringBuilder text = new StringBuilder();
                    String[][] sections = { {getResources().getString(R.string.plant_flowers), plant.getFlower()},
                            {getResources().getString(R.string.plant_inflorescences), plant.getInflorescence()},
                            {getResources().getString(R.string.plant_fruits), plant.getFruit()},
                            {getResources().getString(R.string.plant_leaves), plant.getLeaf()},
                            {getResources().getString(R.string.plant_stem), plant.getStem()},
                            {getResources().getString(R.string.plant_habitat), plant.getHabitat()}
                    };

                    for(int i = 0; i < INFO_SECTIONS; i++ ) {
                        spanIndex[0][i] = text.length();;
                        spanIndex[1][i] = text.length() + sections[i][0].length();
                        text.append(sections[i][0] + ": " + sections[i][1] + " ");
                        //text.append("\n");
                    }

                    final TextView nextToImage = (TextView) convertView.findViewById(R.id.next_to_image);
                    final ImageView drawing = (ImageView) convertView.findViewById(R.id.plant_background);
                    Point size = new Point();
                    getActivity().getWindowManager().getDefaultDisplay().getSize(size);
                    drawing.setMaxWidth(size.x / 3);
                    final SpannableString ss = new SpannableString(text.toString());
                    for(int i = 0; i < INFO_SECTIONS; i++ ) {
                        ss.setSpan(new StyleSpan(Typeface.BOLD), spanIndex[0][i], spanIndex[1][i],
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (plant.getBack_url() != null) {
                        ImageLoader.getInstance().displayImage(plant.getBack_url(), drawing, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                int leftMargin = loadedImage.getWidth() + 10;
                                int height = loadedImage.getHeight();
                                ss.setSpan(new Margin(height / (int) (nextToImage.getLineHeight() * nextToImage
                                        .getLineSpacingMultiplier() + nextToImage.getLineSpacingExtra()) + 2,
                                        leftMargin), 0, ss.length(), Spanned.SPAN_PARAGRAPH);
                                nextToImage.setText(ss);
                            }
                        });

                    } else {
                        nextToImage.setText(ss);
                    }

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

                    ImageView image = (ImageView) convertView.findViewById(R.id.plant_photo);
                    if (plant.getPhoto_urls().size() > thumbnail_position && plant.getPhoto_urls().get(thumbnail_position) != null) {
                        ImageLoader.getInstance().displayImage(plant.getPhoto_urls().get(thumbnail_position), image,
                                ((HerbsApp)getActivity().getApplication()).getOptions());
                    } else if (plant.getPhoto_urls().size() > 0 && plant.getPhoto_urls().get(0) != null) {
                        ImageLoader.getInstance().displayImage(plant.getPhoto_urls().get(0), image,
                                ((HerbsApp)getActivity().getApplication()).getOptions());
                    }

                    break;
                case CARD_SOURCES:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_card_sources, parent, false);
                    TextView sources = (TextView) convertView.findViewById(R.id.plant_source_urls);

                    String[] source_urls = new String[plant.getSource_urls().size()];
                    plant.getSource_urls().toArray(source_urls);

                    StringBuilder textUrl = new StringBuilder();
                    for (String url : source_urls) {
                        textUrl.append(url);
                        textUrl.append("<br/>");
                    }

                    sources.setText(Html.fromHtml(textUrl.toString()));

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
