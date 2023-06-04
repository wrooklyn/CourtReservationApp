package it.polito.mad.courtreservationapp.views.reservationManager

import it.polito.mad.courtreservationapp.views.MainActivity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.ShapeLine
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.InterRegular

@Composable
fun FriendListSection(
    ctx: MainActivity,
    friends: MutableState<List<String>>,
){
    Column(modifier = Modifier
        .fillMaxWidth()) {
        if(friends.value.isNotEmpty()){
            for(s in friends.value){
                AddedFriendCard(s, friends)
                Spacer(modifier = Modifier.height(15.dp))
            }
        }else{
            Text(
                text="No friends added yet.",
                fontSize = 15.sp,
                fontFamily = InterRegular,
                modifier = Modifier.padding(bottom = 15.dp)
            )
        }
    }
}

@Composable
fun AddedFriendCard(friendEmail: String, friends: MutableState<List<String>>){

    androidx.compose.material.Card(
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(size = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Image(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(size = 25.dp),
                painter = painterResource(id = R.drawable.default_pfp),
                contentDescription = "pfp image",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(width = 8.dp)) // gap between image and text
            Text(
                text = friendEmail,
                fontSize = 15.sp,
                fontFamily = InterRegular,
                modifier = Modifier.weight(1f)  // This will push FloatingActionButton to the right
            )

            Box(modifier = Modifier.size(20.dp)){
                androidx.compose.material.FloatingActionButton(
                    backgroundColor = colorResource(id = R.color.red_button),
                    onClick = {
                        friends.value = friends.value - friendEmail
                    }) {
                    androidx.compose.material.Icon(Icons.Default.HorizontalRule, "Delete", tint = Color.White)
                }
            }
        }
    }
}
