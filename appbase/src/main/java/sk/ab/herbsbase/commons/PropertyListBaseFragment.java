package sk.ab.herbsbase.commons;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.Locale;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.BaseActivity;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;
import sk.ab.herbsbase.activities.LegendActivity;

public abstract class PropertyListBaseFragment extends ListFragment {
    protected PropertyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.property_list, container, false);
    }

    @Override
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
    public void onResume() {
        super.onResume();

        manageUserSettings();
    }


    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        PropertyItem item = adapter.getItem(position);
        if (getActivity() != null && !getActivity().isDestroyed() && item != null) {
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
                        BaseActivity baseActivity = ((BaseActivity) getActivity());
                        baseActivity.startLoading();
                        Intent intent = new Intent(getActivity(), getFilterPlantActivityClass());
                        intent.putExtra(AndroidConstants.STATE_FILTER_POSITION, "" + baseActivity.getApp().getFilterAttributes().indexOf(item));
                        intent.putExtra(AndroidConstants.STATE_FILTER, baseActivity.getFilter());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    break;
                case PropertyItem.TYPE_SETTING:
                    BaseSetting setting = (BaseSetting) item;
                    switch (setting.getName()) {
                        case AndroidConstants.ITEM_LEGEND:
                            Intent intent = new Intent(getActivity(), LegendActivity.class);
                            startActivity(intent);
                            break;
                        case AndroidConstants.ITEM_SETTINGS:
                            intent = new Intent(getActivity(), getUserPreferenceActivityClass());
                            startActivity(intent);
                            break;
                        case AndroidConstants.ITEM_FEEDBACK:
                            intent = new Intent(getActivity(), getFeedbackActivityClass());
                            startActivity(intent);
                            break;
                        case AndroidConstants.ITEM_HELP:
                            Intent helpIntent = new Intent("android.intent.action.VIEW", Uri.parse(AndroidConstants.WEB_URL
                                    + "help?lang=" + Locale.getDefault().getLanguage()));
                            startActivity(helpIntent);
                            break;
                        case AndroidConstants.ITEM_ABOUT:
                            Intent aboutIntent = new Intent("android.intent.action.VIEW", Uri.parse(AndroidConstants.WEB_URL
                                    + "about?lang=" + Locale.getDefault().getLanguage()));
                            startActivity(aboutIntent);
                            break;
                        default:
                            handleUserSettings(setting);
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
                convertView = LayoutInflater.from(getContext()).inflate(item.getPropertyLayout(), null);
                switch (item.getType()) {
                    case PropertyItem.TYPE_FILTER:
                        BaseFilterFragment filterFragment = (BaseFilterFragment) item;
                        ImageView icon = convertView.findViewById(R.id.row_icon);
                        if (icon != null) {
                            icon.setImageResource(filterFragment.getIconRes());
                        }
                        TextView title = convertView.findViewById(R.id.row_title);
                        if (title != null) {
                            title.setText(filterFragment.getTitle());
                        }
                        TextView value = convertView.findViewById(R.id.row_value);
                        if (value != null) {
                            String valueText = "";
                            BaseActivity activity = (BaseActivity) getActivity();
                            if ((activity != null && !activity.isDestroyed()) && activity.getFilter() != null){
                                String valueId = activity.getFilter().get(filterFragment.getAttribute());
                                if (valueId != null) {
                                    int resId = AndroidConstants.filterResources.get(filterFragment.getAttribute()+"_"+valueId);
                                    if (resId > 0) {
                                        valueText = getResources().getText(resId).toString();
                                    }
                                }
                            }

                            value.setText(valueText);
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

    protected abstract Class getUserPreferenceActivityClass();

    protected abstract Class getFeedbackActivityClass();

    protected abstract Class getFilterPlantActivityClass();

    protected void handleUserSettings(BaseSetting setting) {
    };

    public void manageUserSettings() {
    };
}
