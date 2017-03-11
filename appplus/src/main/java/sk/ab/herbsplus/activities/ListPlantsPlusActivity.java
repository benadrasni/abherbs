package sk.ab.herbsplus.activities;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sk.ab.common.entity.Plant;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantActivity;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.entity.PlantParcel;

/**
 * @see ListPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class ListPlantsPlusActivity extends ListPlantsBaseActivity {

    @Override
    public void selectPlant(int position) {
        startLoading();

        getApp().getFirebaseClient().getApiService().getDetail(getPlantList().get(position).getId()).enqueue(new Callback<Plant>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {
                Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
                intent.putExtra(AndroidConstants.STATE_PLANT, new PlantParcel(response.body()));
                intent.putExtra(AndroidConstants.STATE_FILTER, filter);
                startActivity(intent);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                stopLoading();
            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        });
    }
}
