package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sk.ab.commons.BaseActivity;
import sk.ab.commons.BaseFilterFragment;
import sk.ab.commons.PropertyListFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.rest.HerbCountResponderFragment;
import sk.ab.herbs.fragments.rest.HerbListResponderFragment;
import sk.ab.tools.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * <p/>
 * Activity for filtering plants
 */
public class FilterPlantsActivity extends BaseActivity {
    private Locale locale;

    private BaseFilterFragment mContent;

    protected PropertyListFragment mPropertyMenu;
    protected HerbCountResponderFragment countResponder;
    protected HerbListResponderFragment listResponder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        locale = Utils.changeLocale(this, language);

        setContentView(R.layout.base_activity);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mPropertyMenu = (PropertyListFragment)fm.findFragmentById(R.id.menu_fragment);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getCurrentFragment().getTitle());
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getCurrentFragment().getTitle());
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        countResponder = (HerbCountResponderFragment) fm.findFragmentByTag("RESTCountResponder");
        if (countResponder == null) {
            countResponder = new HerbCountResponderFragment();
            ft.add(countResponder, "RESTCountResponder");
        }
        listResponder = (HerbListResponderFragment) fm.findFragmentByTag("RESTListResponder");
        if (listResponder == null) {
            listResponder = new HerbListResponderFragment();
            ft.add(listResponder, "RESTListResponder");
        }

        ft.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!locale.equals(Locale.getDefault())) {
            recreate();
        } else {
            int position = getCurrentPosition();
            if (getIntent().getExtras() != null) {
                position = getIntent().getExtras().getInt("position");
            }
            switchContent(getFilterAttributes().get(position));
            removeFromFilter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((HerbsApp)getApplication()).getCount() <= Constants.LIST_THRESHOLD
                        && ((HerbsApp)getApplication()).getCount() > 0) {
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
        return true;
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        for(BaseFilterFragment filterFragment : getFilterAttributes()) {
            ViewGroup viewGroup = (ViewGroup)filterFragment.getView();
            if (viewGroup != null) {
                viewGroup.removeAllViewsInLayout();
                getLayoutInflater().inflate(filterFragment.getLayout(), viewGroup);
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(count-1);
            String str = backEntry.getName();
            Fragment fragment = fm.findFragmentByTag(str);
            if (fragment instanceof BaseFilterFragment) {
                setCurrentFragment((BaseFilterFragment)fragment);
                removeFromFilter();
                closeDrawer();
            }
        }
        super.onBackPressed();
    }

    public void switchContent(final BaseFilterFragment fragment) {
        if (getCurrentFragment() == null || !getCurrentFragment().equals(fragment)) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (getCurrentFragment() != null) {
                fragmentTransaction.addToBackStack(getCurrentFragment().getTag());
            }
            fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttributeId());
            fragmentTransaction.commit();

            setCurrentFragment(fragment);
        }

        closeDrawer();
    }

    public void addToFilter(Integer valueId) {
        loading();
        getFilter().put(getCurrentFragment().getAttributeId(), valueId);

        countResponder.getCount();

        mPropertyMenu.getListView().invalidateViews();
    }

    public void removeFromFilter() {
        loading();
        getFilter().remove(getCurrentFragment().getAttributeId());

        countResponder.getCount();

        mPropertyMenu.getListView().invalidateViews();
    }

    public void clearFilter() {
        loading();
        getFilter().clear();

        countResponder.getCount();

        mPropertyMenu.getListView().invalidateViews();
        switchContent(getFilterAttributes().get(0));
    }

    public void loadResults() {
        loading();
        listResponder.getList();
    }

    public void setCount(int count) {
        ((HerbsApp)getApplication()).setCount(count);

        if (getFilterAttributes().size() == getFilter().size() && ((HerbsApp)getApplication()).getCount() > 0) {
            loadResults();
        } else {
            countButton.setEnabled(true);
            invalidateOptionsMenu();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ((HerbsApp)getApplication()).setLoading(false);
        }
    }

    public void setResults(List<PlantHeader> herbs) {
        Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
        intent.putParcelableArrayListExtra("results", (ArrayList<PlantHeader>) herbs);
        startActivity(intent);
        invalidateOptionsMenu();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ((HerbsApp)getApplication()).setLoading(false);
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return ((HerbsApp)getApplication()).getFilterAttributes();
    }

    public Map<Integer, Integer> getFilter() { return ((HerbsApp)getApplication()).getFilter(); }

    public BaseFilterFragment getCurrentFragment() {
        return mContent;
    }

    public void setCurrentFragment(BaseFilterFragment fragment) {
        mContent = fragment;
        getSupportActionBar().setTitle(mContent.getTitle());
    }

    public int getCurrentPosition() {
        int position = 0;
        if (mContent != null)
            position = getFilterAttributes().indexOf(mContent);
        return position;
    }
}

