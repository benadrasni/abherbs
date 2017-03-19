package sk.ab.herbsbase.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import sk.ab.herbsbase.R;
import sk.ab.herbsbase.fragments.UserPreferenceFragment;

/**
 * User preferences
 */
public class UserPreferenceActivity extends PreferenceActivity {

    private Toolbar mActionBar;

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.settings, new LinearLayout(this), false);

        mActionBar = (Toolbar) contentView.findViewById(R.id.action_bar);
        mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment();

        mActionBar.setTitle(R.string.settings);
    }


    public void updateViews() {

        mActionBar.setTitle(R.string.settings);
    }

    protected void addFragment() {
        getFragmentManager().beginTransaction().replace(R.id.content_wrapper, new UserPreferenceFragment()).commit();
    }
}
