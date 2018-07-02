package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;
import java.util.Locale;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.activities.SearchBaseActivity;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.util.UtilsPlus;

/**
 *
 * Created by adrian on 25. 6. 2018.
 */

public class PhotoSearchActivity extends SearchBaseActivity {

    private class LabelsAdapter extends BaseAdapter {

        private List<FirebaseVisionCloudLabel> labels;

        private LabelsAdapter(List<FirebaseVisionCloudLabel> labels) {
            super();
            this.labels = labels;
        }

        @Override
        public int getCount() {
            return labels.size();
        }

        @Override
        public Object getItem(int i) {
            return labels.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.photo_search_label, container, false);
            }

            FirebaseVisionCloudLabel label = (FirebaseVisionCloudLabel) getItem(position);

            ((TextView) convertView.findViewById(R.id.tvLabel))
                    .setText(label.getLabel());
            ((TextView) convertView.findViewById(R.id.tvConfidence))
                    .setText(String.format(Locale.getDefault(), "%.2f", label.getConfidence()));
            return convertView;
        }
    }

    private Uri mCurrentPhotoUri;
    private ListView photoSearchListView;
    private ScrollView photoSearchScrollView;
    private ScrollView photoSearchNoteScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.search_by_photo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        photoSearchListView = findViewById(R.id.lvPhotoSearchResults);
        photoSearchScrollView = findViewById(R.id.svPhotoSearchResults);
        photoSearchNoteScrollView = findViewById(R.id.svPhotoSearchNote);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photo_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_take_photo:
                mCurrentPhotoUri = UtilsPlus.addCameraPhoto(this, null);
                break;
            case R.id.menu_choose_photo:
                mCurrentPhotoUri = UtilsPlus.addGalleryPhoto(this, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AndroidConstants.REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    processPhoto(mCurrentPhotoUri);
                } else {
                    Toast.makeText(this, R.string.camera_failed, Toast.LENGTH_LONG).show();
                }
                break;
            case AndroidConstants.REQUEST_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    processPhoto(data.getData());
                } else {
                    Toast.makeText(this, R.string.gallery_failed, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected Class getFilterPlantsActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected Class getListPlantsActivityClass() {
        return ListPlantsPlusActivity.class;
    }

    @Override
    protected Class getDisplayPlantActivityClass() {
        return DisplayPlantPlusActivity.class;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.photo_search_activity;
    }

    private void processPhoto(Uri uri) {
        ImageView photoView = findViewById(R.id.iwPhoto);
        ImageLoader.getInstance().displayImage(uri.toString(), new ImageViewAware(photoView), ((BaseApp) getApplication()).getOptions(),
                new ImageSize(AndroidConstants.IMAGE_SIZE, AndroidConstants.IMAGE_SIZE),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        getLabels(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }, null);
    }

    private void getLabels(Bitmap loadedImage) {
        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(10)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(loadedImage);
        FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                                saveLabels(labels);
                                processLabels(labels);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PhotoSearchActivity.this, R.string.photo_search_failed, Toast.LENGTH_LONG).show();
                            }
                        });
    }

    private void saveLabels(List<FirebaseVisionCloudLabel> labels) {
        photoSearchNoteScrollView.setVisibility(View.GONE);
        photoSearchScrollView.setVisibility(View.VISIBLE);

    }

    private void processLabels(List<FirebaseVisionCloudLabel> labels) {
        photoSearchListView.setAdapter(new LabelsAdapter(labels));
    }
}
