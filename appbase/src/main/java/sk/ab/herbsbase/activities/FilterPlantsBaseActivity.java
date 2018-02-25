package sk.ab.herbsbase.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.commons.BaseFilterFragment;
import sk.ab.herbsbase.commons.RateDialogFragment;
import sk.ab.herbsbase.tools.Utils;

/**
 * Main activity which handles all filter fragments.
 *
 */
public abstract class FilterPlantsBaseActivity extends BaseActivity {


    private long mLastClickTime;
    private Integer filterPosition;
    private BaseFilterFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Application);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
            filterPosition = savedInstanceState.getInt(AndroidConstants.STATE_FILTER_POSITION);
        } else if (getIntent().getExtras() != null) {
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
            String pos = getIntent().getExtras().getString(AndroidConstants.STATE_FILTER_POSITION);
            if (pos != null) {
                filterPosition = Integer.parseInt(pos);
            }
            String clearFilter = getIntent().getExtras().getString(AndroidConstants.STATE_FILTER_CLEAR);
            if (clearFilter != null && filter != null) {
                filter.clear();
            }
        }

        if (filter == null) {
            filter = new HashMap<>();
        }

        overlay = findViewById(R.id.overlay);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        countButton = findViewById(R.id.countButton);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    if (count > 0) {
                        isLoading = true;
                        startLoading();
                        getList();
                    }
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

        countText = findViewById(R.id.countText);

        FragmentManager.enableDebugLogging(true);
        FragmentManager fm = getSupportFragmentManager();

        mPropertyMenu = getNewMenuFragment();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.menu_content, mPropertyMenu);
        fragmentTransaction.commit();

        mDrawerLayout = findViewById(R.id.drawer_layout);

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

        getCount();

        showRateDialog();

        updateVersionDialog();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isLoading) {
            startLoading();
        } else {
            stopLoading();
        }

        if (filterPosition == null) {
            filterPosition = 0;
        }

        switchContent(getFilterAttributes().get(filterPosition));
        removeFromFilter(currentFragment.getAttribute());
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getIntent().putExtra(AndroidConstants.STATE_FILTER_POSITION, filterPosition);
        getIntent().putExtra(AndroidConstants.STATE_FILTER, filter);

        recreate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(AndroidConstants.STATE_FILTER_POSITION, filterPosition);
        savedInstanceState.putSerializable(AndroidConstants.STATE_FILTER, filter);

        //super.onSaveInstanceState(savedInstanceState);
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
                    backStack.pop();
                    if (backStack.size() > 0) {
                        BaseFilterFragment fragment = backStack.get(backStack.size() - 1);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttribute());
                        fragmentTransaction.commitAllowingStateLoss();
                        setCurrentFragment(fragment);
                        removeFromFilter(fragment.getAttribute());
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.filter_activity;
    };


    public abstract boolean isAdsAllowed();

    public void switchContent(final BaseFilterFragment fragment) {
        Stack<BaseFilterFragment> backStack = getApp().getBackStack();
        backStack.remove(fragment);
        backStack.add(fragment);

        if (currentFragment == null || !currentFragment.equals(fragment)) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.filter_content, fragment, "" + fragment.getAttribute());
            fragmentTransaction.commitAllowingStateLoss();
        }

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
            if (count > 0) {
                if (filter.get(currentFragment.getAttribute()) != null && getFilterAttributes().size() > filter.size()) {
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
        isLoading = false;
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

    protected abstract void getCount();

    protected abstract void getList();

    protected abstract int getAppVersionCode();

    private List<BaseFilterFragment> getFilterAttributes() {
        return getApp().getFilterAttributes();
    }

    private int getCurrentPosition() {
        int position = 0;
        if (currentFragment != null)
            position = getFilterAttributes().indexOf(currentFragment);
        return position;
    }

    private void showRateDialog() {
        SharedPreferences preferences = getSharedPreferences();

        int rateState = preferences.getInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NO);
        if (BaseApp.isNetworkAvailable(getApplicationContext()) && rateState == AndroidConstants.RATE_SHOW) {
            final LinearLayout rateLayout = findViewById(R.id.ratingQuestion);
            rateLayout.setVisibility(View.VISIBLE);

            Button bYes = findViewById(R.id.likeYes);
            bYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialog = new RateDialogFragment();
                    dialog.show(getSupportFragmentManager(), "RateDialogFragment");
                    rateLayout.setVisibility(View.GONE);
                }
            });
            Button bNo = findViewById(R.id.likeNo);
            bNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences preferences = getSharedPreferences();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NO);
                    editor.putInt(AndroidConstants.RATE_COUNT_KEY, AndroidConstants.RATE_COUNTER);
                    editor.apply();

                    rateLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void updateVersionDialog() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String packageName = this.getBaseContext().getPackageName();
        DatabaseReference mFirebaseRefVersion = database.getReference(AndroidConstants.FIREBASE_VERSIONS + AndroidConstants.SEPARATOR
                + packageName.substring(packageName.lastIndexOf('.') + 1) + AndroidConstants.SEPARATOR + Build.VERSION.SDK_INT);
        mFirebaseRefVersion.keepSynced(true);
        mFirebaseRefVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    int currentVersion = ((Long) dataSnapshot.getValue()).intValue();
                    if (getAppVersionCode() < currentVersion) {
                        AlertDialog dialogBox = Utils.UpdateDialog(FilterPlantsBaseActivity.this);
                        dialogBox.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

