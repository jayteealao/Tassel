package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.boguszpawlowski.chassis.Chassis
import xyz.graphitenerd.tassel.model.BookMarkForm

@Composable
fun OutlinedTextFieldWithLabel(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        Text(
            text = label,
            modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)
        )
        Card(
            shape = RoundedCornerShape(50, 50, 50, 50),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White
                )
            )
        }
    }
}

@Composable
fun Content(
    titleValue: String = "",
    onTitleValueChange: (String) -> Unit = {},
    addressValue: String = "",
    onAddressValueChange: (String) -> Unit = {},
    onAcceptButton: () -> Unit = {}
) {

//    TODO consider encapsulating parameters in a remember hook
    Card(modifier = Modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Add New Bookmark",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            Divider(color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextFieldWithLabel(
                value = titleValue,
                onValueChange = onTitleValueChange,
                label = "Title",
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextFieldWithLabel(
                value = addressValue,
                onValueChange = onAddressValueChange,
                label = "Address",
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAcceptButton,
                modifier = Modifier.padding(bottom = 16.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.DarkGray,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(40.dp, 0.dp)
            ) {
                Text(text = "Add Bookmark")
            }
        }
    }
}

@Composable
fun AddBookmark(onAccept: () -> Unit, formState: Chassis<BookMarkForm>) {

    val bookmarkForm = formState.state.collectAsState()
    Content(
        titleValue = bookmarkForm.value.title.value ?: "",
        onTitleValueChange = { formState.update(BookMarkForm::title, it) },
        addressValue = bookmarkForm.value.address.value ?: "",
        onAddressValueChange = { formState.update(BookMarkForm::address, it) },
        onAcceptButton = onAccept
    )
}

@Composable
fun BetterTextField() {
    BasicTextField(
        "",
        {},
        modifier = Modifier,
        cursorBrush = SolidColor(Color.Black)
    ) { innerTextField ->
        Card(
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(25),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 60.dp),
        ) {
            innerTextField()
        }
    }
}

@Preview
@Composable
fun prevAB() {
    Content()
}

@Preview
@Composable
fun prevBTF() {
    BetterTextField()
}
