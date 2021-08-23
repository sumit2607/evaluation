package com.simplemobiletools.musicplayersumit.fragments

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import com.google.gson.Gson
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.musicplayersumit.R
import com.simplemobiletools.musicplayersumit.activities.SimpleActivity
import com.simplemobiletools.musicplayersumit.activities.TrackActivity
import com.simplemobiletools.musicplayersumit.adapters.TracksAdapter
import com.simplemobiletools.musicplayersumit.dialogs.ChangeSortingDialog
import com.simplemobiletools.musicplayersumit.extensions.*
import com.simplemobiletools.musicplayersumit.helpers.RESTART_PLAYER
import com.simplemobiletools.musicplayersumit.helpers.TAB_TRACKS
import com.simplemobiletools.musicplayersumit.helpers.TRACK
import com.simplemobiletools.musicplayersumit.models.Album
import com.simplemobiletools.musicplayersumit.models.Track
import kotlinx.android.synthetic.main.fragment_tracks.view.*

// Artists -> Albums -> Tracks
class TracksFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    private var tracksIgnoringSearch = ArrayList<Track>()

    override fun setupFragment(activity: SimpleActivity) {
        ensureBackgroundThread {
            val albums = ArrayList<Album>()
            val artists = context.getArtistsSync()
            artists.forEach { artist ->
                albums.addAll(context.getAlbumsSync(artist))
            }

            val tracks = ArrayList<Track>()
            albums.forEach {
                tracks.addAll(context.getAlbumTracksSync(it.id))
            }

            Track.sorting = context.config.trackSorting
            tracks.sort()

            activity.runOnUiThread {
                tracks_placeholder.text = context.getString(R.string.no_items_found)
                tracks_placeholder.beVisibleIf(tracks.isEmpty())
                val adapter = tracks_list.adapter
                if (adapter == null) {
                    TracksAdapter(activity, tracks, false, tracks_list, tracks_fastscroller) {
                        activity.resetQueueItems(tracks) {
                            Intent(activity, TrackActivity::class.java).apply {
                                putExtra(TRACK, Gson().toJson(it))
                                putExtra(RESTART_PLAYER, true)
                                activity.startActivity(this)
                            }
                        }
                    }.apply {
                        tracks_list.adapter = this
                    }

                    tracks_list.scheduleLayoutAnimation()
                    tracks_fastscroller.setViews(tracks_list) {
                        val track = (tracks_list.adapter as TracksAdapter).tracks.getOrNull(it)
                        tracks_fastscroller.updateBubbleText(track?.getBubbleText() ?: "")
                    }
                } else {
                    (adapter as TracksAdapter).updateItems(tracks)
                }
            }
        }
    }

    override fun finishActMode() {
        (tracks_list.adapter as? MyRecyclerViewAdapter)?.finishActMode()
    }

    override fun onSearchQueryChanged(text: String) {
        val filtered = tracksIgnoringSearch.filter { it.title.contains(text, true) }.toMutableList() as ArrayList<Track>
        (tracks_list.adapter as? TracksAdapter)?.updateItems(filtered, text)
        tracks_placeholder.beVisibleIf(filtered.isEmpty())
    }

    override fun onSearchOpened() {
        tracksIgnoringSearch = (tracks_list?.adapter as? TracksAdapter)?.tracks ?: ArrayList()
    }

    override fun onSearchClosed() {
        (tracks_list.adapter as? TracksAdapter)?.updateItems(tracksIgnoringSearch)
        tracks_placeholder.beGoneIf(tracksIgnoringSearch.isNotEmpty())
    }

    override fun onSortOpen(activity: SimpleActivity) {
        ChangeSortingDialog(activity, TAB_TRACKS) {
            val adapter = tracks_list.adapter as? TracksAdapter ?: return@ChangeSortingDialog
            val tracks = adapter.tracks
            Track.sorting = activity.config.trackSorting
            tracks.sort()
            adapter.updateItems(tracks, forceUpdate = true)
        }
    }

    override fun setupColors(textColor: Int, adjustedPrimaryColor: Int) {
        tracks_fastscroller.updatePrimaryColor()
        tracks_fastscroller.updateBubbleColors()
    }
}
