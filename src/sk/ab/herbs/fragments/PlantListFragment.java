package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import sk.ab.herbs.Constants;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.ListPlantsActivity;
import sk.ab.tools.DrawableManager;
import sk.ab.tools.GetResource;

import java.util.List;

public class PlantListFragment extends ListFragment {

  public class PropertyAdapter extends ArrayAdapter<PlantHeader> {

    public PropertyAdapter(Context context) {
      super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.plant_row, null);
      }

      ImageView photo = (ImageView) convertView.findViewById(R.id.plant_photo);
      photo.setImageResource(android.R.color.transparent);
      if (getItem(position).getUrl() != null) {
        drawableManager.fetchDrawableOnThread(getItem(position).getUrl(), photo);
      }

      TextView title = (TextView) convertView.findViewById(R.id.plant_title);
      title.setText(getItem(position).getTitle());

      TextView family = (TextView) convertView.findViewById(R.id.plant_family);
      family.setText(getItem(position).getFamily());

      ImageView family_icon = (ImageView) convertView.findViewById(R.id.family_icon);
      family_icon.setImageResource(GetResource.getResourceDrawable(Constants.FAMILY +
          Constants.RESOURCE_SEPARATOR + getItem(position).getFamilyId(), getActivity().getBaseContext(),
          R.drawable.home));

      return convertView;
    }
  }

  private PropertyAdapter adapter;
  private DrawableManager drawableManager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    drawableManager = new DrawableManager(getResources());
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new PropertyAdapter(getActivity());
  	setListAdapter(adapter);
	}

  @Override
  public void onListItemClick(ListView lv, View v, int position, long id) {
    Intent intent = new Intent(getActivity().getBaseContext(), DisplayPlantActivity.class);
    intent.putExtra("plantHeader", ((ListPlantsActivity)getActivity()).getPlants().get(position));
    startActivity(intent);
  }

  public void recreateList(List<PlantHeader> plants) {
    adapter.clear();

    for(PlantHeader plantHeader : plants) {
      adapter.add(plantHeader);
    }
  }
}
