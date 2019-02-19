package com.example.olesya.boardgames.ui.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import androidx.recyclerview.widget.RecyclerView
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
        initPlayerStatus()
        initCardPager()

        startServerService()
        initObservers()

    }

    private fun initObservers() {
        mBinding.stars.onStart()
        viewModel.message.observe(this, Observer {
            if (it != null) {
                Utils.showAlert(this, it)
            }
        })

        viewModel.isVisibleStatuses.observe(this, Observer { visible ->
            if (visible != null) {
                mBinding.more.progress = if (visible) 0f else 0.5f
                mBinding.more.setMinProgress(if (visible) 0f else 0.5f)
                mBinding.more.setMaxProgress(if (visible) 0.5f else 1f)
                mBinding.more.playAnimation()
//                mBinding.statusMenu.more.addAnimatorListener(Animation.AnimationListener{ })
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mServiceIntent)
    }

    override fun setCallbacks() {
        viewModel.controller = (mService as Server).gameController
        viewModel.controller.test()
        viewModel.controller.lcOwner = this
        viewModel.controller.screenMessage.observe(this, Observer { value ->
            Utils.showSnackbar(mBinding.root, value!!)
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

        viewModel.controller.screenActions.observe(this, Observer {
            viewModel.handleAction(this, it)
        })
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
        (mBinding.cardRv.itemAnimator as SlideInUpAnimator).addDuration = 200
        mBinding.cardRv.adapter = cardPagerAdapter
    }

    private fun initPlayerStatus() {
        playerStatusAdapter = PlayerAdapter()
        mBinding.statusMenu.playersStatusRv.adapter = playerStatusAdapter
        mBinding.more.setOnClickListener({ viewModel.isVisibleStatuses.postValue(!viewModel.isVisibleStatuses.value) })
        mBinding.statusMenu.playersStatusRv.layoutManager =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }
}