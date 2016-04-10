package sk.ab.herbs.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.ListPlantsActivity;
import sk.ab.tools.Utils;

public class PlantListFragment extends Fragment {
    static final String STATE_POSITION = "list_position";

    private int list_position;

    public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {
        private List<PlantHeader> plantHeaders;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView family;
            ImageView familyIcon;
            ImageView photo;

            public ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.plant_title);
                family = (TextView) v.findViewById(R.id.plant_family);
                familyIcon = (ImageView) v.findViewById(R.id.family_icon);
                photo = (ImageView) v.findViewById(R.id.plant_photo);
            }
        }

        public PropertyAdapter(List<PlantHeader> plantHeaders) {
            this.plantHeaders = plantHeaders;
        }

        @Override
        public PropertyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_row, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public int getItemCount() {
            return plantHeaders.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final PlantHeader plantHeader = plantHeaders.get(position);

            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            holder.photo.setImageResource(android.R.color.transparent);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                holder.photo.getLayoutParams().width = dm.widthPixels;
                holder.photo.getLayoutParams().height = holder.photo.getLayoutParams().width;
            } else {
                holder.photo.getLayoutParams().height = dm.heightPixels - Utils.convertDpToPx(50, dm) - Utils.convertDpToPx(70, dm);
                holder.photo.getLayoutParams().width = holder.photo.getLayoutParams().height;
            }
            ((RelativeLayout.LayoutParams) holder.familyIcon.getLayoutParams())
                    .setMargins(holder.photo.getLayoutParams().width - Utils.convertDpToPx(55, dm),
                            holder.photo.getLayoutParams().height - Utils.convertDpToPx(25, dm), 0, 0);

            if (plantHeader.getUrl() != null) {
                ImageLoader.getInstance().displayImage(plantHeader.getUrl(), holder.photo,
                        ((HerbsApp)getActivity().getApplication()).getOptions());
            }

            holder.title.setText(plantHeader.getTitle());
            holder.family.setText(plantHeader.getFamily());
            ImageLoader.getInstance().displayImage(Constants.STORAGE_ENDPOINT + Constants.FAMILY +
                            Constants.RESOURCE_SEPARATOR + plantHeader.getFamilyId() + Constants.DEFAULT_EXTENSION,
                    holder.familyIcon, ((HerbsApp)getActivity().getApplication()).getOptions());

            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list_position = position;
                    ListPlantsActivity activity = (ListPlantsActivity) getActivity();
                    activity.selectPlant(position);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            list_position = savedInstanceState.getInt(STATE_POSITION);
        }
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() != null) {
            RecyclerView list = (RecyclerView) getView().findViewById(R.id.plant_list);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            } else {
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            }
            layoutManager.scrollToPosition(list_position);
            list.setLayoutManager(layoutManager);

            PropertyAdapter adapter = new PropertyAdapter(((ListPlantsActivity) getActivity()).getPlants());
            list.setAdapter(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (getView() != null) {
            RecyclerView list = (RecyclerView) getView().findViewById(R.id.plant_list);
            int pos = ((LinearLayoutManager) list.getLayoutManager()).findFirstVisibleItemPosition();
            savedInstanceState.putInt(STATE_POSITION, pos);
        }

        super.onSaveInstanceState(savedInstanceState);
    }
}
