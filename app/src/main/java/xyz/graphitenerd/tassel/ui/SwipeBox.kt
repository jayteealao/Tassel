package xyz.graphitenerd.tassel.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBox(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()

    var icon = remember { mutableStateOf(Icons.Outlined.Delete) }
    var alignment = remember { mutableStateOf(Alignment.CenterEnd) }
    var color = remember { mutableStateOf(Color.Red) }

//    lateinit var icon: ImageVector
//    lateinit var alignment: Alignment
//    val color: Color

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> {
            icon.value = Icons.Outlined.Delete
            alignment.value = Alignment.CenterEnd
            color.value = MaterialTheme.colorScheme.errorContainer
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon.value = Icons.Outlined.Edit
            alignment.value = Alignment.CenterStart
            color.value = Color.Green.copy(alpha = 0.3f)
        }

        SwipeToDismissBoxValue.Settled -> {
            icon.value = Icons.Outlined.Delete
            alignment.value = Alignment.CenterEnd
            color.value = MaterialTheme.colorScheme.errorContainer
        }
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onDelete()
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState) {
                onEdit()
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.Settled -> {
        }
    }

    SwipeToDismissBox(
        modifier = modifier.animateContentSize(),
        state = swipeState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                contentAlignment = alignment.value,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.value)
            ) {
                Icon(
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    imageVector = icon.value, contentDescription = null
                )
            }
        }
    ) {
        Box(Modifier.wrapContentHeight()){ content() }
    }
}
