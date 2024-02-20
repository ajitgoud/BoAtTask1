package com.example.boattask1.dks.view.progress

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.forEachIndexed
import com.example.boattask1.dks.R
import com.example.boattask1.dks.databinding.LayoutProgressViewBinding
import com.example.boattask1.dks.view.animation.bounce.BounceAnimationDriver
import java.lang.Exception

/**
 * Dks Progress View
 * @author vikramezhil
 */

class DksProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context) {

    private var dksProgressViewListener: DksProgressViewListener? = null
    private var animationDriver: BounceAnimationDriver
private lateinit var binding: LayoutProgressViewBinding

    init {
//        View.inflate(context, R.layout.layout_progress_view, this)
        binding= LayoutProgressViewBinding.inflate(LayoutInflater.from(context))
        animationDriver = BounceAnimationDriver(context, binding.llProgressBalls)

        init(context, attrs)
    }

    /**
     * Initializes the view attributes
     * @param context Context The view context
     * @param attrs AttributeSet The view attributes
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DksProgressView, 0, 0)

        try {
            // Background
            val pvBackgroundColor = typedArray.getInt(R.styleable.DksProgressView_pvBackgroundColor, Color.WHITE)
            val pvBackgroundAlpha = typedArray.getFloat(R.styleable.DksProgressView_pvBackgroundAlpha, 1f)
            setBackground(pvBackgroundColor, pvBackgroundAlpha)

            // Ball
            val pvBallColors = typedArray.getResourceId(R.styleable.DksProgressView_pvBallColors, 0)
            setProgressBallColors(typedArray.resources.getIntArray(pvBallColors).toCollection(ArrayList()))

            // Message
            var pvMessage = typedArray.getString(R.styleable.DksProgressView_pvMessage)
            if (pvMessage.isNullOrEmpty()) pvMessage = resources.getString(R.string.bounce_ball_content_desc)
            setProgressMessage(pvMessage)

            val pvMessageColor = typedArray.getInt(R.styleable.DksProgressView_pvMessageColor, Color.BLACK)
            setProgressMessageColor(pvMessageColor)

            val pvMessageTextSize = typedArray.getFloat(R.styleable.DksProgressView_pvMessageTextSize, 0f)
            setProgressMessageTextSize(pvMessageTextSize)

            // Positive Button
            var pvPositiveButtonText = typedArray.getString(R.styleable.DksProgressView_pvPositiveButtonText)
            if (pvPositiveButtonText.isNullOrEmpty()) pvPositiveButtonText = resources.getString(R.string.positive)
            setPositiveButtonText(pvPositiveButtonText)

            val pvPositiveButtonWidth = typedArray.getInt(R.styleable.DksProgressView_pvPositiveButtonWidth, 0)
            val pvPositiveButtonHeight = typedArray.getInt(R.styleable.DksProgressView_pvPositiveButtonHeight, 0)
            setPositiveButtonDimensions(pvPositiveButtonWidth, pvPositiveButtonHeight)

            val pvPositiveButtonTextSize = typedArray.getFloat(R.styleable.DksProgressView_pvPositiveButtonTextSize, 0f)
            setPositiveButtonTextSize(pvPositiveButtonTextSize)

            val pvPositiveButtonBackgroundColor = typedArray.getInt(R.styleable.DksProgressView_pvPositiveButtonBackgroundColor, Color.GRAY)
            val pvPositiveButtonTextColor = typedArray.getInt(R.styleable.DksProgressView_pvPositiveButtonTextColor, Color.BLACK)
            val pvPositiveButtonCornerRadius = typedArray.getInt(R.styleable.DksProgressView_pvPositiveButtonCornerRadius, 1)
            val pvPositiveButtonBackgroundAlpha = typedArray.getFloat(R.styleable.DksProgressView_pvPositiveButtonBackgroundAlpha, 1f)
            val pvPositiveButtonTextCaps = typedArray.getBoolean(R.styleable.DksProgressView_pvPositiveButtonTextCaps, false)
            setPositiveButtonProperties(pvPositiveButtonBackgroundColor, pvPositiveButtonTextColor, pvPositiveButtonCornerRadius, pvPositiveButtonBackgroundAlpha, pvPositiveButtonTextCaps)

            // Neutral Button
            var pvNeutralButtonText = typedArray.getString(R.styleable.DksProgressView_pvNeutralButtonText)
            if (pvNeutralButtonText.isNullOrEmpty()) pvNeutralButtonText = resources.getString(R.string.neutral)
            setNeutralButtonText(pvNeutralButtonText)

            val pvNeutralButtonWidth = typedArray.getInt(R.styleable.DksProgressView_pvNeutralButtonWidth, 0)
            val pvNeutralButtonHeight = typedArray.getInt(R.styleable.DksProgressView_pvNeutralButtonHeight, 0)
            setNeutralButtonDimensions(pvNeutralButtonWidth, pvNeutralButtonHeight)

            val pvNeutralButtonTextSize = typedArray.getFloat(R.styleable.DksProgressView_pvNeutralButtonTextSize, 0f)
            setNeutralButtonTextSize(pvNeutralButtonTextSize)

            val pvNeutralButtonBackgroundColor = typedArray.getInt(R.styleable.DksProgressView_pvNeutralButtonBackgroundColor, Color.GRAY)
            val pvNeutralButtonTextColor = typedArray.getInt(R.styleable.DksProgressView_pvNeutralButtonTextColor, Color.BLACK)
            val pvNeutralButtonCornerRadius = typedArray.getInt(R.styleable.DksProgressView_pvNeutralButtonCornerRadius, 1)
            val pvNeutralButtonBackgroundAlpha = typedArray.getFloat(R.styleable.DksProgressView_pvNeutralButtonBackgroundAlpha, 1f)
            val pvNeutralButtonTextCaps = typedArray.getBoolean(R.styleable.DksProgressView_pvNeutralButtonTextCaps, false)
            setNeutralButtonProperties(pvNeutralButtonBackgroundColor, pvNeutralButtonTextColor, pvNeutralButtonCornerRadius, pvNeutralButtonBackgroundAlpha, pvNeutralButtonTextCaps)

            // Negative Button
            var pvNegativeButtonText = typedArray.getString(R.styleable.DksProgressView_pvNegativeButtonText)
            if (pvNegativeButtonText.isNullOrEmpty()) pvNegativeButtonText = resources.getString(R.string.negative)
            setNegativeButtonText(pvNegativeButtonText)

            val pvNegativeButtonWidth = typedArray.getInt(R.styleable.DksProgressView_pvNegativeButtonWidth, 0)
            val pvNegativeButtonHeight = typedArray.getInt(R.styleable.DksProgressView_pvNegativeButtonHeight, 0)
            setNegativeButtonDimensions(pvNegativeButtonWidth, pvNegativeButtonHeight)

            val pvNegativeButtonTextSize = typedArray.getFloat(R.styleable.DksProgressView_pvNegativeButtonTextSize, 0f)
            setNegativeButtonTextSize(pvNegativeButtonTextSize)

            val pvNegativeButtonBackgroundColor = typedArray.getInt(R.styleable.DksProgressView_pvNegativeButtonBackgroundColor, Color.GRAY)
            val pvNegativeButtonTextColor = typedArray.getInt(R.styleable.DksProgressView_pvNegativeButtonTextColor, Color.BLACK)
            val pvNegativeButtonCornerRadius = typedArray.getInt(R.styleable.DksProgressView_pvNegativeButtonCornerRadius, 1)
            val pvNegativeButtonBackgroundAlpha = typedArray.getFloat(R.styleable.DksProgressView_pvNegativeButtonBackgroundAlpha, 1f)
            val pvNegativeButtonTextCaps = typedArray.getBoolean(R.styleable.DksProgressView_pvNegativeButtonTextCaps, false)
            setNegativeButtonProperties(pvNegativeButtonBackgroundColor, pvNegativeButtonTextColor, pvNegativeButtonCornerRadius, pvNegativeButtonBackgroundAlpha, pvNegativeButtonTextCaps)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }
    }

    // Progress View Listener

    /**
     * Sets the progress view listener instance
     * @param dksProgressViewListener ProgressViewListener The class instance which implements the listener
     * @override onClickedPositive, onClickedNeutral, onClickedNegative
     */
    fun setProgressViewListener(dksProgressViewListener: DksProgressViewListener) {
        this.dksProgressViewListener = dksProgressViewListener

        binding.btnPositive.setOnClickListener { this.dksProgressViewListener?.onClickedPositive() }
        binding.btnNeutral.setOnClickListener { this.dksProgressViewListener?.onClickedNeutral() }
        binding.btnNegative.setOnClickListener { this.dksProgressViewListener?.onClickedNegative() }
    }

