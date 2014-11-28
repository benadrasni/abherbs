package sk.ab.commons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import sk.ab.herbs.R;

public class PropertyListFragment extends ListFragment {
    private PropertyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.property_list, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PropertyAdapter(getActivity());

        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            for (BaseFilterFragment fragment : activity.getFilterAttributes()) {
                adapter.add(fragment);
            }

            adapter.add(new PropertyDivider());

            adapter.add(new Setting(R.string.settings));
            adapter.add(new Setting(R.string.help));
            adapter.add(new Setting(R.string.about));
        }

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        PropertyItem item = adapter.getItem(position);
        switch (item.getType()) {
            case PropertyItem.TYPE_FILTER:
                BaseFilterFragment filterFragment = (BaseFilterFragment)item;
                switchFragment(position, filterFragment);
                break;
            case PropertyItem.TYPE_SETTING:
                BaseSetting setting = (BaseSetting)item;
                switch (setting.getTitle()) {
                    case R.string.settings:
                        Intent intent = new Intent();
                        intent.setClassName(getActivity(), "sk.ab.herbs.activities.UserPreferenceActivity");
                        startActivity(intent);
                        break;
                    case R.string.about:
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.about)
                                .setMessage(Html.fromHtml(getString(R.string.about_msg)))
                                .show();
                        break;
                }
                break;
        }

    }

    private void switchFragment(int position, BaseFilterFragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof BaseActivity) {
            BaseActivity ba = (BaseActivity) getActivity();
            ba.switchContent(position, fragment);
        }
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
                    ImageView lock = (ImageView) convertView.findViewById(R.id.row_check);
                    lock.setVisibility(filterFragment.isLock() ? View.VISIBLE : View.INVISIBLE);

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
