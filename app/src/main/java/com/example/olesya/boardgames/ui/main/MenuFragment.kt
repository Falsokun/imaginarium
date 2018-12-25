package com.example.olesya.boardgames.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.databinding.FragmentMenuBinding
import com.example.olesya.boardgames.ui.main.MenuViewModel.PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION

class MenuFragment: Fragment() {

    private lateinit var mBinding: FragmentMenuBinding
    private lateinit var mViewModel: MenuViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false)
        mViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        initListeners()
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        return mBinding.root
    }

    private fun initListeners() {
        mBinding.search.setOnClickListener(mViewModel.getOnSearchClickListener(activity, this))
        mBinding.start.setOnClickListener(mViewModel.getOnStartClickListener(mBinding.switcher,
                mBinding.playerName, mBinding.playerNum, mBinding.ptsNum))

        mBinding.switcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.statusMsg.text = "Server, waiting for connections"
                mBinding.search.visibility = View.GONE
                mBinding.playerName.visibility = View.GONE
                mBinding.screenContainer.visibility = View.VISIBLE
                mViewModel.discoverPeers()
            } else {
                mBinding.screenContainer.visibility = View.GONE
                mBinding.search.visibility = View.VISIBLE
                mBinding.playerName.visibility = View.VISIBLE
                mBinding.statusMsg.setText(R.string.no_one_connected_to)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mViewModel.requestPeers()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.onPause()
    }
}