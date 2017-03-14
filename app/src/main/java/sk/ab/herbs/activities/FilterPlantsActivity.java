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

import sk.ab.common.entity.Count;
import sk.ab.common.entity.PlantList;
import sk.ab.common.util.Utils;
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
        DatabaseReference mFirebaseRef = database.getReference(AndroidConstants.FIREBASE_FILTERS + AndroidConstants.FIREBASE_SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES) + AndroidConstants.FIREBASE_SEPARATOR
                + AndroidConstants.FIREBASE_COUNT);

        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Count count = dataSnapshot.getValue(Count.class);
                setCount(count.getCount());
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseRef = database.getReference(AndroidConstants.FIREBASE_FILTERS + AndroidConstants.FIREBASE_SEPARATOR
                + Utils.getFilterKey(filter, SpecificConstants.FILTER_ATTRIBUTES) + AndroidConstants.FIREBASE_SEPARATOR
                + AndroidConstants.FIREBASE_LIST);

        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlantList plantList = dataSnapshot.getValue(PlantList.class);

                Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
                intent.putParcelableArrayListExtra(AndroidConstants.STATE_PLANT_LIST,
                        insertRateView(getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE), plantList.getItems()));
                intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
                intent.putExtra(AndroidConstants.STATE_FILTER, filter);
                startActivity(intent);
                stopLoading();
                setCountButton();
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
    protected SharedPreferences getSharedPreferences() {
        return getSharedPreferences(AndroidConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
