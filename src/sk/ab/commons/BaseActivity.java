package sk.ab.commons;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import sk.ab.herbs.Constants;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.fragments.rest.HerbCountResponderFragment;
import sk.ab.herbs.fragments.rest.HerbListResponderFragment;
import sk.ab.tools.Utils;

import java.util.*;

public class BaseActivity extends ActionBarActivity {
    private Locale locale;
    private final Map<Integer, Object> filter = new HashMap<Integer, Object>();

    protected int count;
    protected int position;
    protected List<BaseFilterFragment> filterAttributes;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected Fragment mContent;
    protected PropertyListFragment mPropertyMenu;
    protected HerbCountResponderFragment countResponder;
    protected HerbListResponderFragment listResponder;

    private Button countButton;
    private AnimationDrawable loadingAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        locale = Utils.changeLocale(this, language);
        count = preferences.getInt(Constants.COUNT_KEY, R.integer.number_of_flowers);

        setContentView(R.layout.base_activity);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);
        Utils.changeLocale(this, language);

        for(BaseFilterFragment filterFragment : filterAttributes) {
            ViewGroup viewGroup = (ViewGroup)filterFragment.getView();
            if (viewGroup != null) {
                viewGroup.removeAllViewsInLayout();
                getLayoutInflater().inflate(filterFragment.getLayout(), viewGroup);
            }
        }

        ViewGroup viewGroup = (ViewGroup)mPropertyMenu.getView();
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
            getLayoutInflater().inflate(R.layout.property_list, viewGroup);
        }
    }

    public void switchContent(int position, final BaseFilterFragment fragment) {
        if (!getCurrentFragment().equals(fragment)) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.filter_content_frame, fragment);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();

            this.mContent = fragment;
            this.position = position;
        }
        mDrawerLayout.closeDrawers();
        mDrawerToggle.syncState();
    }

    public void addToFilter(Object object) {
        if (mContent instanceof BaseFilterFragment) {
            loading();
            filter.put(getCurrentFragment().getAttributeId(), object);
            getCurrentFragment().setLock(true);

            int visiblePosition = mPropertyMenu.getListView().getFirstVisiblePosition();
            View v = mPropertyMenu.getListView().getChildAt(position - visiblePosition);

            ImageView checkImageView = (ImageView) v.findViewById(R.id.row_check);
            checkImageView.setVisibility(View.VISIBLE);

            countResponder.getCount();
        }
    }

    public void unlockMenu() {
        for (int i = mPropertyMenu.getListView().getFirstVisiblePosition(); i <= mPropertyMenu.getListView().getLastVisiblePosition(); i++) {
            View v = mPropertyMenu.getListView().getChildAt(i);
            ImageView checkImageView = (ImageView) v.findViewById(R.id.row_check);
            if (checkImageView != null) {
                checkImageView.setVisibility(View.GONE);
            }
        }
        switchContent(0, filterAttributes.get(0));
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
            editor.commit();
        }
        countButton.setEnabled(true);
        invalidateOptionsMenu();
    }

    public void setResults(List<PlantHeader> herbs) {
        Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
        intent.putParcelableArrayListExtra("results", (ArrayList<PlantHeader>) herbs);
        intent.putExtra("position", position);
        startActivity(intent);
        invalidateOptionsMenu();
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return filterAttributes;
    }

    public Map<Integer, Object> getFilter() {
        return filter;
    }

    public BaseFilterFragment getCurrentFragment() {
        return (BaseFilterFragment) mContent;
    }

    public int getPosition() {
        return position;
    }

    private void loading() {
        countButton.setEnabled(false);
        countButton.setText("");
        countButton.setBackground(loadingAnimation);
        loadingAnimation.start();
    }
}
