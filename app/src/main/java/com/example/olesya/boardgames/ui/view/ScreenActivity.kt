package com.example.olesya.boardgames.ui.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.Utils
import com.example.olesya.boardgames.adapter.CardPagerAdapter
import com.example.olesya.boardgames.adapter.PlayerAdapter
import com.example.olesya.boardgames.connection.common.ServiceHolderActivity
import com.example.olesya.boardgames.connection.server.Server
import com.example.olesya.boardgames.databinding.ActivityScreenImaginariumBinding
import com.example.olesya.boardgames.entity.ImaginariumCard
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

//        initListView()
        initCardPager()
        startServerService()

        viewModel.message.observe(this, Observer<String> { s ->
            if (s != null) {
                Utils.showAlert(this, s)
            }
        })
        //TODO: если аргумент лямбда выражения не используется, то в фигурных скобках можно его не писать
        mBinding.buttonSend.setOnClickListener { serverMessage.postValue("anything") }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("server", "close service on destroy")
        stopService(mServiceIntent)
    }

    override fun setCallbacks() {
        viewModel.controller = (mService as Server).gameController
        viewModel.controller.lcOwner = this
        viewModel.controller.screenCards.observe(this,
                //TODO: opyat kakoj to pizdec
                Observer<MutableList<ImaginariumCard>> { cards ->
                    if (cards != null) {
                        cardPagerAdapter.setData(cards)
                    }
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
        mBinding.cardRv.itemAnimator.addDuration = 200
        mBinding.cardRv.adapter = cardPagerAdapter
//        cardPagerAdapter.setItemCallback(viewModel.getController());
        //scrolls to appeared element
//        cardPagerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                runOnUiThread(() -> mBinding.cardRv.scrollToPosition(positionStart));
//            }
//
//            @Override
//            public void onItemRangeRemoved(int positionStart, int itemCount) {
//                super.onItemRangeRemoved(positionStart, itemCount);
//            }
//        });
    }
}


//
//    private void initListView() {
//        //init player rv
//        mBinding.playersStatusRv.setHasFixedSize(false);
//        //TODO: player adapter is never used
//        mBinding.playersStatusRv.setAdapter(playerAdapter);
//        mBinding.playersStatusRv.setLayoutManager(new LinearLayoutManager(this));
//    }


//
//    private void showRoundResults() {
//        if (((Server) mService).hasClientWinPts()) {
//            Utils.showAlert(this, getResources().getString(R.string.end_of_game));
//            ((Server) mService).stopGame();
//            return;
//        }
//
//        String results = ((Server) mService).getStringResults();
//        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
//        .setTitle(R.string.round_results)
//            .setMessage(results)
//            .setCancelable(false)
//            .setNeutralButton(R.string.OK,
//                    (dialog, which) -> viewModel.getController().onStartNewRound())
//        .show();
//    }
//
//    public void showChoices(RecyclerView recyclerView) {
//        //        for (int i = 0; i < cardAdapter.getItemCount(); i++) {
////            CardPagerAdapter.Holder holder = (CardPagerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
////            holder.addChips(cardAdapter.getVotesByNum(i));
////        }
//    }
//}
