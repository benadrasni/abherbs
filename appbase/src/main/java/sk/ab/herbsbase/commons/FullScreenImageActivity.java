package sk.ab.herbsbase.commons;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;

import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;

/**
 * Created by adrian on 4.4.2016.
 */
public class FullScreenImageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Tracker tracker = ((BaseApp)getApplication()).getTracker();
        tracker.setScreenName(this.getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        setContentView(R.layout.full_screen);

        Bundle extras = getIntent().getExtras();
        String url = (String) extras.getSerializable("image_url");

        ImageView imgDisplay;
        Button btnClose;


        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        btnClose = (Button) findViewById(R.id.btnClose);


        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullScreenImageActivity.this.finish();
            }
        });

        ImageLoader.getInstance().displayImage(url, imgDisplay, ((BaseApp) getApplication()).getOptions());
    }

}
