package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.graphitenerd.tassel.R

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

@Composable
fun SearchBar() {

    OutlinedTextField(
        value = "",
        onValueChange = { },
        placeholder = {
            Text(
                text = "Search",
                modifier = Modifier.wrapContentHeight(),
                style = MaterialTheme.typography.body2
            )
        },
        trailingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
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
}

@Preview
@Composable
fun previewAppBar() {
    HomeAppBar(onClickActionButton = {})
}

@Preview
@Composable
fun previewSearchBar() {
    SearchBar()
}
