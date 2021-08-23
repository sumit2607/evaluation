package com.simplemobiletools.musicplayersumit.models

import androidx.room.*
import com.simplemobiletools.commons.helpers.AlphanumericComparator
import com.simplemobiletools.commons.helpers.SORT_DESCENDING
import com.simplemobiletools.musicplayersumit.helpers.PLAYER_SORT_BY_TITLE

@Entity(tableName = "playlists", indices = [(Index(value = ["id"], unique = true))])
data class Playlist(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "title") var title: String,

    @Ignore var trackCnt: Int = 0
) : Comparable<Playlist> {
    constructor() : this(0, "", 0)

    companion object {
        var sorting = 0
    }

    override fun compareTo(other: Playlist): Int {
        var result = when {
            sorting and PLAYER_SORT_BY_TITLE != 0 -> AlphanumericComparator().compare(title.toLowerCase(), other.title.toLowerCase())
            else -> trackCnt.compareTo(other.trackCnt)
        }

        if (sorting and SORT_DESCENDING != 0) {
            result *= -1
        }

        return result
    }

    fun getBubbleText() = when {
        sorting and PLAYER_SORT_BY_TITLE != 0 -> title
        else -> trackCnt.toString()
    }
}
