package sk.ab.herbsplus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;
import java.util.Map;

import sk.ab.common.entity.PlantNameList;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.commons.NameViewHolder;

/**
 *
 * Created by adrian on 23. 3. 2017.
 */

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference searchInLanguageRef;
    private DatabaseReference searchInLatinRef;
    private RecyclerView namesInLanguage;
    private RecyclerView namesInLatin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        searchView.setLayoutParams(params);
        menuItem.expandActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query queryInLanguage = searchInLanguageRef.orderByKey().startAt(newText).endAt(newText + "\uf8ff");

                FirebaseRecyclerAdapter<Object, NameViewHolder> mAdapterInLanguage = new FirebaseRecyclerAdapter<Object, NameViewHolder>(Object.class, R.layout.search_row, NameViewHolder.class, queryInLanguage) {
                    @Override
                    public void populateViewHolder(NameViewHolder nameViewHolder, Object names, int position) {
                        final String key = this.getRef(position).getKey();
                        if (!(names instanceof Map)) {
                            Log.w("WRONG SEARCH KEY", key);
                            return;
                        }
                        final int size = ((Map<String, Boolean>)names).size();
                        nameViewHolder.getName().setText(key);
                        nameViewHolder.getName().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callListActivity(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR
                                        + Locale.getDefault().getLanguage() + AndroidConstants.FIREBASE_SEPARATOR + key, size);
                            }
                        });
                    }
                };
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
                        final int size = ((Map<String, Boolean>)names).size();
                        nameViewHolder.getName().setText(key);
                        nameViewHolder.getName().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callListActivity(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR
                                        + AndroidConstants.LANGUAGE_LA + AndroidConstants.FIREBASE_SEPARATOR + key, size);
                            }
                        });
                    }
                };
                namesInLatin.swapAdapter(mAdapterInLatin, true);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void callListActivity(String listPath, int count) {
        Intent intent = new Intent(getBaseContext(), ListPlantsPlusActivity.class);
        intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        intent.putExtra(AndroidConstants.STATE_LIST_PATH, listPath);
        startActivity(intent);
    }
}
