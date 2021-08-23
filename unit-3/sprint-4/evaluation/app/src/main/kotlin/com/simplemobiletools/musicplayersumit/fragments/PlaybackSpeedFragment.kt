package com.simplemobiletools.musicplayersumit.fragments

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.onSeekBarChangeListener
import com.simplemobiletools.commons.extensions.updateTextColors
import com.simplemobiletools.commons.views.MySeekBar
import com.simplemobiletools.commons.views.MyTextView
import com.simplemobiletools.musicplayersumit.R
import com.simplemobiletools.musicplayersumit.extensions.config
import com.simplemobiletools.musicplayersumit.helpers.Config
import com.simplemobiletools.musicplayersumit.interfaces.PlaybackSpeedListener
import kotlinx.android.synthetic.main.fragment_playback_speed.view.*

class PlaybackSpeedFragment : BottomSheetDialogFragment() {
    private val MIN_PLAYBACK_SPEED = 0.25f
    private val MAX_PLAYBACK_SPEED = 3f
    private val MAX_PROGRESS = (MAX_PLAYBACK_SPEED * 100 + MIN_PLAYBACK_SPEED * 100).toInt()
    private val HALF_PROGRESS = MAX_PROGRESS / 2
    private val STEP = 0.05f

    private var seekBar: MySeekBar? = null
    private var listener: PlaybackSpeedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val config = context!!.config
        val view = inflater.inflate(R.layout.fragment_playback_speed, container, false)
        val background = context!!.resources.getDrawable(R.drawable.bottom_sheet_bg)
        (background as LayerDrawable).findDrawableByLayerId(R.id.bottom_sheet_background).applyColorFilter(config.backgroundColor)

        view.apply {
            seekBar = playback_speed_seekbar
            setBackgroundDrawable(background)
            context!!.updateTextColors(playback_speed_holder)
            playback_speed_slow.applyColorFilter(config.textColor)
            playback_speed_fast.applyColorFilter(config.textColor)
            playback_speed_slow.setOnClickListener { reduceSpeed() }
            playback_speed_fast.setOnClickListener { increaseSpeed() }
            initSeekbar(playback_speed_seekbar, playback_speed_label, config)
        }

        return view
    }

    private fun initSeekbar(seekbar: MySeekBar, speedLabel: MyTextView, config: Config) {
        val formattedValue = formatPlaybackSpeed(config.playbackSpeed)
        speedLabel.text = "${formattedValue}x"
        seekbar.max = MAX_PROGRESS

        val playbackSpeedProgress = config.playbackSpeedProgress
        if (playbackSpeedProgress == -1) {
            config.playbackSpeedProgress = HALF_PROGRESS
        }
        seekbar.progress = config.playbackSpeedProgress

        var lastUpdatedProgress = config.playbackSpeedProgress
        var lastUpdatedFormattedValue = formattedValue

        seekbar.onSeekBarChangeListener { progress ->
            val playbackSpeed = getPlaybackSpeed(progress)
            if (playbackSpeed.toString() != lastUpdatedFormattedValue) {
                lastUpdatedProgress = progress
                lastUpdatedFormattedValue = playbackSpeed.toString()
                config.playbackSpeed = playbackSpeed
                config.playbackSpeedProgress = progress

                speedLabel.text = "${formatPlaybackSpeed(playbackSpeed)}x"
                listener?.updatePlaybackSpeed(playbackSpeed)
            } else {
                seekbar.progress = lastUpdatedProgress
            }
        }
    }

    private fun getPlaybackSpeed(progress: Int): Float {
        var playbackSpeed = when {
            progress < HALF_PROGRESS -> {
                val lowerProgressPercent = progress / HALF_PROGRESS.toFloat()
                val lowerProgress = (1 - MIN_PLAYBACK_SPEED) * lowerProgressPercent + MIN_PLAYBACK_SPEED
                lowerProgress
            }
            progress > HALF_PROGRESS -> {
                val upperProgressPercent = progress / HALF_PROGRESS.toFloat() - 1
                val upperDiff = MAX_PLAYBACK_SPEED - 1
                upperDiff * upperProgressPercent + 1
            }
            else -> 1f
        }
        playbackSpeed = Math.min(Math.max(playbackSpeed, MIN_PLAYBACK_SPEED), MAX_PLAYBACK_SPEED)
        val stepMultiplier = 1 / STEP
        return Math.round(playbackSpeed * stepMultiplier) / stepMultiplier
    }

    private fun reduceSpeed() {
        var currentProgress = seekBar?.progress ?: return
        val currentSpeed = context!!.config.playbackSpeed
        while (currentProgress > 0) {
            val newSpeed = getPlaybackSpeed(--currentProgress)
            if (newSpeed != currentSpeed) {
                seekBar!!.progress = currentProgress
                break
            }
        }
    }

    private fun increaseSpeed() {
        var currentProgress = seekBar?.progress ?: return
        val currentSpeed = context!!.config.playbackSpeed
        while (currentProgress < MAX_PROGRESS) {
            val newSpeed = getPlaybackSpeed(++currentProgress)
            if (newSpeed != currentSpeed) {
                seekBar!!.progress = currentProgress
                break
            }
        }
    }

    private fun formatPlaybackSpeed(value: Float) = String.format("%.2f", value)

    fun setListener(playbackSpeedListener: PlaybackSpeedListener) {
        listener = playbackSpeedListener
    }
}
