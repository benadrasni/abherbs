package sk.ab.herbsplus.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    private RecyclerView namesInLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        RecyclerView namesInLatin = (RecyclerView)findViewById(R.id.namesInLatin);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        searchInLanguageRef = database.getReference(SpecificConstants.FIREBASE_SEARCH + AndroidConstants.FIREBASE_SEPARATOR + "sk");

        namesInLanguage = (RecyclerView) findViewById(R.id.nameInLanguage);
        namesInLanguage.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<PlantNameList, NameViewHolder> mAdapter = new FirebaseRecyclerAdapter<PlantNameList, NameViewHolder>(PlantNameList.class, R.layout.search_row, NameViewHolder.class, searchInLanguageRef) {
            @Override
            public void populateViewHolder(NameViewHolder nameViewHolder, PlantNameList names, int position) {
                String key = this.getRef(position).getKey();
                nameViewHolder.getName().setText(key);
            }
        };
        namesInLanguage.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = searchInLanguageRef.orderByKey().startAt(newText).endAt(newText + "\uf8ff");

                FirebaseRecyclerAdapter<PlantNameList, NameViewHolder> mAdapter = new FirebaseRecyclerAdapter<PlantNameList, NameViewHolder>(PlantNameList.class, R.layout.search_row, NameViewHolder.class, query) {
                    @Override
                    public void populateViewHolder(NameViewHolder nameViewHolder, PlantNameList names, int position) {
                        String key = this.getRef(position).getKey();
                        nameViewHolder.getName().setText(key);
                    }
                };
                namesInLanguage.swapAdapter(mAdapter, true);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
