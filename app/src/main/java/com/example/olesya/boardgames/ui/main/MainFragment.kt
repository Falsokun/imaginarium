package com.example.olesya.boardgames.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.olesya.boardgames.R


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_main, container, false)
        val b = v.findViewById<Button>(R.id.g_imaginarium)
        b.setOnClickListener { (activity as MainActivity).openFragment(MenuFragment()) }
        return v
    }
}