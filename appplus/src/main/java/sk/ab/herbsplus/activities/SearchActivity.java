package sk.ab.herbsplus.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;

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

                FirebaseRecyclerAdapter<PlantNameList, NameViewHolder> mAdapterInLanguage = new FirebaseRecyclerAdapter<PlantNameList, NameViewHolder>(PlantNameList.class, R.layout.search_row, NameViewHolder.class, queryInLanguage) {
                    @Override
                    public void populateViewHolder(NameViewHolder nameViewHolder, PlantNameList names, int position) {
                        String key = this.getRef(position).getKey();
                        nameViewHolder.getName().setText(key);
                    }
                };
                namesInLanguage.swapAdapter(mAdapterInLanguage, true);

                Query queryInLatin = searchInLatinRef.orderByKey().startAt(newText).endAt(newText + "\uf8ff");

                FirebaseRecyclerAdapter<PlantNameList, NameViewHolder> mAdapterInLatin = new FirebaseRecyclerAdapter<PlantNameList, NameViewHolder>(PlantNameList.class, R.layout.search_row, NameViewHolder.class, queryInLatin) {
                    @Override
                    public void populateViewHolder(NameViewHolder nameViewHolder, PlantNameList names, int position) {
                        String key = this.getRef(position).getKey();
                        nameViewHolder.getName().setText(key);
                    }
                };
                namesInLatin.swapAdapter(mAdapterInLatin, true);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
