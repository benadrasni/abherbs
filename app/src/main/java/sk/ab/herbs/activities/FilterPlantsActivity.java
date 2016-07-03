package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.entity.PlantList;
import sk.ab.common.entity.request.ListRequest;
import sk.ab.herbs.AndroidConstants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.herbs.commons.BaseActivity;
import sk.ab.herbs.commons.BaseFilterFragment;
import sk.ab.herbs.commons.PropertyListFragment;
import sk.ab.herbs.entity.PlantHeaderParcel;

/**
 * Main activity which handles all filter fragments.
 *
 */
public class FilterPlantsActivity extends BaseActivity {

    private Integer filterPosition;
    private BaseFilterFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Application);
        super.onCreate(savedInstanceState);

        filter = new HashMap<>();
        if (savedInstanceState != null) {
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
            filterPosition = savedInstanceState.getInt(AndroidConstants.STATE_FILTER_POSITION);
        } else if (getIntent().getExtras() != null) {
            String pos = getIntent().getExtras().getString(AndroidConstants.STATE_FILTER_POSITION);
            if (pos != null) {
                filterPosition = Integer.parseInt(pos);
            }
            String clearFilter = getIntent().getExtras().getString(AndroidConstants.STATE_FILTER_CLEAR);
            if (clearFilter != null) {
                filter.clear();
            }
        }

        getApp().getTracker().send(builder.build());

        setContentView(R.layout.filter_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count <= AndroidConstants.LIST_THRESHOLD  && count > 0) {
                    startLoading();
                    getList();
                }
            }
        });
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearFilter();
                return true;
            }
        });

        FragmentManager.enableDebugLogging(true);
        FragmentManager fm = getSupportFragmentManager();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mPropertyMenu = (PropertyListFragment)fm.findFragmentById(R.id.menu_fragment);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(currentFragment.getTitle());
                }
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(currentFragment.getTitle());
                }
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        startLoading();
        getCount();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (filterPosition == null) {
            filterPosition = 0;
        }

        switchContent(getFilterAttributes().get(filterPosition));
        removeFromFilter(currentFragment.getAttribute());
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getIntent().putExtra(AndroidConstants.STATE_FILTER_POSITION, "" + getFilterAttributes().indexOf(currentFragment));

        recreate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(AndroidConstants.STATE_FILTER_POSITION, getCurrentPosition());
        savedInstanceState.putSerializable(AndroidConstants.STATE_FILTER, filter);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            if (count == 0) {
                removeFromFilter(currentFragment.getAttribute());
            } else {
                Stack<BaseFilterFragment> backStack = getApp().getBackStack();
                if (backStack.size() > 0) {
                    BaseFilterFragment fragment = backStack.pop();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttribute());
                    fragmentTransaction.commit();
                    setCurrentFragment(fragment);
                    removeFromFilter(fragment.getAttribute());
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    public void switchContent(final BaseFilterFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (currentFragment == null || !currentFragment.equals(fragment)) {
            if (currentFragment != null) {
                getApp().getBackStack().remove(currentFragment);
                getApp().getBackStack().add(currentFragment);
            }
            fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttribute());
        } else {
            fragmentTransaction.detach(fragment);
            fragmentTransaction.attach(fragment);
        }
        fragmentTransaction.commit();

        setCurrentFragment(fragment);
    }

    public void addToFilter(String value) {
        startLoading();
        filter.put(currentFragment.getAttribute(), value);
        getCount();

        mPropertyMenu.getListView().invalidateViews();
    }

    public void removeFromFilter(String attribute) {
        if (filter.get(attribute) != null) {
            startLoading();
            filter.remove(attribute);
            getCount();
            mPropertyMenu.getListView().invalidateViews();
        }
    }

    public void clearFilter() {
        startLoading();
        filter.clear();
        getCount();

        mPropertyMenu.getListView().invalidateViews();
        switchContent(getFilterAttributes().get(0));
    }

    public void setCount(int count) {
        this.count = count;

        if (getFilterAttributes().size() == filter.size() && count > 0) {
            getList();
        } else {
            if (count != 0) {
                if (filter.get(currentFragment.getAttribute()) != null
                        && getFilterAttributes().size() > filter.size()) {
                    for (BaseFilterFragment fragment : getFilterAttributes()) {
                        if (filter.get(fragment.getAttribute()) == null) {
                            switchContent(fragment);
                            break;
                        }
                    }
                }
            }
            stopLoading();
            setCountButton();
        }
    }

    public BaseFilterFragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(BaseFilterFragment fragment) {
        currentFragment = fragment;
        filterPosition = getCurrentPosition();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentFragment.getTitle());
        }

    }

    private HerbsApp getApp() { return (HerbsApp)getApplication(); }

    private List<BaseFilterFragment> getFilterAttributes() {
        return ((HerbsApp)getApplication()).getFilterAttributes();
    }

    private int getCurrentPosition() {
        int position = 0;
        if (currentFragment != null)
            position = getFilterAttributes().indexOf(currentFragment);
        return position;
    }

    private void getCount() {

        ((HerbsApp)getApplication()).getHerbCloudClient().getApiService().getCount(
                new ListRequest(sk.ab.common.Constants.PLANT, filter)).enqueue(new Callback<Count>() {
            @Override
            public void onResponse(Response<Count> response) {
                if (response != null && response.body() != null) {
                    setCount(response.body().getCount());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        });
    }

    private void getList() {

        ((HerbsApp)getApplication()).getHerbCloudClient().getApiService().getList(
                new ListRequest(sk.ab.common.Constants.PLANT, filter)).enqueue(new Callback<PlantList>() {
            @Override
            public void onResponse(Response<PlantList> response) {
                List<PlantHeader> plantHeaderList = response.body().getItems();

                SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
                int rateState = preferences.getInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NO);

                if (rateState == AndroidConstants.RATE_SHOW) {
                    Random rand = new Random();
                    plantHeaderList.add(rand.nextInt(plantHeaderList.size()), new PlantHeader());
                }

                ArrayList<PlantHeaderParcel> plantHeaderParcelList = new ArrayList<>();
                for (PlantHeader plantHeader : plantHeaderList) {
                    PlantHeaderParcel plantHeaderParcel = new PlantHeaderParcel(plantHeader);
                    plantHeaderParcelList.add(plantHeaderParcel);
                }

                Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
                intent.putParcelableArrayListExtra(AndroidConstants.STATE_PLANT_LIST, plantHeaderParcelList);
                intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
                intent.putExtra(AndroidConstants.STATE_FILTER, filter);
                startActivity(intent);
                stopLoading();
                setCountButton();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        });
    }
}

