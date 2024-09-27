package xyz.graphitenerd.tassel.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowDown
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Folder

data class FileTree(
    val folderName: String = "home",
    val folderId: Long = 1,
    val parent: FileTree? = null,
    val children: List<FileTree> = listOf(),
) {

    fun withChildren(childrenProvider: (FileTree) -> List<FileTree>): FileTree {
        val newChildren = childrenProvider(this)
        return copy(children = newChildren.map { it.copy(parent = this) })
    }
}

//TODO: design to use box, expand like a modal e.t.c
@Composable
fun FolderDropDownMenu(
    modifier: Modifier = Modifier,
    fileTree: FileTree = FileTree(), //current file tree should be passed down
    selectedFolder: FileTree? = null,
    isExpanded: Boolean = false,
    onExpand: () -> Unit = {},
    onNavigate: (FileTree) -> Unit = {}, // file tree should be updated here and children fetched
    onDismissRequest: () -> Unit = {},
    onFolderSelected: (FileTree) -> Unit = {}
) {

    val borderModifier = Modifier
        .fillMaxWidth()
        .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))



    AnimatedContent(
        targetState = isExpanded,
        modifier = modifier.wrapContentHeight() // should this be the container, probably
    ) { isExpanded ->

        val scrollState = rememberScrollState()

        if (!isExpanded) {
            FolderPreviewCard(modifier = borderModifier, fileTree = selectedFolder ?: fileTree) { onExpand() }
        } else {
            Dialog (
                onDismissRequest = onDismissRequest,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                )
            ) {
                Column(
                    modifier = borderModifier
                        .heightIn(max = 500.dp)
                        .verticalScroll(scrollState)
                        .background(Color.White)
                ) {
                    FolderSelectorCard(fileTree, isHeader = true,
                        onNavigate = onNavigate //hopefully, this should navigate back up
                    ) //header, should have a back button
                    HorizontalDivider()
                    fileTree.children.forEach { child ->
                        FolderSelectorCard(child,
                            isSelected = child.folderId == selectedFolder?.folderId, //wrong logic, selected isnt always current file
                            onNavigate = { onNavigate(it) },
                        ) {
                             onFolderSelected(child) // should this be child or fileTree?
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FolderSelectorCard(
    fileTree: FileTree,
    isHeader: Boolean = false,
    isSelected: Boolean = false,
    onNavigate: (FileTree) -> Unit = {},
    onSelect: (FileTree) -> Unit = {}
) {

    Card (
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        onClick = {
            if (!isHeader) {
                onSelect(fileTree)
            } else if (fileTree.parent != null) {
                Log.d("folder", "header clicked parent is ${fileTree.parent.folderName}")
                    onNavigate(fileTree.parent)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 4.dp, top = 8.dp)
                .then(if (isSelected && !isHeader) Modifier.background(Color.Green) else Modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            LeadingIcon(isHeader)

            FolderSelectorText(fileTree, isHeader)

            TrailingIcon(fileTree.children.isNotEmpty(), isHeader) {
                onNavigate(fileTree)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FolderPreviewCard(
    modifier: Modifier = Modifier,
    fileTree: FileTree,
    onClick: () -> Unit
) {
    Card (
        modifier = modifier.wrapContentHeight(),
        elevation = 0.dp,
        onClick = { onClick() }
    ){
        Row(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 4.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            LeadingIcon(false)

            FolderSelectorText(fileTree, false)

            TrailingIcon(fileTree.children.isNotEmpty(), true) {
            }
        }
    }
}

@Composable
private fun TrailingIcon(hasChildren: Boolean, isHeader: Boolean, onClick: () -> Unit = {}) {
    if (hasChildren && !isHeader) {
        IconButton(
            modifier = Modifier
                .width(56.dp)
                .padding(end = 8.dp),
            onClick = {
                onClick()
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = FontAwesomeIcons.Solid.ArrowDown,
                contentDescription = "Expand Folder Selector"
            )
        }
    }
}

@Composable
private fun RowScope.FolderSelectorText(fileTree: FileTree, isHeader: Boolean, ) {
        Text(
            text = fileTree.folderName,
            color = if (isHeader) Color.Gray else Color.Unspecified,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(0.8f)
        )
}

@Composable
private fun LeadingIcon(
    isHeader: Boolean = false,
) {
    val leadingIcon = if (isHeader) {
        FontAwesomeIcons.Solid.ArrowLeft
    } else {
        FontAwesomeIcons.Solid.Folder
    }

    Icon(
        imageVector = leadingIcon,
        contentDescription = "folder icon",
        modifier = Modifier
            .size(24.dp)
            .padding(start = 8.dp)
    )
}

@Preview
@Composable
private fun PrevClosed() {
    var selectedFolder by remember { mutableStateOf(FileTree()) }
    val currentFolder by remember {
        derivedStateOf {
            val data = selectedFolder.withChildren {
                if (it.folderId == 1L) {
                    listOf(
                        FileTree(folderName = "folder2", folderId = 2, children = listOf(
                            FileTree(folderName = "folder6", folderId = 6),
                            FileTree(folderName = "folder7", folderId = 7),
                            FileTree(folderName = "folder8", folderId = 8))),
                        FileTree(folderName = "folder3", folderId = 3),
                        FileTree(folderName = "folder4", folderId = 4),
                        FileTree(folderName = "folder5", folderId = 5))
                } else listOf()
            }
            selectedFolder
        }
    }

    var expand by remember { mutableStateOf(false) }

    Column {
        FolderDropDownMenu(
            fileTree = currentFolder,
            isExpanded = expand,
            onNavigate = {
                if (!expand) {
                    expand = true
                }
                if (expand) {
                    selectedFolder = it
                }


            },
            onDismissRequest = { expand = false },
        )
        Text(text = currentFolder.folderName)
        if (selectedFolder.children.isNotEmpty()) {
            selectedFolder.children[0].children.forEach {
                Text(text = it.folderName)
            }
        }

    }
}
