package sk.ab.herbs.commons;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.ab.herbs.R;

public abstract class BaseSettingFragment extends Fragment implements PropertyItem {

    protected int title;
    protected int layout;
    protected int propertyLayout;

    public BaseSettingFragment() {
        this.propertyLayout = R.layout.property_filter;
    }

    @Override
    public int getPropertyLayout() {
        return propertyLayout;
    }

    @Override
    public int getType() {
        return TYPE_SETTING;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layout, null);
    }

    public int getTitle() {
        return title;
    }

    public int getLayout() {
        return layout;
    }
}
