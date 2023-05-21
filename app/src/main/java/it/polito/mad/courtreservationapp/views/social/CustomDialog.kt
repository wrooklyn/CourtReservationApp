package it.polito.mad.courtreservationapp.views.social

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import it.polito.mad.courtreservationapp.R

@Composable
fun CustomDialog(onSendFriendRequest: (String) -> Unit, onDismiss: () -> Unit) {

    var username by remember { mutableStateOf(TextFieldValue()) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "Enter here the username"
            )
        },
        text = {
            Column {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = Color.DarkGray,) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = colorResource(id = R.color.fox_skin),
                        focusedIndicatorColor =  colorResource(id = R.color.red_button),
                        unfocusedIndicatorColor =  Color.LightGray,
                        disabledIndicatorColor = Color.Gray,
                        cursorColor = Color.DarkGray,
                    ),
                )

            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.red_button),
                    contentColor = Color.White,
                ),
                onClick = {
                    onSendFriendRequest(username.text)
                    onDismiss()
                }
            ) {
                Text(text = "Send Request")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismiss() }) {
                Text(text = "Cancel", color = colorResource(id = R.color.red_button))
            }
        }
    )
}