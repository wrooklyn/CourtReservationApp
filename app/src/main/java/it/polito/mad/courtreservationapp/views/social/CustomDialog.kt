package it.polito.mad.courtreservationapp.views.social

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import it.polito.mad.courtreservationapp.R

@Composable
fun CustomDialog(onSendFriendRequest: (String) -> Unit, onDismiss: () -> Unit) {
    var mail by remember { mutableStateOf(TextFieldValue()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.roboto_medium)),
                        fontSize = 18.sp
                    ),
                    text = "Enter here the email",
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                OutlinedTextField(
                    value = mail,
                    onValueChange = { mail = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.deep_blue),
                        cursorColor = colorResource(id = R.color.deep_blue),
                        leadingIconColor = colorResource(id = R.color.deep_blue),
                        focusedLabelColor = colorResource(id = R.color.deep_blue)
                    )
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.red_button),
                            contentColor = Color.White
                        ),
                        onClick = {
                            onSendFriendRequest(mail.text)
                            onDismiss()
                        }
                    ) {
                        Text(text = "Send Request")
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    OutlinedButton(
                        onClick = { onDismiss() }) {
                        Text(
                            text = "Cancel",
                            color = colorResource(id = R.color.red_button)
                        )
                    }
                }
            }
        }
    }
}
