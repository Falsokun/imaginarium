package com.example.olesya.boardgames.ui.view

import android.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.widget.NumberPicker
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.Commands.DELIM
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.Utils
import com.example.olesya.boardgames.adapter.CardDiffUtil
import com.example.olesya.boardgames.adapter.CardPagerAdapter
import com.example.olesya.boardgames.connection.client.ClientService
import com.example.olesya.boardgames.connection.common.ServiceHolderActivity
import com.example.olesya.boardgames.databinding.ActivityCardImaginariumBinding
import com.example.olesya.boardgames.ui.ItemTouchCallback
import com.example.olesya.boardgames.ui.viewmodel.CardViewModel
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import java.io.Serializable

class CardActivity : ServiceHolderActivity() {

    private lateinit var mBinding: ActivityCardImaginariumBinding
    private val mAdapter: CardPagerAdapter = CardPagerAdapter()
    private val itemTouchCallback: ItemTouchCallback = ItemTouchCallback()
    lateinit var mViewModel: CardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_imaginarium)
        mViewModel = ViewModelProviders.of(this).get(CardViewModel::class.java)
        initClientService(intent)
        initListeners()
        initCardPager()
    }

    private fun initCardPager() {
        mBinding.cardRv.setHasFixedSize(true)
        mBinding.cardRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.cardRv.itemAnimator = FadeInDownAnimator()
        (mBinding.cardRv.itemAnimator as FadeInDownAnimator).addDuration = 100
//        mAdapter.setClientCallback(this);
        mBinding.cardRv.adapter = mAdapter
        val touchHelper = ItemTouchHelper(itemTouchCallback)
        touchHelper.attachToRecyclerView(mBinding.cardRv)
    }

    private fun initListeners() {
        itemTouchCallback.pickedData.observe(this, Observer {
            if (it != null) {
                val card = mAdapter.dataset[it]
                mAdapter.remove(it)
                mViewModel.onPicked(it)
                serverMessage.postValue(Commands.CLIENT_COMMANDS.CLIENT_TURN + DELIM + card.img)
            }
        })

        mBinding.buttonSend.setOnClickListener {
            val message = mBinding.testMsg.text.toString()
            (service as ClientService).onUserAction(message)
        }

        mViewModel.player.cards.observe(this, Observer { cards ->
            //scroll to position
            if (cards != null) {
                val diffResult = DiffUtil.calculateDiff(CardDiffUtil(mAdapter.dataset, cards))
                mAdapter.setData(cards)
                diffResult.dispatchUpdatesTo(mAdapter)
            }
        })

        mViewModel.picking.observe(this, Observer { value ->
            value?.let { itemTouchCallback.isSwipeEnabled = value }
        })

        mViewModel.message.observe(this, Observer {
            run {
                if (it == null)
                    return@run

                Utils.showSnackbar(mBinding.root, it)
            }
        })

        mViewModel.choosing.observe(this, Observer {
            if (it != true)
                return@Observer

            val dialogView = layoutInflater.inflate(R.layout.dialog_number_picker, null)
            val np = dialogView.findViewById<NumberPicker>(R.id.numberPicker).apply {
                maxValue = mViewModel.playersNumber
                minValue = 1
                value = 1
                wrapSelectorWheel = true
            }

            AlertDialog.Builder(this)
                    .setTitle(R.string.most_suitable_card)
                    .setView(dialogView)
                    .setCancelable(false)
                    .setNeutralButton(R.string.OK) { _, _ ->
                        serverMessage.postValue(Commands.CLIENT_COMMANDS.CLIENT_CHOOSE
                                + Commands.DELIM
                                + np.value)
                        mViewModel.choosing.value = false
                    }.show()
        })
    }

    private fun initClientService(intent: Intent?) {
        if (intent == null || intent.extras == null)
            return

        val username = intent.extras.getString(Commands.CLIENT_CONFIG.USERNAME)
        val screenAddress = intent.getSerializableExtra(Commands.CLIENT_CONFIG.HOST_CONFIG)
        startClientService(screenAddress, username)
    }

    private fun startClientService(screenAddress: Serializable?, username: String?) {
        mServiceIntent = Intent(this, ClientService::class.java)
        mServiceIntent.putExtra(Commands.ACTION_SERVER_SERVICE, false)
        mServiceIntent.putExtra(Commands.CLIENT_CONFIG.HOST_CONFIG, screenAddress)
        mServiceIntent.putExtra(Commands.CLIENT_CONFIG.USERNAME, username)
        startService(mServiceIntent)
        bindService(this)
    }

    override fun setCallbacks() {
        (mService as ClientService).callback = mViewModel
        (mService as ClientService).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(mServiceIntent)
    }
}