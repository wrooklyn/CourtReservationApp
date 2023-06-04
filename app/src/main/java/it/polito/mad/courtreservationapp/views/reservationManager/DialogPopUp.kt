package it.polito.mad.courtreservationapp.views.reservationManager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.courtreservationapp.views.InterSemiBold
import it.polito.mad.courtreservationapp.views.MainActivity

@Composable
fun DialogSection(
    ctx: MainActivity,
    friends: MutableState<List<String>>,
    isDialogVisible: MutableState<Boolean>,
){

    val friendsPlaceHolder = listOf<String>("test1@gmail.com", "test2@gmail.com", "test3@gmail.com")
    AlertDialog(
        onDismissRequest = {
            isDialogVisible.value = false
        },
        shape = RoundedCornerShape(size = 12.dp),
        title = {
            PopUpContent(ctx, friends, isDialogVisible)
        },
        containerColor = Color.White,
        confirmButton = {

        },
    )
}

@Composable
fun PopUpContent(
    ctx: MainActivity,
    friends: MutableState<List<String>>,
    isDialogVisible: MutableState<Boolean>,
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState()))
        {
            Text(
                text = "Friend List",
                fontFamily = InterSemiBold,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            FriendToAddSection(ctx, friends)
            Text(
                text = "Friends Added",
                fontFamily = InterSemiBold,
                modifier = Modifier.padding(top = 5.dp, bottom = 15.dp)
            )
            FriendListSection(ctx, friends)

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.Center,){
                OutlinedButton(
                    border = BorderStroke(2.dp, Color(0xFFF16E64)),
                    onClick = {
                        isDialogVisible.value = false
                    },
                ) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFFF16E64),
                        fontSize = 15.sp,
                        fontFamily = InterSemiBold,
                    )
                }
                Spacer(modifier = Modifier.width(35.dp))
                Button(
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color(0xFFF16E64)),
                    onClick = {
                        isDialogVisible.value = false
                    }
                ) {
                    Text(text = "Confirm",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontFamily = InterSemiBold,)
                }
            }

        }

}

