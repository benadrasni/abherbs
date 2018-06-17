package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sk.ab.common.Constants;
import sk.ab.herbsplus.HerbsApp;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;

public class MyFilterActivity extends AppCompatActivity {

    private List<String> filters;
    private List<String> avaiableFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_filter_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.my_filter_title);
        }

        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);

        filters = new ArrayList<>();
        for (String filterTag : SpecificConstants.FILTERS) {
            String filter = preferences.getString(filterTag, null);
            if (filter != null) {
                filters.add(filter);
            }
        }
        if (filters.isEmpty()) {
            filters = new ArrayList<>(Arrays.asList(SpecificConstants.FILTER_ATTRIBUTES));
        }

        avaiableFilters = new ArrayList<>();
        for (String filterTag : SpecificConstants.AVAILABLE_FILTERS) {
            String filter = preferences.getString(filterTag, null);
            if (filter != null) {
                avaiableFilters.add(filter);
            }
        }

        displayFilters();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayFilters() {
        // visible filters
        LinearLayout layout = findViewById(R.id.my_filter);
        layout.removeAllViews();
        showFilters(layout);

        // available filters
        LinearLayout layoutAvailable = findViewById(R.id.my_available_filter);
        layoutAvailable.removeAllViews();
        showAvailableFilters(layoutAvailable);
    }

    private void showFilters(final LinearLayout layout) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        int i = 0;
        for (final String filter : filters) {
            final int position = i;
            View inflatedLayout = inflater.inflate(R.layout.filter_row, null, false);
            TextView filterName = inflatedLayout.findViewById(R.id.filter_name);

            ImageView filterVisible = inflatedLayout.findViewById(R.id.btn_filter_visible);
            filterVisible.setEnabled(i > 0);
            filterVisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    avaiableFilters.add(filter);
                    filters.remove(filter);
                    updateFilters();
                    displayFilters();
                }
            });

            ImageView filterUp = inflatedLayout.findViewById(R.id.btn_filter_up);
            filterUp.setEnabled(i > 0);
            if (i > 0) {
                filterUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.swap(filters, position, position-1);
                        updateFilters();
                        displayFilters();
                    }
                });
            }

            ImageView filterDown = inflatedLayout.findViewById(R.id.btn_filter_down);
            filterDown.setEnabled(i < filters.size() - 1 && filters.size() > 1);
            if (i < filters.size() - 1) {
                filterDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Collections.swap(filters, position, position+1);
                        updateFilters();
                        displayFilters();
                    }
                });
            }

            switch (filter) {
                case Constants.COLOR_OF_FLOWERS:
                    filterName.setText(R.string.color_of_flower);
                    break;
                case Constants.HABITAT:
                    filterName.setText(R.string.habitats);
                    break;
                case Constants.NUMBER_OF_PETALS:
                    filterName.setText(R.string.number_of_petals);
                    break;
                case Constants.DISTRIBUTION:
                    filterName.setText(R.string.distribution);
                    break;
            }
            layout.addView(inflatedLayout);
            i++;
        }
    }

    private void showAvailableFilters(final LinearLayout layout) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        for (final String filter : avaiableFilters) {
            View inflatedLayout = inflater.inflate(R.layout.available_filter_row, null, false);
            TextView filterName = inflatedLayout.findViewById(R.id.filter_name);

            ImageView filterVisible = inflatedLayout.findViewById(R.id.btn_filter_visible);
            filterVisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    avaiableFilters.remove(filter);
                    filters.add(filter);
                    updateFilters();
                    displayFilters();
                }
            });

            switch (filter) {
                case Constants.COLOR_OF_FLOWERS:
                    filterName.setText(R.string.color_of_flower);
                    break;
                case Constants.HABITAT:
                    filterName.setText(R.string.habitats);
                    break;
                case Constants.NUMBER_OF_PETALS:
                    filterName.setText(R.string.number_of_petals);
                    break;
                case Constants.DISTRIBUTION:
                    filterName.setText(R.string.distribution);
                    break;
            }
            layout.addView(inflatedLayout);
        }
    }

    private void updateFilters() {
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        for (String filterTag : SpecificConstants.FILTERS) {
            editor.remove(filterTag);
        }
        for (String filterTag : SpecificConstants.AVAILABLE_FILTERS) {
            editor.remove(filterTag);
        }

        int i = 0;
        for (String filter : filters) {
            editor.putString(SpecificConstants.FILTERS[i], filter);
            i++;
        }
        i = 0;
        for (String filter : avaiableFilters) {
            editor.putString(SpecificConstants.AVAILABLE_FILTERS[i], filter);
            i++;
        }

        editor.apply();

        ((HerbsApp)getApplication()).setFilters(filters);
    }
}
