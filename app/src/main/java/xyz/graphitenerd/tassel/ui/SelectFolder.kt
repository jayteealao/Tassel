package xyz.graphitenerd.tassel.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.node.CustomBranch
import cafe.adriel.bonsai.core.node.CustomLeaf
import cafe.adriel.bonsai.core.node.Node
import cafe.adriel.bonsai.core.tree.Tree
import cafe.adriel.bonsai.core.tree.TreeScope
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.graphitenerd.tassel.model.BookmarkViewModel
import javax.inject.Singleton

@Composable
fun SelectFolder(
//    bookmarkViewModel: BookmarkViewModel,
    selectedFolder: Folder,
    onSelect: (Node<Folder>) -> Unit = {},
    tree: Tree<Folder>,
) {

    var toggleVisibility by remember { mutableStateOf(false) }

//    val tree = bookmarkViewModel.folderTree.buildBonsaiTree()

    Column(
        modifier = Modifier

            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(max = 320.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleVisibility = !toggleVisibility }
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .paddingFromBaseline(top = 4.dp),
            text = "Select Folder: ${selectedFolder.folderName.uppercase()}")

        AnimatedVisibility(visible = toggleVisibility) {
            Bonsai(
                tree = tree,
                onClick = { node ->
                    tree.clearSelection()
                    tree.toggleExpansion(node)
                    onSelect(node)
                },
            )
        }
    }
}

@Singleton
data class Folder constructor(
    val folderName: String = "home",
    val folderId: Long = 1,
    var children: MutableList<Folder?> = mutableListOf(),
) {

    //    @Composable
    fun buildFolderTree(VM: BookmarkViewModel) {
        //todo: change to ??launchedeffect
//        val scope = rememberCoroutineScope()
        VM.viewModelScope.launch(Dispatchers.IO) {
            for (bookmarkFolder in VM.getFolderChildren(folderId)) {
                val childFolder = Folder(bookmarkFolder.name, bookmarkFolder.id)
                childFolder.buildFolderTree(VM)
                children.add(childFolder)
            }
        }
    }

    context(TreeScope)
    @Composable
    fun buildBranchAndLeaves() {
        if (!children.isEmpty()) {
            Log.d("bonsai tree count", "${children.size}")
            CustomBranch<Folder>(
                content = this@Folder,
                customIcon = { Icon(
                    imageVector = FontAwesomeIcons.Solid.Folder,
                    contentDescription = "folder icon"
                )},
                customContent = {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = it.content.folderName
                    )}
            ) {
                children.forEach { it?.buildBranchAndLeaves() }
            }
        } else {
            CustomLeaf(
                content = this@Folder,
                customIcon = { Icon(
                    imageVector = FontAwesomeIcons.Solid.Folder,
                    contentDescription = "folder icon"
                )}
            ) {
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = it.content.folderName)
            }
        }
    }
    @Composable
    fun buildBonsaiTree(): Tree<Folder> {
        return Tree {
            if (children.isEmpty()) {
                CustomLeaf(
                    content = this@Folder,
                    customIcon = { Icon(
                        imageVector = FontAwesomeIcons.Solid.Folder,
                        contentDescription = "folder icon"
                    )}
                ) {
                    Text(modifier = Modifier.padding(horizontal = 8.dp), text = it.content.folderName)
                }
            } else {
                this@Folder.buildBranchAndLeaves()
            }
        }
    }
}