    // Progress View Background

    /**
     * Sets the progress view background
     * @param bgColor Int The background color
     * @param alpha Float The alpha value
     */
    fun setBackground(bgColor: Int, alpha: Float) {
        binding.viewBg.setBackgroundColor(bgColor)
        binding.viewBg.alpha = alpha
    }

    // Progress View Message

    /**
     * Sets the progress message in the text view
     * @param text String The progress message to be set in the text view
     */
    fun setProgressMessage(text: String) {
        binding.tvProgressMessage.text = text
    }

    /**
     * Sets the progress message color
     * @param messageColor Int The progress message color
     */
    fun setProgressMessageColor(messageColor: Int) {
        binding.tvProgressMessage.setTextColor(messageColor)
    }

    /**
     * Sets the progress message text size
     * @param size Float The progress message text size
     */
    fun setProgressMessageTextSize(size: Float) {
        if (size > 0) {
            binding.tvProgressMessage.textSize = size
        }
    }

    // Progress View Positive Button

    /**
     * Sets the positive button text
     * @param text String The positive text to be shown in the button
     */
    fun setPositiveButtonText(text: String) {
        binding.btnPositive.text = text
    }

    /**
     * Sets the positive button text size
     * @param size Float The positive button text size
     */
    fun setPositiveButtonTextSize(size: Float) {
        if (size > 0) {
           binding.btnPositive.textSize = size
        }
    }

