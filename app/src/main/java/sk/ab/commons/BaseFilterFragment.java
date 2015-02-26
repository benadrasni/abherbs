package sk.ab.commons;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.ab.herbs.R;
import sk.ab.herbs.activities.FilterPlantsActivity;

public abstract class BaseFilterFragment extends Fragment implements PropertyItem {
    protected int attributeId;
    protected int title;
    protected int iconRes;
    protected int layout;
    protected int propertyLayout;

    public BaseFilterFragment() {
        this.propertyLayout = R.layout.property_filter;
    }

    @Override
    public int getPropertyLayout() {
        return propertyLayout;
    }

    @Override
    public int getType() {
        return TYPE_FILTER;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(title);
        ActionBarActivity activity = (ActionBarActivity) getActivity();
        activity.getSupportActionBar().setIcon(iconRes);

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

    public int getLayout() {
        return layout;
    }
}
