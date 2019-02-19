package com.example.olesya.boardgames.ui.custom.view

import android.animation.AnimatorInflater
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.Utils
import android.widget.LinearLayout.LayoutParams



class CardView(context: Context, attrs: AttributeSet? = null) : ImageView(context, attrs), View.OnClickListener {

    private var faceDownUrl: String? = null

    private var faceUpUrl: String

    private val animationStart = AnimatorInflater.loadAnimator(context, R.animator.animation_rotate_hide)
    private val animationEnd = AnimatorInflater.loadAnimator(context, R.animator.animation_rotate_reveal)

    private val animStartLen: Long
    private val animEndLen: Long

    private var isAnimating: Boolean = false

    var isHidden: Boolean = true

    init {
        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.CardView)
        faceDownUrl = ta.getString(R.styleable.CardView_faceDown)
        faceUpUrl = ta.getString(R.styleable.CardView_faceUp) ?: Utils.DEFAULT_FACE_UP
        ta.recycle()
        loadImage()
        setOnClickListener(this)
        animStartLen = getContext().resources.getInteger(R.integer.anim_length_half).toLong()
        animEndLen = animStartLen * 2
        scaleType = ScaleType.FIT_CENTER
    }

    override fun onClick(v: View?) {
        if (!isAnimating) {
            isHidden = !isHidden
            startAnimation()
            isAnimating = true
        }
    }

    fun setFaceUp(url: String) {
        faceUpUrl = url
        invalidate()
    }

    private fun startAnimation() {
        animationStart.setTarget(this)
        animationEnd.setTarget(this)
        val scale = context.resources.displayMetrics.density
        val distance = 10 * width
        cameraDistance = distance * scale

        Log.d("anim", "start rot " + rotationY)
        //half of anim ends
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("anim", "half rot " + rotationY)
            rotationY = 90f
            loadImage()
        }, animStartLen)

        //anim ends
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("anim", "end rot " + rotationY)
            rotationY = 0f
            isAnimating = false
        }, animStartLen * 2)
        animationStart.start()
        animationEnd.start()
    }

    private fun loadImage() {
        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_image_black_24dp)
                .fitCenter()
                .transform(RoundedCorners(10))

        Glide.with(context)
                .load(if (!isHidden) faceUpUrl else faceDownUrl)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)
    }
}