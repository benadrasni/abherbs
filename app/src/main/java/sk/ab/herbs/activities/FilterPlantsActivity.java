package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;

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

import java.util.*;

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

    private Map<Integer, Integer> filter;
    private int count;
    private BaseFilterFragment mContent;

    protected PropertyListFragment mPropertyMenu;
    protected HerbCountResponderFragment countResponder;
    protected HerbListResponderFragment listResponder;

    private Button countButton;
    private AnimationDrawable loadingAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        locale = Utils.changeLocale(this, language);
        count = preferences.getInt(Constants.COUNT_KEY, Constants.NUMBER_OF_PLANTS);
        filter = new HashMap<>();

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

        if (mContent == null && getFilterAttributes().size() > 0) {
            switchContent(getFilterAttributes().get(0));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!locale.equals(Locale.getDefault())) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        MenuItem item = menu.findItem(R.id.count);
        countButton = (Button) item.getActionView().findViewById(R.id.countButton);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadResults();
            }
        });
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading();
                filter.clear();
                countResponder.getCount();
                unlockMenu();
                return true;
            }
        });
        loadingAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.loading);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        countButton.setText("" + count);
        return super.onPrepareOptionsMenu(menu);
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
                mContent = (BaseFilterFragment)fragment;
                getSupportActionBar().setTitle(mContent.getTitle());
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

            mContent = fragment;
            getSupportActionBar().setTitle(mContent.getTitle());
        }

        closeDrawer();
    }

    public void addToFilter(Integer valueId) {
        loading();
        filter.put(getCurrentFragment().getAttributeId(), valueId);
        getCurrentFragment().setLock(true);

        int visiblePosition = mPropertyMenu.getListView().getFirstVisiblePosition();
        int position = getFilterAttributes().indexOf(getCurrentFragment());
        View v = mPropertyMenu.getListView().getChildAt(position - visiblePosition);

        ImageView checkImageView = (ImageView) v.findViewById(R.id.row_check);
        checkImageView.setVisibility(View.VISIBLE);

        countResponder.getCount();
    }

    public void unlockMenu() {
        for (int i = mPropertyMenu.getListView().getFirstVisiblePosition(); i <= mPropertyMenu.getListView().getLastVisiblePosition(); i++) {
            View v = mPropertyMenu.getListView().getChildAt(i);
            ImageView checkImageView = (ImageView) v.findViewById(R.id.row_check);
            if (checkImageView != null) {
                checkImageView.setVisibility(View.GONE);
            }
        }
        switchContent(getFilterAttributes().get(0));
    }

    public void loadResults() {
        loading();
        listResponder.getList();
    }

    public void setCount(int count) {
        this.count = count;
        if (filter.size() == 0) {
            SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(Constants.COUNT_KEY, count);
            editor.apply();
        }
        countButton.setEnabled(true);
        invalidateOptionsMenu();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void setResults(List<PlantHeader> herbs) {
        Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
        intent.putParcelableArrayListExtra("results", (ArrayList<PlantHeader>) herbs);
        startActivity(intent);
        invalidateOptionsMenu();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return ((HerbsApp)getApplication()).getFilterAttributes();
    }

    public Map<Integer, Integer> getFilter() { return filter; }

    public BaseFilterFragment getCurrentFragment() {
        return mContent;
    }

    public int getCurrentPosition() { return getFilterAttributes().indexOf(mContent); }

    private void loading() {
        countButton.setEnabled(false);
        countButton.setText("");
        countButton.setBackground(loadingAnimation);
        loadingAnimation.start();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}

