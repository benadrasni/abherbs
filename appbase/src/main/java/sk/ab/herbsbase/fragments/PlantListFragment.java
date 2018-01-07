package sk.ab.herbsbase.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Map;

import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.commons.PlantViewHolder;
import sk.ab.herbsbase.tools.Utils;

public class PlantListFragment extends Fragment {
    private long mLastClickTime;

    private int listPosition;
    private PropertyAdapter adapter;

    private class PropertyAdapter extends FirebaseIndexRecyclerAdapter<FirebasePlant, PlantViewHolder> {

        PropertyAdapter(Class<FirebasePlant> modelClass, @LayoutRes int modelLayout, Class<PlantViewHolder> viewHolderClass, Query keyRef, Query dataRef) {
            super(modelClass, modelLayout, viewHolderClass, keyRef, dataRef);
        }

        @Override
        protected void populateViewHolder(final PlantViewHolder holder, final FirebasePlant plant, int position) {
            DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
            holder.getPhoto().setImageResource(android.R.color.transparent);
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int size = dm.widthPixels;
                holder.getPhoto().getLayoutParams().width = size;
                holder.getPhoto().getLayoutParams().height = size;
            } else {
                int size = dm.heightPixels - Utils.convertDpToPx(50, dm) - Utils.convertDpToPx(70, dm);
                holder.getPhoto().getLayoutParams().height = size;
                holder.getPhoto().getLayoutParams().width = size;
            }
            ((RelativeLayout.LayoutParams) holder.getFamilyIcon().getLayoutParams())
                    .setMargins(holder.getPhoto().getLayoutParams().width - Utils.convertDpToPx(55, dm),
                            holder.getPhoto().getLayoutParams().height - Utils.convertDpToPx(25, dm), 0, 0);

            if (plant.getPhotoUrls().get(0) != null) {
                Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_PHOTOS + plant.getPhotoUrls().get(0),
                        holder.getPhoto(), ((BaseApp) getActivity().getApplication()).getOptions());
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mTranslationRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.FIREBASE_SEPARATOR
                    + Locale.getDefault().getLanguage() + AndroidConstants.FIREBASE_SEPARATOR + plant.getName());
            mTranslationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        PlantTranslation plantTranslation = dataSnapshot.getValue(PlantTranslation.class);

                        if (plantTranslation != null && plantTranslation.getLabel() != null) {
                            holder.getTitle().setText(plantTranslation.getLabel());
                        } else {
                            holder.getTitle().setText(plant.getName());
                        }
                    } catch (DatabaseException ex) {
                        Crashlytics.log("Translation (" + Locale.getDefault().getLanguage() + "): " + plant.getName());
                        Crashlytics.log(ex.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(this.getClass().getName(), databaseError.getMessage());
                }
            });

            for(Map.Entry<String, String> entry : plant.getTaxonomy().entrySet()) {
                if (entry.getKey().endsWith(Constants.TAXONOMY_FAMILY)) {
                    String family = entry.getValue();
                    holder.getFamily().setText(family);
                    Utils.displayImage(getActivity().getApplicationContext().getFilesDir(), AndroidConstants.STORAGE_FAMILIES + family
                            + AndroidConstants.DEFAULT_EXTENSION, holder.getFamilyIcon(), ((BaseApp) getActivity().getApplication()).getOptions());
                    break;
                }
            }

            holder.getPhoto().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;
                    if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                        listPosition = holder.getAdapterPosition();
                        ((ListPlantsBaseActivity) getActivity()).selectPlant(plant.getName());
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, null);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.plant_list);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference plantsRef = database.getReference(AndroidConstants.FIREBASE_PLANTS);
        DatabaseReference listRef = database.getReference(((ListPlantsBaseActivity)getActivity()).getListPath());

        adapter = new PropertyAdapter(FirebasePlant.class, R.layout.plant_row, PlantViewHolder.class, listRef, plantsRef);
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() != null) {
            RecyclerView list = (RecyclerView) getView().findViewById(R.id.plant_list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());

            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            } else {
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            }
            list.setLayoutManager(linearLayoutManager);
            linearLayoutManager.scrollToPosition(listPosition);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
