package sk.ab.herbsbase.commons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.BaseActivity;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;
import sk.ab.herbsbase.activities.LegendActivity;
import sk.ab.herbsbase.activities.UserPreferenceActivity;

public class PropertyListFragment extends ListFragment {
    private PropertyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.property_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PropertyAdapter(getActivity());

        for (BaseFilterFragment fragment : ((BaseApp)getActivity().getApplication()).getFilterAttributes()) {
            adapter.add(fragment);
        }

        adapter.add(new PropertyDivider());

        adapter.add(new Setting(R.string.legend, AndroidConstants.ITEM_LEGEND));
        adapter.add(new Setting(R.string.settings, AndroidConstants.ITEM_SETTINGS));
        adapter.add(new Setting(R.string.feedback, AndroidConstants.ITEM_FEEDBACK));
        adapter.add(new Setting(R.string.help, AndroidConstants.ITEM_HELP));
        adapter.add(new Setting(R.string.about, AndroidConstants.ITEM_ABOUT));

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        PropertyItem item = adapter.getItem(position);
        if (item != null) {
            switch (item.getType()) {
                case PropertyItem.TYPE_FILTER:
                    if (getActivity() instanceof FilterPlantsBaseActivity) {
                        FilterPlantsBaseActivity activity = (FilterPlantsBaseActivity) getActivity();
                        BaseFilterFragment fragment = (BaseFilterFragment) item;
                        if (!activity.getCurrentFragment().equals(fragment)) {
                            activity.switchContent(fragment);
                            activity.removeFromFilter(fragment.getAttribute());
                        }
                    } else {
                        ((BaseActivity) getActivity()).startLoading();
                        Intent intent = new Intent(getActivity(), FilterPlantsBaseActivity.class);
                        intent.putExtra(AndroidConstants.STATE_FILTER_POSITION, ""
                                + ((BaseApp) getActivity().getApplication()).getFilterAttributes()
                                .indexOf(item));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    break;
                case PropertyItem.TYPE_SETTING:
                    BaseSetting setting = (BaseSetting) item;
                    switch (setting.getName()) {
                        case AndroidConstants.ITEM_LEGEND:
                            Intent intent = new Intent(getContext(), LegendActivity.class);
                            startActivity(intent);
                            break;
                        case AndroidConstants.ITEM_SETTINGS:
                            intent = new Intent(getContext(), getUserPreferenceActivityClass());
                            startActivity(intent);
                            break;
                        case AndroidConstants.ITEM_FEEDBACK:
                        case AndroidConstants.ITEM_HELP:
                        case AndroidConstants.ITEM_ABOUT:
                            intent = new Intent(getContext(), HtmlActivity.class);
                            intent.putExtra("title", setting.getName());
                            startActivity(intent);
                            break;
                    }
                    break;
            }
        }
        ((BaseActivity)getActivity()).closeDrawer();
    }

    public class PropertyAdapter extends ArrayAdapter<PropertyItem> {

        PropertyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
            PropertyItem item = getItem(position);
            if (item != null) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(item.getPropertyLayout(), null);
                }
                switch (item.getType()) {
                    case PropertyItem.TYPE_FILTER:
                        BaseFilterFragment filterFragment = (BaseFilterFragment) item;
                        ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
                        if (icon != null) {
                            icon.setImageResource(filterFragment.getIconRes());
                        }
                        TextView title = (TextView) convertView.findViewById(R.id.row_title);
                        if (title != null) {
                            title.setText(filterFragment.getTitle());
                        }
                        TextView value = (TextView) convertView.findViewById(R.id.row_value);
                        if (value != null) {
                            String valueId = ((BaseActivity) getActivity()).getFilter().get(filterFragment.getAttribute());
                            if (valueId != null) {
                                int resId = AndroidConstants.getValueResource(getResources(), valueId);
                                if (resId > 0) {
                                    value.setText(getResources().getText(resId));
                                }
                            } else {
                                value.setText("");
                            }
                        }
                        break;
                    case PropertyItem.TYPE_DIVIDER:
                        break;
                    case PropertyItem.TYPE_SETTING:
                        BaseSetting setting = (BaseSetting) item;
                        title = (TextView) convertView.findViewById(R.id.row_title);
                        if (title != null) {
                            title.setText(setting.getTitle());
                        }
                        break;
                }
            }
            return convertView;
        }
    }

    protected Class getUserPreferenceActivityClass() {
        return UserPreferenceActivity.class;
    }
}
