package com.example.olesya.boardgames.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.Utils
import com.example.olesya.boardgames.adapter.CardDiffUtil
import com.example.olesya.boardgames.adapter.CardPagerAdapter
import com.example.olesya.boardgames.adapter.PlayerAdapter
import com.example.olesya.boardgames.connection.common.ServiceHolderActivity
import com.example.olesya.boardgames.connection.server.Server
import com.example.olesya.boardgames.databinding.ActivityScreenImaginariumBinding
import com.example.olesya.boardgames.entity.Card
import com.example.olesya.boardgames.ui.viewmodel.ScreenViewModel
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ScreenActivity : ServiceHolderActivity() {

    private lateinit var mBinding: ActivityScreenImaginariumBinding
    private lateinit var viewModel: ScreenViewModel
    private var cardPagerAdapter: CardPagerAdapter = CardPagerAdapter()
    private lateinit var playerStatusAdapter: PlayerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_screen_imaginarium)
        viewModel = ViewModelProviders.of(this).get(ScreenViewModel::class.java)
        playerStatusAdapter = PlayerAdapter()
        initPlayerStatus()
        initCardPager()
        startServerService()

        viewModel.message.observe(this, Observer {
            if (it != null) {
                Utils.showAlert(this, it)
            }
        })

        mBinding.buttonSend.setOnClickListener { serverMessage.postValue("anything") }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mServiceIntent)
    }

    override fun setCallbacks() {
        viewModel.controller = (mService as Server).gameController
        viewModel.controller.lcOwner = this
        viewModel.controller.screenMessage.observe(this, Observer { value ->
                Utils.showSnackbar(mBinding.buttonSend, value!!)
        })

        viewModel.controller.screenCards.observe(this) {
            val diffResult = DiffUtil.calculateDiff(CardDiffUtil(cardPagerAdapter.dataset,
                    it.map { it as Card }.toMutableList()))
            cardPagerAdapter.setData(it)
            diffResult.dispatchUpdatesTo(cardPagerAdapter)
            cardPagerAdapter.notifyDataSetChanged()
        }

        viewModel.controller.players.observe(this) {
            playerStatusAdapter.players = it
            playerStatusAdapter.notifyDataSetChanged()
            if (it.size == viewModel.controller.totalPlayers) {
                viewModel.controller.startGame()
            }
        }
    }

    private fun startServerService() {
        mServiceIntent = Intent(this, Server::class.java)
        if (intent.extras != null) {
            val playerNum = intent.extras.getInt(Commands.CLIENT_NUM)
            val win = intent.extras.getInt(Commands.WIN_PTS)
            mServiceIntent.putExtra(Commands.CLIENT_NUM, playerNum)
            mServiceIntent.putExtra(Commands.WIN_PTS, win)
        }

        startService(mServiceIntent)
    }

    private fun initCardPager() {
        mBinding.cardRv.setHasFixedSize(true)
        mBinding.cardRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.cardRv.itemAnimator = SlideInUpAnimator()
        mBinding.cardRv.itemAnimator.addDuration = 200
        mBinding.cardRv.adapter = cardPagerAdapter
    }

    private fun initPlayerStatus() {
        mBinding.playersStatusRv.adapter = playerStatusAdapter
        mBinding.playersStatusRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}