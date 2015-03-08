package sk.ab.herbs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import sk.ab.herbs.HerbsApp;
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
public class GalleryFragment extends Fragment {
    private static final String THUMBNAIL_DIR = "/.thumbnails";

    private int thumbnail_position;

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
            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            imageView.getLayoutParams().width = (dm.widthPixels - Utils.convertDpToPx(25, dm))/2;
            imageView.getLayoutParams().height = imageView.getLayoutParams().width;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plant_card_gallery, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            setGallery(((DisplayPlantActivity) getActivity()).getPlant(), getView());
        }
    }

    private void setGallery(Plant plant, View convertView) {
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
    }}

