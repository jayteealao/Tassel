package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.graphitenerd.tassel.R

@Composable
fun HomeAppBar(onClickTasselButton: () -> Unit) {

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
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "",
                    tint = Color.Black
                )
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "Tassels",
                color = Color.Black,
                style = MaterialTheme.typography.h4
            )

            IconButton(onClick = onClickTasselButton) {
                Icon(
                    painter = painterResource(id = R.drawable.tassel_app_icon),
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
fun SearchBar() {

    var text by remember { mutableStateOf("") }
    var value = ""
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)

    Card(
        shape = RoundedCornerShape(50, 50, 50, 50),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValueState = it
                    if (value != it.text) {
                        value = text
                    }
                },
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
                    .fillMaxHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White
                )

            )
        }
    }
}

@Preview
@Composable
fun previewAppBar() {
    HomeAppBar(onClickTasselButton = {})
}

@Preview
@Composable
fun previewSearchBar() {
    SearchBar()
}
