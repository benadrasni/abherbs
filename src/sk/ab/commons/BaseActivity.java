package sk.ab.commons;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import sk.ab.herbs.*;
import sk.ab.herbs.activities.ListPlantsActivity;
import sk.ab.herbs.fragments.HerbCountResponderFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseActivity extends SlidingFragmentActivity {
  private final Map<Integer, Object> filter = new HashMap<Integer, Object>();

  protected int count;
  protected int position;
  protected List<BaseFilterFragment> filterAttributes;
  protected List<PlantHeader> results;
  protected Fragment mContent;
	protected PropertyListFragment mPropertyMenu;
  protected HerbCountResponderFragment responder;

  @Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // set the Content View
    setContentView(R.layout.filter_content_frame);

    SlidingMenu sm = getSlidingMenu();
    sm.setMode(SlidingMenu.LEFT);

    FragmentManager     fm = getSupportFragmentManager();
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

    responder = (HerbCountResponderFragment) fm.findFragmentByTag("RESTResponder");
    if (responder == null) {
      responder = new HerbCountResponderFragment();
      ft.add(responder, "RESTResponder");
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
        filter.clear();
        responder.getCount();
        unlockMenu();
        results = TestPlants.getInitial();
        invalidateOptionsMenu();
        break;
    }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.filter, menu);
		return true;
	}

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {

    MenuItem item = menu.findItem(R.id.count);
    Button b = (Button)item.getActionView().findViewById(R.id.countButton);
    b.setText(""+count);

    return super.onPrepareOptionsMenu(menu);
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
      filter.put(getCurrentFragment().getAttributeId(), object);
      getCurrentFragment().setLock(true);

      int visiblePosition = mPropertyMenu.getListView().getFirstVisiblePosition();
      View v = mPropertyMenu.getListView().getChildAt(position - visiblePosition);

      ImageView checkImageView = (ImageView)v.findViewById(R.id.row_check);
      checkImageView.setVisibility(View.VISIBLE);

      responder.getCount();
    }
  }

  public void unlockMenu() {
    for(int i = mPropertyMenu.getListView().getFirstVisiblePosition(); i <= mPropertyMenu.getListView().getLastVisiblePosition(); i++) {
      View v = mPropertyMenu.getListView().getChildAt(i);
      ImageView checkImageView = (ImageView)v.findViewById(R.id.row_check);
      checkImageView.setVisibility(View.GONE);
    }
    switchContent(0, filterAttributes.get(0));
  }

  public void showResultsMenu(View v) {
    Intent intent = new Intent(getBaseContext(), ListPlantsActivity.class);
    intent.putParcelableArrayListExtra("results", (ArrayList<PlantHeader>)getResults());
    intent.putExtra("position", position);
    startActivity(intent);
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<BaseFilterFragment> getFilterAttributes() {
    return filterAttributes;
  }

  public List<PlantHeader> getResults() {
    return results;
  }

  public void setResults() {
    results = TestPlants.getPlants(results.size());
    invalidateOptionsMenu();
  }

  public Map<Integer, Object> getFilter() {
    return filter;
  }

  public BaseFilterFragment getCurrentFragment() {
    return (BaseFilterFragment)mContent;
  }

  public int getPosition() {
    return position;
  }
}
