package xyz.graphitenerd.tassel.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.data.FolderDao
import xyz.graphitenerd.tassel.model.BookmarkFolder
import xyz.graphitenerd.tassel.service.AccountService
import xyz.graphitenerd.tassel.service.StorageService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepository @Inject constructor(
    private val folderDao: FolderDao,
    private val storageService: StorageService,
    private val accountService: AccountService,
    private val scope: CoroutineScope,
) : IFolderRepository {

    init {
        if (accountService.hasUser() and !storageService.isUserSet()) {
//            Log.d("foldervm", "user will be set")
            storageService.setUserId(accountService.getUserId())
        }
    }

    override fun getFolderById(id: Long) = folderDao.getFolderById(id)

    override fun getFoldersByParentId(id: Long?) = folderDao.getFolderChildren(id)

    override fun getFolderByName(name: String) = folderDao.getFolderByName(name)

    override fun insertFolder(folder: BookmarkFolder) = folderDao.insertFolder(folder)

    override fun getFolders() = folderDao.getFolders()

    override fun syncFoldersToCloud() {
        if (!accountService.hasUser()) return
        scope.launch(Dispatchers.IO) {
            val folders = getFolders()
            storageService.syncFoldersToCloud(folders)
        }
    }

    override suspend fun saveAndSyncFolder(folder: BookmarkFolder) {
        insertFolder(folder)
        storageService.saveFolder(folder)
    }
}