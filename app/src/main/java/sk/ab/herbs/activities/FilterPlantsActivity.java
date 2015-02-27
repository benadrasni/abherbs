package sk.ab.herbs.activities;

import android.content.Intent;
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
    private boolean ignoreBack;

    protected PropertyListFragment mPropertyMenu;
    protected HerbCountResponderFragment countResponder;
    protected HerbListResponderFragment listResponder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

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
            loading();
            countResponder.getCount();
        }

        switchContent(getFilterAttributes().get(position));
        removeFromFilter(getCurrentFragment().getAttributeId());
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
        mPropertyMenu.getListView().invalidateViews();
    }

    @Override
    public void onBackPressed() {
        if (ignoreBack) {
            ignoreBack = false;
            removeFromFilter(getCurrentFragment().getAttributeId());
        } else {
            FragmentManager fm = getSupportFragmentManager();
            int count = fm.getBackStackEntryCount();
            if (count > 0) {
                FragmentManager.BackStackEntry backEntry = fm.getBackStackEntryAt(count - 1);
                String str = backEntry.getName();
                Fragment fragment = fm.findFragmentByTag(str);
                if (fragment instanceof BaseFilterFragment) {
                    setCurrentFragment((BaseFilterFragment) fragment);
                    removeFromFilter(Integer.parseInt(str));
                }
            }
            closeDrawer();
            super.onBackPressed();
        }
    }

    public void switchContent(final BaseFilterFragment fragment) {
        if (getCurrentFragment() == null || !getCurrentFragment().equals(fragment)) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            if (getCurrentFragment() != null) {
                fragmentTransaction.addToBackStack(getCurrentFragment().getTag());
            }
            fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttributeId());
            fragmentTransaction.commit();

            setCurrentFragment(fragment);
        }
    }

    public void addToFilter(Integer valueId) {
        loading();
        getFilter().put(getCurrentFragment().getAttributeId(), valueId);

        countResponder.getCount();

        mPropertyMenu.getListView().invalidateViews();
    }

    public void removeFromFilter(int attrId) {
        if (getFilter().get(attrId) != null) {
            loading();
            getFilter().remove(attrId);
            countResponder.getCount();
            mPropertyMenu.getListView().invalidateViews();
        }
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
        HerbsApp app = (HerbsApp)getApplication();
        app.setCount(count);
        ignoreBack = false;

        if (getFilterAttributes().size() == getFilter().size() && app.getCount() > 0) {
            loadResults();
        } else {
            if (app.getCount() == 0) {
                ignoreBack = true;
            } else {
                if (getFilter().get(getCurrentFragment().getAttributeId()) != null
                        && getFilterAttributes().size() > getFilter().size()) {
                    for (BaseFilterFragment fragment : getFilterAttributes()) {
                        if (getFilter().get(fragment.getAttributeId()) == null) {
                            switchContent(fragment);
                            break;
                        }
                    }
                }
            }
            countButton.setEnabled(true);
            invalidateOptionsMenu();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            app.setLoading(false);
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

