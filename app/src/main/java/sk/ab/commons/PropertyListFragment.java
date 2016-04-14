package sk.ab.commons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.FilterPlantsActivity;

public class PropertyListFragment extends ListFragment {
    private PropertyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.property_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PropertyAdapter(getActivity());

        for (BaseFilterFragment fragment : ((HerbsApp)getActivity().getApplication()).getFilterAttributes()) {
            adapter.add(fragment);
        }

        adapter.add(new PropertyDivider());

        adapter.add(new Setting(R.string.legend));
        adapter.add(new Setting(R.string.settings));
        adapter.add(new Setting(R.string.feedback));
        adapter.add(new Setting(R.string.help));
        adapter.add(new Setting(R.string.about));

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        PropertyItem item = adapter.getItem(position);
        switch (item.getType()) {
            case PropertyItem.TYPE_FILTER:
                if (getActivity() instanceof FilterPlantsActivity) {
                    FilterPlantsActivity activity = (FilterPlantsActivity) getActivity();
                    BaseFilterFragment fragment = (BaseFilterFragment) item;
                    if (!activity.getCurrentFragment().equals(fragment)) {
                        activity.switchContent(fragment);
                        activity.removeFromFilter(fragment.getAttributeId());
                    }
                } else {
                    ((BaseActivity)getActivity()).loading();
                    Intent intent = new Intent(getActivity(), FilterPlantsActivity.class);
                    intent.putExtra("position", ""+((HerbsApp)getActivity().getApplication()).getFilterAttributes()
                            .indexOf(item));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                break;
            case PropertyItem.TYPE_SETTING:
                BaseSetting setting = (BaseSetting)item;
                switch (setting.getTitle()) {
                    case R.string.legend:
                        Intent intent = new Intent();
                        intent.putExtra("title", setting.getTitle());
                        intent.setClassName(getActivity(), "sk.ab.herbs.activities.LegendActivity");
                        startActivity(intent);
                        break;
                    case R.string.settings:
                        intent = new Intent();
                        intent.setClassName(getActivity(), "sk.ab.herbs.activities.UserPreferenceActivity");
                        startActivity(intent);
                        break;
                    case R.string.feedback:
                    case R.string.help:
                    case R.string.about:
                        intent = new Intent();
                        intent.putExtra("title", setting.getTitle());
                        intent.setClassName(getActivity(), "sk.ab.commons.HtmlActivity");
                        startActivity(intent);
                        break;
                }
                break;
        }
        ((BaseActivity)getActivity()).closeDrawer();
    }

    public class PropertyAdapter extends ArrayAdapter<PropertyItem> {

        public PropertyAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            PropertyItem item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(item.getPropertyLayout(), null);
            }
            switch (item.getType()) {
                case PropertyItem.TYPE_FILTER:
                    BaseFilterFragment filterFragment = (BaseFilterFragment)item;
                    ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
                    icon.setImageResource(filterFragment.getIconRes());
                    TextView title = (TextView) convertView.findViewById(R.id.row_title);
                    title.setText(filterFragment.getTitle());
                    TextView value = (TextView) convertView.findViewById(R.id.row_value);
                    Integer valueId = ((HerbsApp)getActivity().getApplication()).getFilter().get(filterFragment.getAttributeId());
                    if (valueId != null) {
                        int resId = Constants.getValueResource(getResources(), valueId);
                        if (resId > 0) {
                            value.setText(getResources().getText(resId));
                        }
                    } else {
                        value.setText("");
                    }
                    break;
                case PropertyItem.TYPE_DIVIDER:
                    break;
                case PropertyItem.TYPE_SETTING:
                    BaseSetting setting = (BaseSetting)item;
                    title = (TextView) convertView.findViewById(R.id.row_title);
                    title.setText(setting.getTitle());
                    break;
            }

            return convertView;
        }

    }
}
