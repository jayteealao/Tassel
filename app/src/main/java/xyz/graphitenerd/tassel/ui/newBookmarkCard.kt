package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.boguszpawlowski.chassis.Chassis
import xyz.graphitenerd.tassel.model.BookMarkForm
import xyz.graphitenerd.tassel.model.Bookmark
import java.net.Inet4Address

@Composable
fun OutlinedTextFieldWithLabel(value: String, onValueChange: (String) -> Unit, label: String) {

    Column(){
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Add New Bookmark")
            Divider(color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextFieldWithLabel(
                value = titleValue,
                onValueChange = onTitleValueChange,
                label = "Title"
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextFieldWithLabel(
                value = addressValue,
                onValueChange = onAddressValueChange,
                label = "Address"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onAcceptButton,
                modifier = Modifier,
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
fun AddBookmark(shouldDialogShow:Boolean = false, toggleShow: () -> Unit, onAccept: () -> Unit, formState: Chassis<BookMarkForm>) {

    val bookmarkForm = formState.state.collectAsState()
    if (shouldDialogShow) {
        Dialog(onDismissRequest = toggleShow) {
            Content(
                titleValue = bookmarkForm.value.title.value ?: "",
                onTitleValueChange = { formState.update(BookMarkForm::title, it) },
                addressValue = bookmarkForm.value.address.value ?: "",
                onAddressValueChange = { formState.update(BookMarkForm::address, it) },
                onAcceptButton = onAccept
            )
        }
    }
}



@Preview
@Composable
fun prevAB() {
    Content()
}