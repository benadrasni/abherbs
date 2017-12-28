package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sk.ab.common.util.Utils;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsplus.BuildConfig;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;

/**
 * @see FilterPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class FilterPlantsPlusActivity extends FilterPlantsBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean offlineMode = getSharedPreferences().getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        if (offlineMode && BaseApp.isConnectedToWifi(getApplicationContext())) {
            Intent intent = new Intent(this, TransparentActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_init, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_init:
                Intent intent = new Intent(this, NameSearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void getCount() {
        isLoading = true;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseRef = database.getReference(AndroidConstants.FIREBASE_COUNTS + AndroidConstants.FIREBASE_SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES));

        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer count = dataSnapshot.getValue(Integer.class);
                if (count != null && !FilterPlantsPlusActivity.this.isDestroyed()) {
                    setCount(count);
                } else {
                    stopLoading();
                    isLoading = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
                isLoading = false;
            }
        });
    }

    @Override
    protected void getList() {
        Intent intent = new Intent(getBaseContext(), ListPlantsPlusActivity.class);
        intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        intent.putExtra(AndroidConstants.STATE_FILTER, filter);
        intent.putExtra(AndroidConstants.STATE_LIST_PATH, AndroidConstants.FIREBASE_LISTS + AndroidConstants.FIREBASE_SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES));
        startActivity(intent);
        stopLoading();
        setCountButton();
        isLoading = false;
    }

    @Override
    protected PropertyListBaseFragment getMenuFragment() {
        return new PropertyListPlusFragment();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    public String getAppVersion() {
        return SpecificConstants.PACKAGE.substring(SpecificConstants.PACKAGE.lastIndexOf(".")+1) + AndroidConstants.FIREBASE_SEPARATOR + BuildConfig.VERSION_CODE;
    }

    @Override
    public boolean isAdsAllowed() {
        return false;
    }
}
