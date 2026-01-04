package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.graphitenerd.tassel.R
import xyz.graphitenerd.tassel.model.Bookmark
import xyz.graphitenerd.tassel.screens.recents.SearchScreenState

@Composable
fun HomeAppBar(
    actionIcon: Painter = painterResource(id = R.drawable.tassel_app_icon),
    onClickMenuButton: () -> Unit = {},
    onClickActionButton: () -> Unit,
) {

    TopAppBar(
        backgroundColor = Color.White,
        elevation = 0.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(1f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onClickMenuButton) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "",
                    tint = Color.Black,
                    modifier = Modifier.size(33.dp)
                )
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "Tassels",
                color = Color.Black,
                style = MaterialTheme.typography.h4
            )

            IconButton(onClick = onClickActionButton) {
                Icon(
                    painter = actionIcon,
//                    painter = painterResource(id = R.drawable.tassel_app_icon),
                    contentDescription = "",
                    tint = Color.Black,
                    modifier = Modifier.size(33.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasselSearchBar(searchScreenState: SearchScreenState = SearchScreenState()) {
    OutlinedTextField(
        value = searchScreenState.searchQuery,
        onValueChange = { searchScreenState.onQueryChange(it) },
        placeholder = {
            Text(
                text = "Search",
                modifier = Modifier.wrapContentHeight(),
                style = MaterialTheme.typography.body2
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
//                    searchScreenState.onQueryChange()
                    searchScreenState.searchBookmarks()
                    searchScreenState.toggleShowSearchResults(true)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = ""
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                searchScreenState.searchBookmarks()
                searchScreenState.toggleShowSearchResults(true)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = Color.White,
            unfocusedBorderColor = Color.Black
        ),
        shape = RoundedCornerShape(50),
        singleLine = true

    )
    
//    Box( modifier = Modifier
//        .fillMaxSize()
//        .semantics { isTraversalGroup = true } ) {
//        SearchBar(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .semantics { traversalIndex = 0f },
//            inputField = {
//                SearchBarDefaults.InputField(
//                    query = searchScreenState.searchQuery,
//                    onQueryChange = { searchScreenState.onQueryChange(it) },
//                    onSearch = { searchScreenState.onExpandedChange(false) },
//                    expanded = searchScreenState.isSearchActive,
//                    onExpandedChange = { searchScreenState.onExpandedChange(it) },
//                    placeholder = { Text("Search") },
//                )
//            },
//            expanded = searchScreenState.isSearchActive,
//            onExpandedChange = { searchScreenState.onExpandedChange(it) }
//        ) { }
//    }
}

@Composable
fun SearchContent(modifier: Modifier = Modifier, searchScreenState: SearchScreenState, bookmarks: List<Bookmark>) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(20.dp, 0.dp, 20.dp, 78.dp)
        ) {
        item {
            Column { // did this fix search bar not showing up?
                Divider(
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
                TasselSearchBar(searchScreenState) //TODO: shared content animation
                Spacer(modifier = Modifier.height(20.dp))
                Divider(
                    color = Color.Black
                )
            }
        }
        itemsIndexed(
            items=bookmarks,
            key = { i, bookmark -> bookmark.id }
        ) { i, bookmark ->
            Column { // did this fix divider not showing up?
                BookmarkCard(bookmark = bookmark)
                Divider(
                    color = Color.Black,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAppBar() {
    HomeAppBar(onClickActionButton = {})
}

@Preview
@Composable
fun PreviewSearchBar() {
    TasselSearchBar()
}
