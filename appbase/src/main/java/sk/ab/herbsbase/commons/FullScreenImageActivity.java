package sk.ab.herbsbase.commons;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.tools.Utils;

/**
 * Activity for full screen image view
 *
 * Created by adrian on 4. 4. 2016.
 */
public class FullScreenImageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen);

        Bundle extras = getIntent().getExtras();
        String url = (String) extras.getSerializable("image_url");

        ImageView imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        Button btnClose = (Button) findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullScreenImageActivity.this.finish();
            }
        });

        Utils.displayImage(getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS + url,
                imgDisplay, ((BaseApp) getApplication()).getOptions());
    }
}
