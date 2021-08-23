package com.simplemobiletools.musicplayersumit.fragments

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import com.google.gson.Gson
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.underlineText
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.musicplayersumit.R
import com.simplemobiletools.musicplayersumit.activities.SimpleActivity
import com.simplemobiletools.musicplayersumit.activities.TracksActivity
import com.simplemobiletools.musicplayersumit.adapters.PlaylistsAdapter
import com.simplemobiletools.musicplayersumit.dialogs.ChangeSortingDialog
import com.simplemobiletools.musicplayersumit.dialogs.NewPlaylistDialog
import com.simplemobiletools.musicplayersumit.extensions.config
import com.simplemobiletools.musicplayersumit.extensions.playlistDAO
import com.simplemobiletools.musicplayersumit.extensions.tracksDAO
import com.simplemobiletools.musicplayersumit.helpers.PLAYLIST
import com.simplemobiletools.musicplayersumit.helpers.TAB_PLAYLISTS
import com.simplemobiletools.musicplayersumit.models.Events
import com.simplemobiletools.musicplayersumit.models.Playlist
import kotlinx.android.synthetic.main.fragment_playlists.view.*
import org.greenrobot.eventbus.EventBus

class PlaylistsFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    private var playlistsIgnoringSearch = ArrayList<Playlist>()

    override fun setupFragment(activity: SimpleActivity) {
        playlists_placeholder_2.underlineText()
        playlists_placeholder_2.setOnClickListener {
            NewPlaylistDialog(activity) {
                EventBus.getDefault().post(Events.PlaylistsUpdated())
            }
        }

        ensureBackgroundThread {
            val playlists = context.playlistDAO.getAll() as ArrayList<Playlist>
            playlists.forEach {
                it.trackCnt = context.tracksDAO.getTracksCountFromPlaylist(it.id)
            }

            Playlist.sorting = context.config.playlistSorting
            playlists.sort()

            activity.runOnUiThread {
                playlists_placeholder.text = context.getString(R.string.no_items_found)
                playlists_placeholder.beVisibleIf(playlists.isEmpty())
                playlists_placeholder_2.beVisibleIf(playlists.isEmpty())
                val adapter = playlists_list.adapter
                if (adapter == null) {
                    PlaylistsAdapter(activity, playlists, playlists_list, playlists_fastscroller) {
                        Intent(activity, TracksActivity::class.java).apply {
                            putExtra(PLAYLIST, Gson().toJson(it))
                            activity.startActivity(this)
                        }
                    }.apply {
                        playlists_list.adapter = this
                    }

                    playlists_list.scheduleLayoutAnimation()
                    playlists_fastscroller.setViews(playlists_list) {
                        val playlist = (playlists_list.adapter as PlaylistsAdapter).playlists.getOrNull(it)
                        playlists_fastscroller.updateBubbleText(playlist?.getBubbleText() ?: "")
                    }
                } else {
                    (adapter as PlaylistsAdapter).updateItems(playlists)
                }
            }
        }
    }

    override fun finishActMode() {
        (playlists_list.adapter as? MyRecyclerViewAdapter)?.finishActMode()
    }

    override fun onSearchQueryChanged(text: String) {
        val filtered = playlistsIgnoringSearch.filter { it.title.contains(text, true) }.toMutableList() as ArrayList<Playlist>
        (playlists_list.adapter as? PlaylistsAdapter)?.updateItems(filtered, text)
        playlists_placeholder.beVisibleIf(filtered.isEmpty())
        playlists_placeholder_2.beVisibleIf(filtered.isEmpty())
    }

    override fun onSearchOpened() {
        playlistsIgnoringSearch = (playlists_list?.adapter as? PlaylistsAdapter)?.playlists ?: ArrayList()
    }

    override fun onSearchClosed() {
        (playlists_list.adapter as? PlaylistsAdapter)?.updateItems(playlistsIgnoringSearch)
        playlists_placeholder.beGoneIf(playlistsIgnoringSearch.isNotEmpty())
        playlists_placeholder_2.beGoneIf(playlistsIgnoringSearch.isNotEmpty())
    }

    override fun onSortOpen(activity: SimpleActivity) {
        ChangeSortingDialog(activity, TAB_PLAYLISTS) {
            val adapter = playlists_list.adapter as? PlaylistsAdapter ?: return@ChangeSortingDialog
            val playlists = adapter.playlists
            Playlist.sorting = activity.config.playlistSorting
            playlists.sort()
            adapter.updateItems(playlists, forceUpdate = true)
        }
    }

    override fun setupColors(textColor: Int, adjustedPrimaryColor: Int) {
        playlists_placeholder.setTextColor(textColor)
        playlists_placeholder_2.setTextColor(adjustedPrimaryColor)

        playlists_fastscroller.updatePrimaryColor()
        playlists_fastscroller.updateBubbleColors()
    }
}
