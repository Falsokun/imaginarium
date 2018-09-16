package com.example.olesya.rxjavatest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        openFragment(new MainFragment());
    }

    public void openFragment(Fragment fragment) {
        //may be need to add bundle in future
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (((FrameLayout) findViewById(R.id.fragment_container)).getChildCount() == 0) {
            ft.add(R.id.fragment_container, fragment, MenuFragment.class.getName());
        } else {
            ft.replace(R.id.fragment_container, fragment, MenuFragment.class.getName())
                    .addToBackStack(MenuFragment.class.getName());
        }

        ft.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() == 1) {
                removeTopFragment(fm);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeTopFragment(FragmentManager manager) {
        Fragment topFragment = manager.findFragmentByTag(MenuFragment.class.getName());
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(topFragment);
        trans.commit();
        manager.popBackStack();
    }
}
