package sk.ab.commons;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import sk.ab.herbs.Constants;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.ListPlantsActivity;
import sk.ab.herbs.fragments.rest.HerbCountResponderFragment;
import sk.ab.herbs.fragments.rest.HerbListResponderFragment;

import java.util.*;

public class BaseActivity extends SlidingFragmentActivity {
    private final Map<Integer, Object> filter = new HashMap<Integer, Object>();

    protected int count;
    protected int position;
    protected List<BaseFilterFragment> filterAttributes;
    protected List<PlantHeader> results;
    protected Fragment mContent;
    protected PropertyListFragment mPropertyMenu;
    protected HerbCountResponderFragment countResponder;
    protected HerbListResponderFragment listResponder;

    private Button countButton;
    private AnimationDrawable loadingAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the Content View
        setContentView(R.layout.filter_content_frame);

        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // check if the content frame contains the menu frame
        if (findViewById(R.id.filter_menu_frame) == null) {
            setBehindContentView(R.layout.filter_menu_frame);
            sm.setSlidingEnabled(true);
            sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
            sm.setFadeDegree(0.35f);
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            // show home as up so we can toggle
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // left menu
            mPropertyMenu = new PropertyListFragment();
            ft.replace(R.id.filter_menu_frame, mPropertyMenu);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                break;
            case R.id.clear:
                loading();
                filter.clear();
                countResponder.getCount();
                unlockMenu();
                listResponder.getList();
                break;
            case R.id.en:
                changeLocale(Constants.LANGUAGE_EN);
                Toast.makeText(this, R.string.locale_en, Toast.LENGTH_LONG).show();
                break;

            case R.id.sk:
                changeLocale(Constants.LANGUAGE_SK);
                Toast.makeText(this, R.string.locale_sk, Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        MenuItem item = menu.findItem(R.id.count);
        countButton = (Button) item.getActionView().findViewById(R.id.countButton);
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
        changeLocale(language);

        for(BaseFilterFragment filterFragment : filterAttributes) {
            ViewGroup viewGroup = (ViewGroup)filterFragment.getView();
            if (viewGroup != null) {
                viewGroup.removeAllViewsInLayout();
                getLayoutInflater().inflate(filterFragment.getLayout(), viewGroup);
            }
        }
    }

    public void switchContent(int position, final BaseFilterFragment fragment) {
        if (!getCurrentFragment().equals(fragment)) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.filter_content_frame, fragment);
            fragmentTransaction.addToBackStack(fragment.getTag());
            fragmentTransaction.commit();

            this.mContent = fragment;
            this.position = position;
        }
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                getSlidingMenu().showContent();
            }
        }, 50);
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
            checkImageView.setVisibility(View.GONE);
        }
        switchContent(0, filterAttributes.get(0));
    }

    public void showResultsMenu(View v) {
        Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
        intent.putParcelableArrayListExtra("results", (ArrayList<PlantHeader>) getResults());
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void setCount(int count) {
        this.count = count;
        countButton.setEnabled(true);
        //invalidateOptionsMenu();
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return filterAttributes;
    }

    public List<PlantHeader> getResults() {
        return results;
    }

    public void setResults(List<PlantHeader> herbs) {
        this.results = herbs;
    }

    public void setResults() {
        listResponder.getList();
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

    public void loading() {
        countButton.setEnabled(false);
        countButton.setBackground(loadingAnimation);
        loadingAnimation.start();
    }

    protected void changeLocale(String language) {
        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        editor.putString(Constants.LANGUAGE_DEFAULT_KEY, language);
        editor.commit();
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
