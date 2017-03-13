package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.PlantList;
import sk.ab.common.util.Utils;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;
import sk.ab.herbs.SpecificConstants;

/**
 * @see FilterPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class FilterPlantsActivity extends FilterPlantsBaseActivity {

    @Override
    protected void getCount() {
        getApp().getFirebaseClient().getApiService().getCount(Utils.getFilterKey(filter,
                SpecificConstants.FILTER_ATTRIBUTES)).enqueue(new Callback<Count>() {
            @Override
            public void onResponse(Call<Count> call, Response<Count> response) {
                if (response != null && response.body() != null) {
                    setCount(response.body().getCount());
                }
            }

            @Override
            public void onFailure(Call<Count> call, Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }

        });
    }

    @Override
    protected void getList() {
        getApp().getFirebaseClient().getApiService().getList(Utils.getFilterKey(filter,
                SpecificConstants.FILTER_ATTRIBUTES)).enqueue(new Callback<PlantList>() {
            @Override
            public void onResponse(Call<PlantList> call, Response<PlantList> response) {
                Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
                intent.putParcelableArrayListExtra(AndroidConstants.STATE_PLANT_LIST,
                        insertRateView(getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE), response.body().getItems()));
                intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
                intent.putExtra(AndroidConstants.STATE_FILTER, filter);
                startActivity(intent);
                stopLoading();
                setCountButton();
            }

            @Override
            public void onFailure(Call<PlantList> call, Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
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
