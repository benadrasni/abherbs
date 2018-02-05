package sk.ab.herbsbase.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.tools.OnSwipeTouchListener;
import sk.ab.herbsbase.tools.Utils;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class GalleryFragment extends Fragment {
    private int thumbnail_position;
    private ImageView photoView;

    private class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {
        private String urls[];

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;

            ViewHolder(View v) {
                super(v);
                mImageView = (ImageView) v.findViewById(R.id.image);
            }
        }

        private ThumbnailAdapter(String[] urls) {
            this.urls = urls;
        }

        @Override
        public ThumbnailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final String url = urls[position];
            Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS + Utils.getThumbnailUrl(url),
                    holder.mImageView, ((BaseApp) getActivity().getApplication()).getOptions());

            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumbnail_position = position;
                    Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS + url,
                            photoView, ((BaseApp) getActivity().getApplication()).getOptions());
                }
            });
        }

        @Override
        public int getItemCount() {
            return urls.length;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plant_card_gallery, null);
        photoView = (ImageView) view.findViewById(R.id.plant_photo);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            final FirebasePlant plant = ((DisplayPlantBaseActivity) getActivity()).getPlant();
            RecyclerView thumbnails = (RecyclerView) getView().findViewById(R.id.plant_thumbnails);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            layoutManager.scrollToPosition(thumbnail_position);
            thumbnails.setLayoutManager(layoutManager);

            String[] urls = new String[plant.getPhotoUrls().size()];
            plant.getPhotoUrls().toArray(urls);

            ThumbnailAdapter adapter = new ThumbnailAdapter(urls);
            thumbnails.setAdapter(adapter);

            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            int size = dm.widthPixels - Utils.convertDpToPx(20, dm);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                size = size / 2;
            }
            photoView.getLayoutParams().width = size;
            photoView.getLayoutParams().height = size;

            displayImage(photoView, plant, thumbnail_position);
            photoView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeTop() {

                }

                public void onSwipeRight() {
                    if (thumbnail_position > 0) {
                        thumbnail_position--;
                        displayImage(photoView, plant, thumbnail_position);
                    }
                }

                public void onSwipeLeft() {
                    if (thumbnail_position < plant.getPhotoUrls().size() - 1) {
                        thumbnail_position++;
                        displayImage(photoView, plant, thumbnail_position);
                    }
                }

                public void onSwipeBottom() {

                }

            });
        }
    }

    private void displayImage(ImageView image, FirebasePlant plant, int position) {
        if (plant.getPhotoUrls().size() > position && plant.getPhotoUrls().get(position) != null) {
            Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS
                    + plant.getPhotoUrls().get(position), image, ((BaseApp) getActivity().getApplication()).getOptions());
        }
    }
}

