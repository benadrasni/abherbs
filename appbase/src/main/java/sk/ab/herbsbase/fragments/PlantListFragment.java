package sk.ab.herbsbase.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;
import sk.ab.common.Constants;
import sk.ab.common.entity.Rate;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.ListPlantsActivity;
import sk.ab.herbsbase.entity.PlantHeaderParcel;
import sk.ab.herbsbase.tools.Utils;

public class PlantListFragment extends Fragment {
    static final String STATE_POSITION = "list_position";

    private int list_position;

    public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {
        private List<PlantHeaderParcel> plantHeaders;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView family;
            ImageView familyIcon;
            ImageView photo;
            Button never;
            Button later;
            Button rate;

            public ViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.plant_title);
                family = (TextView) v.findViewById(R.id.plant_family);
                familyIcon = (ImageView) v.findViewById(R.id.family_icon);
                photo = (ImageView) v.findViewById(R.id.plant_photo);

                never = (Button) v.findViewById(R.id.btnNever);
                later = (Button) v.findViewById(R.id.btnLater);
                rate = (Button) v.findViewById(R.id.btnRate);
            }
        }

        public PropertyAdapter(List<PlantHeaderParcel> plantHeaders) {
            this.plantHeaders = plantHeaders;
        }

        @Override
        public int getItemViewType(int position) {
            return plantHeaders.get(position).getId() == null ? 0 : 1;
        }

        @Override
        public PropertyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0: return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_row, parent, false));
                default: return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_row, parent, false));
            }
        }

        @Override
        public int getItemCount() {
            return plantHeaders.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (holder.getItemViewType() > 0) {
                final PlantHeaderParcel plantHeader = plantHeaders.get(position);

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
                            ((BaseApp) getActivity().getApplication()).getOptions());
                }

                holder.title.setText(getName(plantHeader.getLabel()));
                holder.family.setText(getName(plantHeader.getFamily()));
                ImageLoader.getInstance().displayImage(AndroidConstants.STORAGE_ENDPOINT + plantHeader.getFamily().get(Constants.LANGUAGE_LA)
                        + AndroidConstants.DEFAULT_EXTENSION, holder.familyIcon, ((BaseApp) getActivity().getApplication()).getOptions());

                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list_position = holder.getAdapterPosition();
                        ((ListPlantsActivity)getActivity()).selectPlant(list_position);
                    }
                });
            } else {
                holder.rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = getActivity().getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_DONE);
                        editor.apply();

                        saveRate(AndroidConstants.RATE_STATUS_DONE);

                        Uri uri = Uri.parse("market://details?id=" + getActivity().getBaseContext().getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getBaseContext().getPackageName())));
                        }

                        plantHeaders.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });

                holder.later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = getActivity().getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NO);
                        editor.putInt(AndroidConstants.RATE_COUNT_KEY, AndroidConstants.RATE_COUNTER);
                        editor.apply();

                        saveRate(AndroidConstants.RATE_STATUS_LATER);

                        plantHeaders.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });

                holder.never.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences preferences = getActivity().getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NEVER);
                        editor.apply();

                        saveRate(AndroidConstants.RATE_STATUS_NEVER);

                        plantHeaders.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });
            }
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

            PropertyAdapter adapter = new PropertyAdapter(((ListPlantsActivity)getActivity()).getPlantList());
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

    private void saveRate(String status) {
        Rate rate = new Rate(status, Locale.getDefault().getCountry());

        ((BaseApp)getActivity().getApplication()).getHerbCloudClient().getApiService().saveRate(rate)
                .enqueue(new Callback<Rate>() {
                    @Override
                    public void onResponse(Call<Rate> call, Response<Rate> response) {
                        Log.i(this.getClass().getName(), "Rate with status '" +
                                response.body().getStatus() + "' was saved to the datastore");
                    }

                    @Override
                    public void onFailure(Call<Rate> call, Throwable t) {
                        Log.e(this.getClass().getName(), "Failed to save rate action. Check your internet settings.", t);
                    }
                });

    }

    private String getName(HashMap<String, String> names) {
        String language = Locale.getDefault().getLanguage();

        String name = names.get(language);
        if (name == null) {
            name = names.get(Constants.LANGUAGE_LA);
        }

        return name;
    }
}