    /**
     * Sets the positive button properties
     * @param bgColor Int The background color
     * @param txtColor Int The text color
     * @param cornerRadius Int The corner radius
     * @param alpha Float The alpha value
     * @param isAllCaps Boolean The all caps status
     */
    fun setPositiveButtonProperties(bgColor: Int, txtColor: Int, cornerRadius: Int, alpha: Float, isAllCaps: Boolean) {

        binding.btnPositive.setBackgroundColor(bgColor)
        binding.btnPositive.setTextColor(txtColor)
        binding.btnPositive.cornerRadius = cornerRadius
        binding.btnPositive.alpha = alpha
        binding.btnPositive.isAllCaps = isAllCaps
    }

    /**
     * Sets the positive button dimensions (width and height)
     * @param width Int The button width
     * @param height Int The button height
     */
    fun setPositiveButtonDimensions(width: Int, height: Int) {
        val params = binding.btnPositive.layoutParams

        if (width > 0) {
            binding.btnPositive.width = width
        }

        if (height > 0) {
            binding.btnPositive.height = height
        }

        binding.btnPositive.layoutParams = params
    }

    /**
     * Sets the positive button visibility
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    fun setPositiveButtonVisibility(visibility: Int) {
        binding.btnPositive.visibility = visibility
    }

    // Progress View Neutral Button

    /**
     * Sets the neutral button text
     * @param text String The neutral text to be shown in the button
     */
    fun setNeutralButtonText(text: String) {
        binding.btnNeutral.text = text
    }

