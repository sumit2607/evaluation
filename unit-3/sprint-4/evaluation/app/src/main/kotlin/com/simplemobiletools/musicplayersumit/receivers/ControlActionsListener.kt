package com.simplemobiletools.musicplayersumit.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.simplemobiletools.musicplayersumit.extensions.sendIntent
import com.simplemobiletools.musicplayersumit.helpers.FINISH
import com.simplemobiletools.musicplayersumit.helpers.NEXT
import com.simplemobiletools.musicplayersumit.helpers.PLAYPAUSE
import com.simplemobiletools.musicplayersumit.helpers.PREVIOUS

class ControlActionsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            PREVIOUS, PLAYPAUSE, NEXT, FINISH -> context.sendIntent(action)
        }
    }
}
