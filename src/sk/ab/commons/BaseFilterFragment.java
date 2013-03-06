package sk.ab.commons;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public abstract class BaseFilterFragment extends Fragment {

  protected int attributeId;
  protected int title;
  protected int iconRes;
  protected int layout;
  protected boolean lock;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    getActivity().setTitle(title);
    if (getActivity() instanceof SlidingFragmentActivity) {
      SherlockFragmentActivity activity = (SherlockFragmentActivity)getActivity();
      activity.getSupportActionBar().setIcon(iconRes);
    }

    return inflater.inflate(layout, null);
  }

  public int getAttributeId() {
    return attributeId;
  }

  public int getTitle() {
    return title;
  }

  public int getIconRes() {
    return iconRes;
  }

  public boolean isLock() {
    return lock;
  }

  public void setLock(boolean lock) {
    this.lock = lock;
  }
}
