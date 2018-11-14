package com.example.olesya.boardgames.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.example.olesya.boardgames.R

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        openFragment(MainFragment())
    }

    fun openFragment(fragment: Fragment) {
        //may be need to add bundle in future
        val ft = supportFragmentManager.beginTransaction()
        if ((findViewById<View>(R.id.fragment_container) as FrameLayout).childCount == 0) {
            ft.add(R.id.fragment_container, fragment, MenuFragment::class.java.name)
        } else {
            ft.replace(R.id.fragment_container, fragment, MenuFragment::class.java.name)
                    .addToBackStack(MenuFragment::class.java.name)
        }

        ft.commit()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val fm = supportFragmentManager
            if (fm.backStackEntryCount == 1) {
                removeTopFragment(fm)
                if (supportActionBar != null) {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun removeTopFragment(manager: FragmentManager) {
        val topFragment = manager.findFragmentByTag(MenuFragment::class.java.name)
        val trans = manager.beginTransaction()
        trans.remove(topFragment)
        trans.commit()
        manager.popBackStack()
    }
}