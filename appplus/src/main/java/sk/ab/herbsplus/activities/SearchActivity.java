package sk.ab.herbsplus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;
import java.util.Map;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.commons.NameViewHolder;

/**
 *
 * Created by adrian on 23. 3. 2017.
 */

public class SearchActivity extends SearchBaseActivity {

    private SearchView mSearchView;
    private String mSearchText;

    private DatabaseReference searchInLanguageRef;
    private DatabaseReference searchInLatinRef;
    private RecyclerView namesInLanguage;
    private RecyclerView namesInLatin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSearchText = savedInstanceState.getString(SpecificConstants.STATE_SEARCH_TEXT, "");
        } else {
            mSearchText = "";
        }

        setContentView(R.layout.search_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        searchInLanguageRef = database.getReference(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR + Locale.getDefault().getLanguage());
        namesInLanguage = (RecyclerView) findViewById(R.id.nameInLanguage);
        namesInLanguage.setLayoutManager(new LinearLayoutManager(this));

        searchInLatinRef = database.getReference(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR + AndroidConstants.LANGUAGE_LA);
        namesInLatin = (RecyclerView)findViewById(R.id.namesInLatin);
        namesInLatin.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();



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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) menuItem.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setIconified(false);
        int options = mSearchView.getImeOptions();
        mSearchView.setImeOptions(options| EditorInfo.IME_FLAG_NO_EXTRACT_UI);
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
                if (newText.isEmpty()) {
                    namesInLanguage.swapAdapter(null, true);
                    namesInLatin.swapAdapter(null, true);
                } else {
                    Query queryInLanguage = searchInLanguageRef.orderByKey().startAt(newText).endAt(newText + "\uf8ff");

                    FirebaseRecyclerAdapter<Object, NameViewHolder> mAdapterInLanguage = new FirebaseRecyclerAdapter<Object, NameViewHolder>(Object.class, R.layout.search_row, NameViewHolder.class, queryInLanguage) {
                        @Override
                        public void populateViewHolder(NameViewHolder nameViewHolder, Object names, int position) {
                            final String key = this.getRef(position).getKey();
                            if (!(names instanceof Map)) {
                                Log.w("WRONG SEARCH KEY", key);
                                return;
                            }
                            final int size = ((Map<String, Boolean>) names).size();
                            Map.Entry<String, Boolean> entry = ((Map<String, Boolean>) names).entrySet().iterator().next();
                            final String firstName = entry.getKey();
                            nameViewHolder.getName().setText(key + (size > 1 ? " (" + size + ")" : ""));
                            nameViewHolder.getName().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    if (size == 1) {
                                        callDetailActivity(firstName);
                                    } else {
                                        callListActivity(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR
                                                + Locale.getDefault().getLanguage() + AndroidConstants.FIREBASE_SEPARATOR + key, size);
                                    }
                                }
                            });
                        }
                    };
                    if (namesInLanguage.getAdapter() != null) {
                        ((FirebaseRecyclerAdapter) namesInLanguage.getAdapter()).cleanup();
                    }
                    namesInLanguage.swapAdapter(mAdapterInLanguage, true);

                    Query queryInLatin = searchInLatinRef.orderByKey().startAt(newText).endAt(newText + "\uf8ff");

                    FirebaseRecyclerAdapter<Object, NameViewHolder> mAdapterInLatin = new FirebaseRecyclerAdapter<Object, NameViewHolder>(Object.class, R.layout.search_row, NameViewHolder.class, queryInLatin) {
                        @Override
                        public void populateViewHolder(NameViewHolder nameViewHolder, Object names, int position) {
                            final String key = this.getRef(position).getKey();
                            if (!(names instanceof Map)) {
                                Log.w("WRONG SEARCH KEY", key);
                                return;
                            }
                            final int size = ((Map<String, Boolean>) names).size();
                            Map.Entry<String, Boolean> entry = ((Map<String, Boolean>) names).entrySet().iterator().next();
                            final String firstName = entry.getKey();
                            nameViewHolder.getName().setText(key + (size > 1 ? " (" + size + ")" : ""));
                            nameViewHolder.getName().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    if (size == 1) {
                                        callDetailActivity(firstName);
                                    } else {
                                        callListActivity(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR
                                                + AndroidConstants.LANGUAGE_LA + AndroidConstants.FIREBASE_SEPARATOR + key, size);
                                    }
                                }
                            });
                        }
                    };
                    if (namesInLatin.getAdapter() != null) {
                        ((FirebaseRecyclerAdapter) namesInLatin.getAdapter()).cleanup();
                    }
                    namesInLatin.swapAdapter(mAdapterInLatin, true);
                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (namesInLanguage.getAdapter() != null) {
            ((FirebaseRecyclerAdapter) namesInLanguage.getAdapter()).cleanup();
        }
        if (namesInLatin.getAdapter() != null) {
            ((FirebaseRecyclerAdapter) namesInLatin.getAdapter()).cleanup();
        }
    }
}
