package com.simplemobiletools.musicplayersumit.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.musicplayersumit.R
import com.simplemobiletools.musicplayersumit.adapters.QueueAdapter
import com.simplemobiletools.musicplayersumit.dialogs.NewPlaylistDialog
import com.simplemobiletools.musicplayersumit.helpers.PLAY_TRACK
import com.simplemobiletools.musicplayersumit.helpers.RoomHelper
import com.simplemobiletools.musicplayersumit.helpers.TRACK_ID
import com.simplemobiletools.musicplayersumit.models.Events
import com.simplemobiletools.musicplayersumit.models.Track
import com.simplemobiletools.musicplayersumit.services.MusicService
import kotlinx.android.synthetic.main.activity_queue.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class QueueActivity : SimpleActivity() {
    private var bus: EventBus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)
        bus = EventBus.getDefault()
        bus!!.register(this)
        setupAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        bus?.unregister(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_queue, menu)
        updateMenuItemColors(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_playlist_from_queue -> createPlaylistFromQueue()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupAdapter() {
        val adapter = queue_list.adapter
        if (adapter == null) {
            val queueAdapter = QueueAdapter(this, MusicService.mTracks, queue_list, queue_fastscroller) {
                Intent(this, MusicService::class.java).apply {
                    action = PLAY_TRACK
                    putExtra(TRACK_ID, (it as Track).mediaStoreId)
                    startService(this)
                }
            }.apply {
                queue_list.adapter = this
            }

            queue_list.scheduleLayoutAnimation()
            queue_fastscroller.setViews(queue_list) {
                val track = queueAdapter.items.getOrNull(it)
                queue_fastscroller.updateBubbleText(track?.title ?: "")
            }
        } else {
            adapter.notifyDataSetChanged()
        }
    }

    private fun createPlaylistFromQueue() {
        NewPlaylistDialog(this) { newPlaylistId ->
            val tracks = ArrayList<Track>()
            (queue_list.adapter as? QueueAdapter)?.items?.forEach {
                it.playListId = newPlaylistId
                tracks.add(it)
            }

            ensureBackgroundThread {
                RoomHelper(this).insertTracksWithPlaylist(tracks)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun trackChangedEvent(event: Events.TrackChanged) {
        setupAdapter()
    }
}
