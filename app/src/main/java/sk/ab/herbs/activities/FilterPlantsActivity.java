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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.request.CountRequest;
import sk.ab.common.entity.request.ListRequest;
import sk.ab.herbs.commons.BaseActivity;
import sk.ab.herbs.commons.BaseFilterFragment;
import sk.ab.herbs.commons.PropertyListFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * <p/>
 * Activity for filtering plants
 */
public class FilterPlantsActivity extends BaseActivity {

    private BaseFilterFragment mContent;

    protected PropertyListFragment mPropertyMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HerbsApp)getApplication()).getTracker().send(builder.build());

        setContentView(R.layout.filter_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((HerbsApp) getApplication()).getCount() <= Constants.LIST_THRESHOLD
                        && ((HerbsApp) getApplication()).getCount() > 0) {
                    loadResults();
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
                    getSupportActionBar().setTitle(getCurrentFragment().getTitle());
                }
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(getCurrentFragment().getTitle());
                }
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onStart() {
        super.onStart();

        int position = getCurrentPosition();
        if (getIntent().getExtras() != null) {
            String pos = getIntent().getExtras().getString("position");
            if (pos != null) {
                position = Integer.parseInt(pos);
            }
        }

        if (getCurrentFragment() == null) {
            startLoading();
            getCount();
        }

        switchContent(getFilterAttributes().get(position));
        removeFromFilter(getCurrentFragment().getAttribute());
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getIntent().putExtra("position", "" + ((HerbsApp) getApplication()).getFilterAttributes()
                .indexOf(getCurrentFragment()));

        recreate();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            HerbsApp app = (HerbsApp) getApplication();
            if (app.getCount() == 0) {
                removeFromFilter(getCurrentFragment().getAttribute());
            } else {
                Stack<BaseFilterFragment> backStack = app.getBackStack();
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
        if (getCurrentFragment() == null || !getCurrentFragment().equals(fragment)) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            if (getCurrentFragment() != null) {
                ((HerbsApp)getApplication()).getBackStack().add(getCurrentFragment());
            }
            fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttribute());
            fragmentTransaction.commit();

            setCurrentFragment(fragment);
        }
    }

    public void addToFilter(String value) {
        startLoading();
        getFilter().put(getCurrentFragment().getAttribute(), value);
        getCount();

        mPropertyMenu.getListView().invalidateViews();
    }

    public void removeFromFilter(String attribute) {
        if (getFilter().get(attribute) != null) {
            startLoading();
            getFilter().remove(attribute);
            getCount();
            mPropertyMenu.getListView().invalidateViews();
        }
    }

    public void clearFilter() {
        startLoading();
        getFilter().clear();
        getCount();

        mPropertyMenu.getListView().invalidateViews();
        switchContent(getFilterAttributes().get(0));
    }

    public void loadResults() {
        startLoading();
        getList();
    }

    public void setCount(int count) {
        HerbsApp app = (HerbsApp)getApplication();
        app.setCount(count);

        if (getFilterAttributes().size() == getFilter().size() && app.getCount() > 0) {
            loadResults();
        } else {
            if (app.getCount() != 0) {
                if (getFilter().get(getCurrentFragment().getAttribute()) != null
                        && getFilterAttributes().size() > getFilter().size()) {
                    for (BaseFilterFragment fragment : getFilterAttributes()) {
                        if (getFilter().get(fragment.getAttribute()) == null) {
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

    public void setResults(List<PlantHeader> herbs) {
        Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
        intent.putParcelableArrayListExtra("results", (ArrayList<PlantHeader>) herbs);
        startActivity(intent);
        stopLoading();
        setCountButton();
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return ((HerbsApp)getApplication()).getFilterAttributes();
    }

    public Map<String, String> getFilter() { return ((HerbsApp)getApplication()).getFilter(); }

    public BaseFilterFragment getCurrentFragment() {
        return mContent;
    }

    public void setCurrentFragment(BaseFilterFragment fragment) {
        mContent = fragment;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mContent.getTitle());
        }
    }

    public int getCurrentPosition() {
        int position = 0;
        if (mContent != null)
            position = getFilterAttributes().indexOf(mContent);
        return position;
    }

    private void getCount() {
        ((HerbsApp)getApplication()).getHerbCloudClient().getApiService().getCount(
                new CountRequest(sk.ab.common.Constants.PLANT, getFilter())).enqueue(new Callback<Count>() {
            @Override
            public void onResponse(Response<Count> response) {
                if (response != null && response.body() != null) {
                    setCount(response.body().getCount());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
            }
        });
    }

    private void getList() {
        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

        List<String> attributes = new ArrayList<>();
        attributes.add(sk.ab.common.Constants.PLANT_LABEL);
        attributes.add(sk.ab.common.Constants.PLANT_PHOTO_URL);
        attributes.add(sk.ab.common.Constants.PLANT_FAMILY);

        ((HerbsApp)getApplication()).getHerbCloudClient().getApiService().getList(
                new ListRequest(language,
                        getFilter(),
                        attributes)).enqueue(new Callback<Map<Integer, Map<String, List<String>>>>() {
            @Override
            public void onResponse(Response<Map<Integer,Map<String,List<String>>>> response) {
                List<PlantHeader> result = new ArrayList<>();

                SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
                int rateState = preferences.getInt(Constants.RATE_STATE_KEY, Constants.RATE_NO);

                int ratePosition = -1;
                if (rateState == Constants.RATE_SHOW) {
                    Random rand = new Random();
                    ratePosition = rand.nextInt(response.body().entrySet().size());
                }

                int position = 0;
                for (Map.Entry<Integer,Map<String,List<String>>> entry : response.body().entrySet()) {

                    Map<String,List<String>> attributes = entry.getValue();
                    String name = attributes.get(""+Constants.PLANT_NAME+"_0").get(0);
                    String url = attributes.get(""+Constants.PLANT_PHOTO_URL+"_0").get(0);
                    String family = attributes.get(""+Constants.PLANT_FAMILY+"_0").get(0);
                    String familyId = attributes.get(""+Constants.PLANT_FAMILY+"_0").get(1);

                    PlantHeader plantHeader = new PlantHeader(entry.getKey(),
                            name != null ? name : "",
                            url != null ? url : "",
                            family != null ? family : "",
                            familyId != null ? Integer.parseInt(familyId) : 0);

                    result.add(plantHeader);

                    if (position == ratePosition) {
                        result.add(new PlantHeader(0, "", "", "", 0));
                    }

                    position++;
                }

                setResults(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
            }
        });
    }
}

