package xyz.graphitenerd.tassel.model

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents a smart collection of bookmarks based on dynamic filters
 */
sealed class SmartCollection(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    /**
     * Bookmarks marked as unread (isRead = false)
     */
    data object ReadLater : SmartCollection(
        id = "read_later",
        title = "Read Later",
        icon = Icons.Default.AutoStories,
        description = "Bookmarks you haven't read yet"
    )

    /**
     * Bookmarks added in the last 7 days
     */
    data object RecentlyAdded : SmartCollection(
        id = "recently_added",
        title = "This Week",
        icon = Icons.Default.CalendarToday,
        description = "Added in the last 7 days"
    )

    /**
     * Bookmarks marked as favorite (isFavorite = true)
     */
    data object Favorites : SmartCollection(
        id = "favorites",
        title = "Favorites",
        icon = Icons.Default.Star,
        description = "Your starred bookmarks"
    )

    /**
     * Bookmarks with highest open count
     */
    data object MostVisited : SmartCollection(
        id = "most_visited",
        title = "Most Visited",
        icon = Icons.Default.Timeline,
        description = "Your most accessed bookmarks"
    )

    /**
     * Bookmarks with mediaType containing "video"
     */
    data object Videos : SmartCollection(
        id = "videos",
        title = "Videos",
        icon = Icons.Default.VideoLibrary,
        description = "Video bookmarks"
    )

    /**
     * Bookmarks opened in the last 24 hours
     */
    data object RecentlyViewed : SmartCollection(
        id = "recently_viewed",
        title = "Recently Viewed",
        icon = Icons.Default.AccessTime,
        description = "Opened in the last 24 hours"
    )

    companion object {
        /**
         * All available smart collections in display order
         */
        fun getAll(): List<SmartCollection> = listOf(
            ReadLater,
            RecentlyAdded,
            Favorites,
            MostVisited,
            Videos,
            RecentlyViewed
        )

        /**
         * Get default/featured collections to show on home screen
         */
        fun getFeatured(): List<SmartCollection> = listOf(
            ReadLater,
            RecentlyAdded,
            Favorites,
            Videos
        )

        /**
         * Get collection by ID
         */
        fun getById(id: String): SmartCollection? = getAll().find { it.id == id }
    }
}

/**
 * UI state for a smart collection with bookmark count
 */
data class SmartCollectionWithCount(
    val collection: SmartCollection,
    val count: Int
)
