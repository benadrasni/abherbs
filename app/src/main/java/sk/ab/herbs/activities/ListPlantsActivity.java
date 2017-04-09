package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.Plant;
import sk.ab.common.util.Utils;
import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantActivity;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.entity.PlantParcel;

/**
 * @see ListPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class ListPlantsActivity extends ListPlantsBaseActivity {

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(AndroidConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