    /**
     * Sets the neutral button text size
     * @param size Float The neutral button text size
     */
    fun setNeutralButtonTextSize(size: Float) {
        if (size > 0) {
            binding.btnNeutral.textSize = size
        }
    }

    /**
     * Sets the neutral button properties
     * @param bgColor Int The background color
     * @param txtColor Int The text color
     * @param cornerRadius Int The corner radius
     * @param alpha Float The alpha value
     * @param isAllCaps Boolean The all caps status
     */
    fun setNeutralButtonProperties(bgColor: Int, txtColor: Int, cornerRadius: Int, alpha: Float, isAllCaps: Boolean) {
        binding.btnNeutral.setBackgroundColor(bgColor)
        binding.btnNeutral.setTextColor(txtColor)
        binding.btnNeutral.cornerRadius = cornerRadius
        binding.btnNeutral.alpha = alpha
        binding.btnNeutral.isAllCaps = isAllCaps
    }

    /**
     * Sets the neutral button dimensions (width and height)
     * @param width Int The button width
     * @param height Int The button height
     */
    fun setNeutralButtonDimensions(width: Int, height: Int) {
        val params = binding.btnNeutral.layoutParams

        if (width > 0) {
            binding.btnNeutral.width = width
        }

        if (height > 0) {
            binding.btnNeutral.height = height
        }

        binding.btnNeutral.layoutParams = params
    }

    /**
     * Sets the neutral button visibility
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    fun setNeutralButtonVisibility(visibility: Int) {
        binding.btnNeutral.visibility = visibility
    }

    // Progress View Negative Button

    /**
     * Sets the negative button text
     * @param visibility String The negative text to be shown in the button
     */
    fun setNegativeButtonText(visibility: String) {
        binding.btnNegative.text = visibility
    }

    /**
     * Sets the negative button text size
     * @param size Float The negative button text size
     */
    fun setNegativeButtonTextSize(size: Float) {
        if (size > 0) {
            binding.btnNegative.textSize = size
        }
    }

    /**
     * Sets the negative button properties
     * @param bgColor Int The background color
     * @param txtColor Int The text color
     * @param cornerRadius Int The corner radius
     * @param alpha Float The alpha value
     * @param isAllCaps Boolean The all caps status
     */
    fun setNegativeButtonProperties(bgColor: Int, txtColor: Int, cornerRadius: Int, alpha: Float, isAllCaps: Boolean) {
        binding.btnNegative.setBackgroundColor(bgColor)
        binding.btnNegative.setTextColor(txtColor)
        binding.btnNegative.cornerRadius = cornerRadius
        binding.btnNegative.alpha = alpha
        binding.btnNegative.isAllCaps = isAllCaps
    }

    /**
     * Sets the negative button dimensions (width and height)
     * @param width Int The button width
     * @param height Int The button height
     */
    fun setNegativeButtonDimensions(width: Int, height: Int) {
        val params = binding.btnNegative.layoutParams

        if (width > 0) {
            binding.btnNegative.width = width
        }

        if (height > 0) {
            binding.btnNegative.height = height
        }

        binding.btnNegative.layoutParams = params
    }

    /**
     * Sets the negative button visibility
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     */
    fun setNegativeButtonVisibility(visibility: Int) {
        binding.btnNegative.visibility = visibility
    }

    // Progress View Ball

    /**
     * Sets the progress ball colors
     * @param progressBallColors ArrayList<Int> The progress bar color list
     */
    fun setProgressBallColors(progressBallColors: ArrayList<Int>) {
        binding.llProgressBalls.forEachIndexed { index, view ->
            if (index < progressBallColors.size) {
                (view as ImageView).setColorFilter(progressBallColors[index])
            }
        }
    }

    /**
     * Sets the progress ball frequency
     * @param rmsdB The frequency value
     */
    fun setProgressBallFrequency(rmsdB: Float) {
        animationDriver.start(rmsdB)
    }
}