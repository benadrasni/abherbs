package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sk.ab.common.Constants;
import sk.ab.common.entity.PlantTaxon;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.SearchBaseActivity;
import sk.ab.herbsbase.tools.Utils;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;

/**
 *
 * Created by adrian on 1. 4. 2017.
 */

public class TaxonomyActivity extends SearchBaseActivity {

    private SearchView mSearchView;

    private List<PlantTaxon> taxons;
    private ListView taxonomyListView;

    private class TaxonAdapter extends BaseAdapter {
        private final LayoutInflater inflater;
        private final List<PlantTaxon> taxons;
        private final ArrayList<PlantTaxon> allTaxons;

        TaxonAdapter(Context context, List<PlantTaxon> taxons) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.taxons = taxons;
            this.allTaxons = new ArrayList<>();
            this.allTaxons.addAll(taxons);
        }

        @Override
        public int getCount() {
            return taxons.size();
        }

        @Override
        public Object getItem(int position) {
            return taxons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PlantTaxon taxon = (PlantTaxon) getItem(position);

            View rowView = inflater.inflate(R.layout.taxon_row, parent, false);

            LinearLayout namesLayout = (LinearLayout)rowView.findViewById(R.id.taxonNames);
            namesLayout.setPadding(taxon.getOffset()*20, 0, 0, 0);
            if (taxon.getCount() > 0) {
                namesLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callProperActivity(taxon);
                    }
                });
            }

            TextView textCount = (TextView) rowView.findViewById(R.id.taxonCount);
            if (taxon.getCount() > 0) {
                textCount.setText("(" + taxon.getCount() + ")");
                textCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callProperActivity(taxon);
                    }
                });
            } else {
                textCount.setVisibility(View.GONE);
            }

            TextView textType = (TextView)rowView.findViewById(R.id.taxonType);
            textType.setText(Utils.getId(AndroidConstants.RES_TAXONOMY_PREFIX + taxon.getType().toLowerCase(), sk.ab.herbsbase.R.string.class));
            textType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callProperActivity(taxon);
                }
            });

            TextView textName = (TextView)rowView.findViewById(R.id.taxonName);
            StringBuilder sbName = new StringBuilder();
            if (taxon.getName() != null) {
                for (String s : taxon.getName()) {
                    if (sbName.length() > 0) {
                        sbName.append(", ");
                    }
                    sbName.append(s);
                }
            }

            TextView textLatinName = (TextView)rowView.findViewById(R.id.taxonLatinName);
            StringBuilder sbLatinName = new StringBuilder();
            if (taxon.getLatinName() != null) {
                for (String s : taxon.getLatinName()) {
                    if (sbLatinName.length() > 0) {
                        sbLatinName.append(", ");
                    }
                    sbLatinName.append(s);
                }
            }

            if (sbName.length() > 0) {
                textName.setText(sbName.toString());
                if (sbLatinName.length() > 0) {
                    textLatinName.setText(sbLatinName.toString());
                } else {
                    textLatinName.setVisibility(View.GONE);
                }
            } else {
                if (sbLatinName.length() > 0) {
                    textName.setText(sbLatinName.toString());
                } else {
                    textName.setVisibility(View.GONE);
                }
                textLatinName.setVisibility(View.GONE);
            }

            if (taxon.getCount() > 0) {
                textName.setTypeface(Typeface.DEFAULT_BOLD);
            }

            return rowView;
        }

        void filter(String constraint) {
            constraint = constraint.toLowerCase();
            taxons.clear();
            if (constraint.isEmpty()) {
                taxons.addAll(allTaxons);
            } else {
                for (PlantTaxon taxon : allTaxons) {
                    if (taxon.getName() != null) {
                        for (String name : taxon.getName()) {
                            if (name.toLowerCase().contains(constraint)) {
                                taxons.add(taxon);
                                break;
                            }
                        }
                    }
                    if (taxon.getLatinName() != null) {
                        for (String latinName : taxon.getLatinName()) {
                            if (latinName.toLowerCase().contains(constraint)) {
                                taxons.add(taxon);
                                break;
                            }
                        }
                    } else {
                        Log.w("TAXONOMY", "Missing latin name for " + taxon.getType());
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        taxonomyListView = (ListView) findViewById(R.id.taxonomy_view);

        taxons = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_taxonomy_search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_taxonomy_search);
        mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setIconified(false);
        int options = mSearchView.getImeOptions();
        mSearchView.setImeOptions(options | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        EditText searchViewEditText = (EditText) mSearchView.findViewById(R.id.search_src_text);
        searchViewEditText.setEnabled(false);
        menuItem.expandActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((TaxonAdapter)taxonomyListView.getAdapter()).filter(newText);
                return false;
            }
        });

        loadTaxonomy();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Class getFilterPlantsActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected Class getListPlantsActivityClass() {
        return ListPlantsPlusActivity.class;
    }

    @Override
    protected Class getDisplayPlantActivityClass() {
        return DisplayPlantPlusActivity.class;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.taxonomy_activity;
    };

    private void loadTaxonomy() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseRef = database.getReference(AndroidConstants.FIREBASE_APG_IV);

        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buildTaxonomy((Map)((Map)dataSnapshot.getValue()).get(AndroidConstants.ROOT_TAXON), 0, AndroidConstants.FIREBASE_APG_IV
                        + AndroidConstants.SEPARATOR + AndroidConstants.ROOT_TAXON);

                TaxonAdapter adapter = new TaxonAdapter(getApplicationContext(), taxons);
                taxonomyListView.setAdapter(adapter);
                EditText searchViewEditText = (EditText) mSearchView.findViewById(R.id.search_src_text);
                searchViewEditText.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildTaxonomy(Map taxonomy, int offset, String path) {
        PlantTaxon taxon = new PlantTaxon();
        taxon.setPath(path + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_APG_LIST);
        taxon.setOffset(offset);
        taxon.setType((String) taxonomy.get(AndroidConstants.FIREBASE_APG_TYPE));
        taxon.setLatinName((List<String>) ((HashMap<String, Object>) taxonomy.get(AndroidConstants.FIREBASE_APG_NAMES)).get(Constants.LANGUAGE_LA));
        taxon.setName((List<String>) ((HashMap<String, Object>)taxonomy.get(AndroidConstants.FIREBASE_APG_NAMES)).get(Locale.getDefault().getLanguage()));
        Long count = (Long)taxonomy.get(AndroidConstants.FIREBASE_APG_COUNT);
        if (count != null && count > 0) {
            taxon.setCount(count.intValue());
            Map<String, Object> plants = (HashMap<String, Object>)taxonomy.get(AndroidConstants.FIREBASE_APG_LIST);
            Map.Entry<String, Object> entry = plants.entrySet().iterator().next();
            taxon.setPlantName(entry.getKey());
        }
        taxons.add(taxon);

        final List<String> keys = new ArrayList<>(taxonomy.keySet());
        for (String key : keys) {
            if (AndroidConstants.FIREBASE_APG_TYPE.equals(key) || AndroidConstants.FIREBASE_APG_NAMES.equals(key)
                    || AndroidConstants.FIREBASE_APG_LIST.equals(key) || AndroidConstants.FIREBASE_APG_COUNT.equals(key)) {
                continue;
            }
            buildTaxonomy((Map)taxonomy.get(key), offset + 1, path + AndroidConstants.SEPARATOR + key);
        }
    }

    private void callProperActivity(PlantTaxon taxon) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (taxon.getCount() == 1) {
            callDetailActivity(taxon.getPlantName(), false);
        } else {
            callListActivity(taxon.getPath(), taxon.getCount(), false);
        }
    }
}
