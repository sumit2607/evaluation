package com.simplemobiletools.musicplayersumit.extensions

import android.app.Activity
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.musicplayersumit.dialogs.SelectPlaylistDialog
import com.simplemobiletools.musicplayersumit.helpers.RoomHelper
import com.simplemobiletools.musicplayersumit.models.Track
import com.simplemobiletools.musicplayersumit.services.MusicService

fun Activity.addTracksToPlaylist(tracks: List<Track>, callback: () -> Unit) {
    SelectPlaylistDialog(this) { playlistId ->
        val tracksToAdd = ArrayList<Track>()
        tracks.forEach {
            it.id = 0
            it.playListId = playlistId
            tracksToAdd.add(it)
        }

        ensureBackgroundThread {
            RoomHelper(this).insertTracksWithPlaylist(tracksToAdd)

            runOnUiThread {
                callback()
            }
        }
    }
}

fun Activity.addTracksToQueue(tracks: List<Track>, callback: () -> Unit) {
    addQueueItems(tracks) {
        tracks.forEach { track ->
            if (MusicService.mTracks.none { it.mediaStoreId == track.mediaStoreId }) {
                MusicService.mTracks.add(track)
            }
        }

        runOnUiThread {
            callback()
        }
    }
}
