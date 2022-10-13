package xyz.graphitenerd.tassel

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.graphitenerd.tassel.data.IRepository
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder



class FakeRepository: IRepository {

    override fun getAllBookmarks(): Flow<List<Bookmark>> {
        TODO("Not yet implemented")
    }

    override fun countBookmarks(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun addBookmark(bookmark: Bookmark): Long {
        TODO("Not yet implemented")
    }

    override fun getRecentBookmarks(): Flow<List<Bookmark>> {
        TODO("Not yet implemented")
    }

    override fun getBookmarkById(id: Long): Bookmark {
        return bookmarks.first { it.id == id } ?: Bookmark(
            id = 0,
            rawUrl = "https://"
        )
    }

    override fun getLastSavedBookmark(time: Long): List<Bookmark> {
        TODO("Not yet implemented")
    }

    override fun deleteBookmark(bookmark: Bookmark) {
        TODO("Not yet implemented")
    }

    override fun getFolderById(id: Long): BookmarkFolder {
        return folders.first { it.id == id } ?: BookmarkFolder(
            name = "HOME",
            parentId = null
        )
    }

    override fun getFoldersByParentId(id: Long?): List<BookmarkFolder> {
        TODO("Not yet implemented")
    }

    override fun getFolderByName(name: String): BookmarkFolder {
        TODO("Not yet implemented")
    }

    override fun getBookmarksByFolders(id: Long): Flow<List<Bookmark>> {
        TODO("Not yet implemented")
    }

    override fun insertFolder(folder: BookmarkFolder) {
        TODO("Not yet implemented")
    }

    override fun getFolders(): List<BookmarkFolder> {
        TODO("Not yet implemented")
    }

    override fun syncFoldersToCloud() {
        TODO("Not yet implemented")
    }

    override fun syncBookmarksToCloud() {
        TODO("Not yet implemented")
    }

    override suspend fun saveBookmarkToCloud(bookmark: Bookmark) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAndSyncBookmark(bookmark: Bookmark) {
        return
    }

    override suspend fun saveAndSyncFolder(folder: BookmarkFolder) {
        TODO("Not yet implemented")
    }

    override suspend fun syncUnsyncedBookmarks() {
        TODO("Not yet implemented")
    }
}

object BoolAsStringSerializer: KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("synced", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Boolean {
        val string = decoder.decodeString()
        return if (string.toInt() == 1) true else false
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        val string = if (value) 1 else 0
        encoder.encodeString(string.toString())
    }

}

val bookmarks = listOf(
    Bookmark(
        id = -1249962991056893400,
        rawUrl = "https://github.com/SmartToolFactory/Compose-Colorful-Sliders?utm_campaign=jetc.dev%20Newsletter&utm_medium=email&utm_source=Revue%20newsletter",
        url = "https://github.com/SmartToolFactory/Compose-Colorful-Sliders",
        title = "GitHub - SmartToolFactory/Compose-Colorful-Sliders: 🚀🌈 😍 Colorful Sliders written with Jetpack Compose that enliven default sliders with track and thumb dimensions, and gradient colors, borders, labels on top or at the bottom move with thumb and ColorfulIconSlider that can display emoji or any Composable as thumb",
        desc = "🚀🌈 😍 Colorful Sliders written with Jetpack Compose that enliven default sliders with track and thumb dimensions, and gradient colors, borders, labels on top or at the bottom move with thumb and ColorfulIconSlider that can display emoji or any Composable as thumb - GitHub - SmartToolFactory/Compose-Colorful-Sliders: 🚀🌈 😍 Colorful Sliders written with Jetpack Compose that enliven default sliders with track and thumb dimensions, and gradient colors, borders, labels on top or at the bottom move with thumb and ColorfulIconSlider that can display emoji or any Composable as thumb",
        imageUrl = "https://opengraph.githubassets.com/bab685570a982eed64b877db7ba3f18e746010fcafdf7881289549ea3e277933/SmartToolFactory/Compose-Colorful-Sliders",
        name = "GitHub",
        mediaType = "object",
        favIcon = "https://github.githubassets.com/favicons/favicon.svg",
        folderId = 5,
        creationDate = 1664828064007,
        synced = true
    ),
    Bookmark(
        id = 5327466503284867000,
        rawUrl = "https://github.com/pChochura/richtext-compose",
        url = "https://github.com/pChochura/richtext-compose",
        title = "GitHub - pChochura/richtext-compose: An all-in-one Jetpack Compose component to handle text styling inside TextFields",
        desc = "An all-in-one Jetpack Compose component to handle text styling inside TextFields - GitHub - pChochura/richtext-compose: An all-in-one Jetpack Compose component to handle text styling inside TextFields",
        imageUrl = "https://opengraph.githubassets.com/fde73995f0420d7e84b588ba83e7d4a4f68070d53dc3c39bdcaf3791bc35697b/pChochura/richtext-compose",
        name = "GitHub",
        mediaType = "object",
        favIcon = "https://github.githubassets.com/favicons/favicon.svg",
        folderId = 5,
        creationDate = 1664828064880,
        synced = true
    ),
    Bookmark(
        id = 8019278867914574000,
        rawUrl = "https://github.com/BMukhtar/linear-gradient-any-angle",
        url = "https://github.com/BMukhtar/linear-gradient-any-angle",
        title = "GitHub - BMukhtar/linear-gradient-any-angle: Linear gradient implementation with any angle for Android",
        desc = "Linear gradient implementation with any angle for Android - GitHub - BMukhtar/linear-gradient-any-angle: Linear gradient implementation with any angle for Android",
        imageUrl = "https://opengraph.githubassets.com/50ec9bfbd6c1dccdf36847b9b494097a5481a2fb47cbb811b979d2f76c336804/BMukhtar/linear-gradient-any-angle",
        name = "GitHub",
        mediaType = "object",
        favIcon = "https://github.githubassets.com/favicons/favicon.svg",
        folderId = 5,
        creationDate = 1664828065910,
        synced = true
    ),
    Bookmark(
        id = -175371738505786850,
        rawUrl = "https://github.com/hi-manshu/Kalendar?utm_campaign=jetc.dev%20Newsletter&utm_medium=email&utm_source=Revue%20newsletter",
        url = "https://github.com/hi-manshu/Kalendar",
        title = "GitHub - hi-manshu/Kalendar: An Elementary Compose Calendar",
        desc = "An Elementary Compose Calendar. Contribute to hi-manshu/Kalendar development by creating an account on GitHub.",
        imageUrl = "https://repository-images.githubusercontent.com/448952507/3fc4ea9b-ffa0-4071-b30d-c51729b52e5e",
        name = "GitHub",
        mediaType = "object",
        favIcon = "https://github.githubassets.com/favicons/favicon.svg",
        folderId = 5,
        creationDate = 1664828066631,
        synced = true
    ),
    Bookmark(
        id = 726747585210764400,
        rawUrl = "https://github.com/joaopegoraro/ComposePdfViewer?utm_campaign=jetc.dev%20Newsletter&utm_medium=email&utm_source=Revue%20newsletter",
        url = "https://github.com/joaopegoraro/ComposePdfViewer",
        title = "GitHub - joaopegoraro/ComposePdfViewer: A simple PDF Viewer for Jetpack Compose",
        desc = "A simple PDF Viewer for Jetpack Compose. Contribute to joaopegoraro/ComposePdfViewer development by creating an account on GitHub.",
        imageUrl = "https://opengraph.githubassets.com/dd53606000b8d24ccb2272b28b8ad4a417d83fef63bbf35dd3c0fa0574623114/joaopegoraro/ComposePdfViewer",
        name = "GitHub",
        mediaType = "object",
        favIcon = "https://github.githubassets.com/favicons/favicon.svg",
        folderId = 5,
        creationDate = 1664828068624,
        synced = true
    ),
    Bookmark(
        id = 1542284625108290800,
        rawUrl = "https://github.com/mohsenoid/SvgToCompose?utm_campaign=jetc.dev%20Newsletter&utm_medium=email&utm_source=Revue%20newsletter",
        url = "https://github.com/mohsenoid/SvgToCompose",
        title = "GitHub - mohsenoid/SvgToCompose: SVG path to Jetpack Compose tool",
        desc = "SVG path to Jetpack Compose tool. Contribute to mohsenoid/SvgToCompose development by creating an account on GitHub.",
        imageUrl = "https://opengraph.githubassets.com/c5c70e43d288681213f2abdd28e712fbf27db8689d7ac6ac64405eba3b50ee85/mohsenoid/SvgToCompose",
        name = "GitHub",
        mediaType = "object",
        favIcon = "https://github.githubassets.com/favicons/favicon.svg",
        folderId = 5,
        creationDate = 1664828069362,
        synced = true
    ),
)

val folders: List<BookmarkFolder> = listOf(
    BookmarkFolder(
        id = 5,
        name = "Home",
        parentId = 1
    )
)