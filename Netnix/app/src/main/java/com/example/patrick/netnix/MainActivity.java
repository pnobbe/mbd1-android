package com.example.patrick.netnix;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_CONTENT = "content";


    private Fragment mContent;
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shows:
                    setContent(new ShowListFragment());
                    return true;
                case R.id.navigation_schedule:
                    setContent(new ScheduleFragment());
                    return true;
            }
            return false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        mContent = fm.findFragmentByTag("content");
        Log.d("CONTENT2", mContent+"");

        setContentView(R.layout.activity_main);

        if (mContent == null) {
            setContent(new ShowListFragment());
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Declare our toolbar and apply it to our layout
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        handleIntent(getIntent());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        if (mContent != null) {
            if (mContent.isAdded()) {
                getSupportFragmentManager().putFragment(outState, TAG_CONTENT, mContent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle b = new Bundle();
            b.putString("query", query);
            setContent(new ShowListFragment(),b);
        }

        if (mContent != null) {
            if (mContent instanceof ShowListFragment) {
                navigation.getMenu().getItem(0).setChecked(true);
            }
            if (mContent instanceof ScheduleFragment) {
                navigation.getMenu().getItem(1).setChecked(true);

            }
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tools, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public void setContent(Fragment content) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.content, content, TAG_CONTENT).commit();
        this.mContent = content;
    }

    public void setContent(Fragment content, Bundle arguments) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Set arguments
        content.setArguments(arguments);

        ft.replace(R.id.content, content, TAG_CONTENT).commit();
        this.mContent = content;
    }

}
