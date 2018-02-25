package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sk.ab.common.util.Utils;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsplus.BuildConfig;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;
import sk.ab.herbsplus.util.UtilsPlus;

/**
 * @see FilterPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class FilterPlantsPlusActivity extends FilterPlantsBaseActivity {

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_init, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_search_init:
                intent = new Intent(this, NameSearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_observations:
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    intent = new Intent(this, ListObservationsActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog dialogBox = UtilsPlus.LoginDialog(this);
                    dialogBox.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AndroidConstants.REQUEST_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    getMenuFragment().manageUserSettings();
                    UtilsPlus.saveToken(this);
                } else {
                    // Sign in failed, check response for error code
                    Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    protected void getCount() {
        isLoading = true;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseRef = database.getReference(AndroidConstants.FIREBASE_COUNTS + AndroidConstants.SEPARATOR
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
        intent.putExtra(AndroidConstants.STATE_LIST_PATH, AndroidConstants.FIREBASE_LISTS + AndroidConstants.SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES));
        startActivity(intent);
        stopLoading();
        setCountButton();
        isLoading = false;
    }

    @Override
    protected PropertyListBaseFragment getNewMenuFragment() {
        return new PropertyListPlusFragment();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    public int getAppVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public boolean isAdsAllowed() {
        return false;
    }

    @Override
    protected void handleSynchronizationDownload(Integer number, Integer countAll) {
        if (menu != null) {
            MenuItem synchronizationMenuItem = menu.findItem(R.id.menu_synchronization_download);

            if (number != null && number > -1) {
                synchronizationMenuItem.setVisible(true);
                synchronizationMenuItem.setTitle("▼" + (countAll - number));
            } else {
                synchronizationMenuItem.setVisible(false);
                synchronizationMenuItem.setTitle("");
            }
        }
    }

    @Override
    protected void handleSynchronizationUpload(Integer number, Integer countAll) {
        if (menu != null) {
            MenuItem synchronizationMenuItem = menu.findItem(R.id.menu_synchronization_upload);

            if (number != null && number > -1) {
                synchronizationMenuItem.setVisible(true);
                synchronizationMenuItem.setTitle("▲" + (countAll - number));
            } else {
                synchronizationMenuItem.setVisible(false);
                synchronizationMenuItem.setTitle("");
            }
        }
    }

}
