package xyz.graphitenerd.tassel.service

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.model.BookmarkFolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class StorageServiceImpl @Inject constructor(
    val firestore: FirebaseFirestore,
    val scope: CoroutineScope
) : StorageService {
    private var listenerRegistration: ListenerRegistration? = null

    private var userId: String? = null

    override fun setUserId(userId: String) {
//        Log.d("","userid set")
        this@StorageServiceImpl.userId = userId
        setBookmarksRef(userId)
        setFoldersRef(userId)
    }

    private var bookmarksRef: CollectionReference? = null
    private fun setBookmarksRef(userId: String) {
        bookmarksRef = firestore
            .collection(APPDATA_COLLECTION)
            .document(userId)
            .collection("Bookmarks")
    }

    private lateinit var foldersRef: CollectionReference
    private fun setFoldersRef(userId: String) {
//        Log.d("","foldersref created")
        foldersRef = firestore
            .collection(APPDATA_COLLECTION)
            .document(userId)
            .collection("Folders")
    }
    override fun isUserSet() = userId != null

    override fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Bookmark) -> Unit,
        onError: (Throwable) -> Unit
    ) {

        val query = firestore.collection(APPDATA_COLLECTION).document(userId).collection("Bookmarks").orderBy("creationDate")

        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            value?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == REMOVED
//                val task = it.document
                it.document.toObject<Bookmark>()
                val bookmark = it.document.toObject<Bookmark>()
                onDocumentEvent(wasDocumentDeleted, bookmark)
            }
        }
    }

    override fun removeListener() {
        listenerRegistration?.remove()
    }

    override suspend fun syncBookmarksToStorage(
        getLocalBookmark: (Long) -> List<Bookmark>,
        onError: (Throwable) -> Unit
    ) {
        var bookmark: Bookmark?
        bookmarksRef?.orderBy("creationDate", Query.Direction.DESCENDING)?.limit(1)?.get(Source.SERVER)?.addOnSuccessListener { querySnapshot ->
            bookmark = querySnapshot.toBookmarks().firstOrNull()
            scope.launch {
                getLocalBookmark(bookmark?.creationDate ?: 0).forEach {
                    bookmarksRef?.document(it.id.toString())?.set(it)
                }
            }
        }?.addOnFailureListener {
//            Log.d("sync", "error: ${it.message}")
        }
    }

    override suspend fun syncFoldersToCloud(folders: List<BookmarkFolder>) {
//        Log.d("","folder sync started")
        folders.forEach {
            scope.launch {
                foldersRef.document(it.id.toString()).set(it).addOnSuccessListener {
//                    Log.d("","folder synced")

                }
            }
        }
    }

    override suspend fun saveBookmark(bookmark: Bookmark, onSuccess: (Bookmark) -> Unit) {
        scope.launch {
            bookmarksRef?.document(bookmark.id.toString())?.set(bookmark)?.addOnSuccessListener {
                onSuccess(bookmark.copy(synced = true))
            }
        }
    }

    override suspend fun saveFolder(folder: BookmarkFolder) {
        scope.launch {
            foldersRef.document(folder.id.toString()).set(folder)
        }
    }


//    override fun getTask(
//        taskId: String,
//        onError: (Throwable) -> Unit,
//        onSuccess: (Bookmark) -> Unit
//    ) {
//        Firebase.firestore
//            .collection(TASK_COLLECTION)
//            .document(taskId)
//            .get()
//            .addOnFailureListener { error -> onError(error) }
//            .addOnSuccessListener { result ->
//                val task = result.toObject<Bookmark>()?.copy(id = result.id.toLong())
//                onSuccess(task ?: Bookmark(rawUrl = "error")) //TODO: fix
//            }
//    }

//    override fun saveTask(task: Task, onResult: (Throwable?) -> Unit) {
//        Firebase.firestore
//            .collection(TASK_COLLECTION)
//            .add(task)
//            .addOnCompleteListener { onResult(it.exception) }
//    }
//
//    override fun updateTask(task: Task, onResult: (Throwable?) -> Unit) {
//        Firebase.firestore
//            .collection(TASK_COLLECTION)
//            .document(task.id)
//            .set(task)
//            .addOnCompleteListener { onResult(it.exception) }
//    }
//
//    override fun deleteTask(taskId: String, onResult: (Throwable?) -> Unit) {
//        Firebase.firestore
//            .collection(TASK_COLLECTION)
//            .document(taskId)
//            .delete()
//            .addOnCompleteListener { onResult(it.exception) }
//    }
//
//    override fun deleteAllForUser(userId: String, onResult: (Throwable?) -> Unit) {
//        Firebase.firestore
//            .collection(TASK_COLLECTION)
//            .whereEqualTo(USER_ID, userId)
//            .get()
//            .addOnFailureListener { error -> onResult(error) }
//            .addOnSuccessListener { result ->
//                for (document in result) document.reference.delete()
//                onResult(null)
//            }
//    }
//
//    override fun updateUserId(
//        oldUserId: String,
//        newUserId: String,
//        onResult: (Throwable?) -> Unit
//    ) {
//        Firebase.firestore
//            .collection(TASK_COLLECTION)
//            .whereEqualTo(USER_ID, oldUserId)
//            .get()
//            .addOnFailureListener { error -> onResult(error) }
//            .addOnSuccessListener { result ->
//                for (document in result) document.reference.update(USER_ID, newUserId)
//                onResult(null)
//            }
//    }

    companion object {
        private const val APPDATA_COLLECTION = "UserData"
        private const val USER_ID = "userId"
    }
}

fun QuerySnapshot.toBookmarks(): List<Bookmark> {
    return this.documents.map {
        val data = it.data
        Bookmark(
            id = it.id.toLong(),
            rawUrl = data?.get("rawUrl").toString(),
            url = data?.get("url").toString(),
            title = data?.get("title").toString(),
            desc = data?.get("desc").toString(),
            imageUrl = data?.get("imageUrl").toString(),
            name = data?.get("name").toString(),
            mediaType = data?.get("mediaType").toString(),
            favIcon = data?.get("favIcon").toString(),
            folderId = data?.get("folderId").toString().toLong(),
            creationDate = data?.get("creationDate").toString().toLong(),
        )
    }
}