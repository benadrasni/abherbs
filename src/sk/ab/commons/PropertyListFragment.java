package sk.ab.commons;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import sk.ab.herbs.R;

public class PropertyListFragment extends ListFragment {
    private PropertyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_menu_list, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PropertyAdapter(getActivity());

        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            for (BaseFilterFragment fragment : activity.getFilterAttributes()) {
                adapter.add(fragment);
            }
        }

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        switchFragment(position, adapter.getItem(position));
    }

    private void switchFragment(int position, BaseFilterFragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof BaseActivity) {
            BaseActivity ba = (BaseActivity) getActivity();
            ba.switchContent(position, fragment);
        }
    }

    public class PropertyAdapter extends ArrayAdapter<BaseFilterFragment> {

        public PropertyAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.filter_menu_row, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).getIconRes());
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(getItem(position).getTitle());
            ImageView lock = (ImageView) convertView.findViewById(R.id.row_check);
            lock.setVisibility(getItem(position).isLock() ? View.VISIBLE : View.INVISIBLE);

            return convertView;
        }

    }
}
