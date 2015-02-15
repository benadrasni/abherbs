package sk.ab.herbs.fragments;

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

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.ListPlantsActivity;

public class PlantListFragment extends ListFragment {

    static class ViewHolder {
        TextView title;
        TextView family;
        ImageView familyIcon;
        ImageView photo;
    }

    public class PropertyAdapter extends ArrayAdapter<PlantHeader> {

        public PropertyAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            PlantHeader plantHeader = getItem(position);

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.plant_title);
                viewHolder.family = (TextView) convertView.findViewById(R.id.plant_family);
                viewHolder.familyIcon = (ImageView) convertView.findViewById(R.id.family_icon);
                viewHolder.photo = (ImageView) convertView.findViewById(R.id.plant_photo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.photo.setImageResource(android.R.color.transparent);

            if (plantHeader.getUrl() != null) {
                ImageLoader.getInstance().displayImage(plantHeader.getUrl(), viewHolder.photo,
                        ((HerbsApp)getActivity().getApplication()).getOptions());
            }

            viewHolder.title.setText(plantHeader.getTitle());
            viewHolder.family.setText(plantHeader.getFamily());
            ImageLoader.getInstance().displayImage(Constants.STORAGE_ENDPOINT + Constants.FAMILY +
                            Constants.RESOURCE_SEPARATOR + plantHeader.getFamilyId() + Constants.DEFAULT_EXTENSION,
                    viewHolder.familyIcon, ((HerbsApp)getActivity().getApplication()).getOptions());

            return convertView;
        }
    }

    private PropertyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PropertyAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        ListPlantsActivity activity = (ListPlantsActivity) getActivity();
        activity.selectPlant(position);
    }

    public void recreateList(List<PlantHeader> plants) {
        adapter.clear();
        for (PlantHeader plantHeader : plants) {
            adapter.add(plantHeader);
        }
    }
}
