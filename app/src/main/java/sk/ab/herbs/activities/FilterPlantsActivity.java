package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sk.ab.common.util.Utils;
import sk.ab.herbs.BuildConfig;
import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;

/**
 * @see FilterPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class FilterPlantsActivity extends FilterPlantsBaseActivity {

    @Override
    protected void getCount() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseRef = database.getReference(AndroidConstants.FIREBASE_COUNTS + AndroidConstants.FIREBASE_SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES));

        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer count = dataSnapshot.getValue(Integer.class);
                setCount(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        });
    }

    @Override
    protected void getList() {
        Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
        intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        intent.putExtra(AndroidConstants.STATE_FILTER, filter);
        intent.putExtra(AndroidConstants.STATE_LIST_PATH, AndroidConstants.FIREBASE_LISTS + AndroidConstants.FIREBASE_SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES));
        startActivity(intent);
        stopLoading();
        setCountButton();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    public String getAppVersion() {
        return SpecificConstants.PACKAGE.substring(SpecificConstants.PACKAGE.lastIndexOf(".")+1) + AndroidConstants.FIREBASE_SEPARATOR + BuildConfig.VERSION_CODE;
    }

}
