package sk.ab.herbsplus.activities;

import android.app.Activity;
import android.os.Bundle;

import sk.ab.herbsplus.StorageLoading;

/**
 * Activity for disabling touch interface when synchronizing
 *
 * Created by adrian on 18. 4. 2017.
 */
public class TransparentActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StorageLoading storageLoading = new StorageLoading(this);
        storageLoading.downloadOfflineFiles();
    }
}
