package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sk.ab.common.entity.PlantName;
import sk.ab.common.util.Utils;
import sk.ab.herbsbase.activities.SearchBaseActivity;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.tools.SynchronizedCounter;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.commons.NameViewHolder;

/**
 *
 * Created by adrian on 1. 4. 2017.
 */

public class NameSearchActivity extends SearchBaseActivity {
    private final static int API_CALLS = 2;

    private SearchView mSearchView;
    private String mSearchText;

    private List<PlantName> names;
    private List<PlantName> latinNames;
    private RecyclerView namesInLanguage;
    private RecyclerView namesInLatin;

    private class NameAdapter extends RecyclerView.Adapter<NameViewHolder> {
        private final String language;
        private final List<PlantName> names;
        private final ArrayList<PlantName> allNames;

        NameAdapter(String language, List<PlantName> names) {
            this.language = language;
            this.names = names;
            this.allNames = new ArrayList<>();

            this.allNames.addAll(names);
        }

        @Override
        public NameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
            return new NameViewHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(NameViewHolder holder, int position) {
            final PlantName name = names.get(position);

            String item = name.getName() + (name.getCount() > 1 ? " (" + name.getCount() + ")" : "");
            holder.getName().setText(item);
            holder.getName().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if (name.getCount() == 1) {
                        callDetailActivity(name.getPlantName(), false);
                    } else {
                        callListActivity(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.SEPARATOR
                                + language + AndroidConstants.SEPARATOR + name.getName(), name.getCount(), false);
                    }
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        void filter(String constraint) {
            constraint = Utils.removeDiacriticalMarks(constraint.toLowerCase());
            names.clear();
            if (constraint.isEmpty()) {
                names.addAll(allNames);
            } else {
                for (PlantName name : allNames) {
                    if (name.getNameWithoutDiacritics().contains(constraint)) {
                        names.add(name);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSearchText = savedInstanceState.getString(SpecificConstants.STATE_SEARCH_TEXT, "");
        } else {
            mSearchText = "";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        namesInLanguage = (RecyclerView) findViewById(R.id.nameInLanguage);
        namesInLanguage.setLayoutManager(new LinearLayoutManager(this));

        namesInLatin = (RecyclerView)findViewById(R.id.namesInLatin);
        namesInLatin.setLayoutManager(new LinearLayoutManager(this));

        names = new ArrayList<>();
        latinNames = new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mSearchView != null) {
            String searchText = mSearchView.getQuery().toString();
            if (!searchText.isEmpty()) {
                outState.putString(SpecificConstants.STATE_SEARCH_TEXT, searchText);
            }
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setIconified(false);
        int options = mSearchView.getImeOptions();
        mSearchView.setImeOptions(options| EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        EditText searchViewEditText = mSearchView.findViewById(R.id.search_src_text);
        searchViewEditText.setEnabled(false);
        menuItem.expandActionView();

        if (!mSearchText.isEmpty()) {
            mSearchView.post(new Runnable() {
                @Override
                public void run() {
                    mSearchView.setQuery(mSearchText, false); // sets the last search string on the view
                }
            });
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (namesInLanguage.getAdapter() != null) {
                    ((NameAdapter) namesInLanguage.getAdapter()).filter(newText);
                }
                if (namesInLatin.getAdapter() != null) {
                    ((NameAdapter) namesInLatin.getAdapter()).filter(newText);
                }
                return false;
            }
        });

        loadNames();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_taxonomy:
                Intent intent = new Intent(this, TaxonomyActivity.class);
                startActivity(intent);
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
        return R.layout.search_activity;
    };

    private void loadNames() {
        final SynchronizedCounter counter = new SynchronizedCounter();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mFirebaseRef = database.getReference(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.SEPARATOR + Locale.getDefault().getLanguage());
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                    PlantName name = new PlantName();

                    String nameKey = nameSnapshot.getKey();
                    name.setName(nameSnapshot.getKey());
                    name.setNameWithoutDiacritics(Utils.removeDiacriticalMarks(nameKey));
                    name.setCount(Utils.safeLongToInt(nameSnapshot.getChildrenCount()));
                    name.setPlantName(nameSnapshot.getChildren().iterator().next().getKey());
                    names.add(name);
                }

                NameAdapter adapter = new NameAdapter(Locale.getDefault().getLanguage(), names);
                namesInLanguage.setAdapter(adapter);

                counter.increment();

                if (counter.value() == API_CALLS) {
                    enableSearch();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference mFirebaseLatinRef = database.getReference(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_LA);
        mFirebaseLatinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                    PlantName name = new PlantName();

                    String nameKey = nameSnapshot.getKey();
                    name.setName(nameKey);
                    name.setNameWithoutDiacritics(nameKey);
                    name.setCount(Utils.safeLongToInt(nameSnapshot.getChildrenCount()));
                    name.setPlantName(nameSnapshot.getChildren().iterator().next().getKey());
                    latinNames.add(name);
                }

                NameAdapter adapter = new NameAdapter(AndroidConstants.LANGUAGE_LA, latinNames);
                namesInLatin.setAdapter(adapter);

                counter.increment();

                if (counter.value() == API_CALLS) {
                    enableSearch();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableSearch() {
        EditText searchViewEditText = mSearchView.findViewById(R.id.search_src_text);
        searchViewEditText.setEnabled(true);
    }
}
