package it.polito.mad.courtreservationapp.views

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.profile.AddEditInterestActivity
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import it.polito.mad.courtreservationapp.views.ui.theme.CourtReservationAppTheme



@Composable
fun AchievementSection(ctx : FragmentActivity?){
    val sportsPlaceHolder = listOf<String>("Soccer", "Tennis", "Ice Skate")
    val imageId = mapOf("Soccer" to R.drawable.soccer_ball, "Tennis" to R.drawable.tennis, "Ice Skate" to R.drawable.ice_skate)
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp)) {
        for(s in sportsPlaceHolder){
            imageId[s]?.let { sportCard(s, it) }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(onClick = {
            val interestId = 1L
            val intent = Intent(ctx, AddEditInterestActivity::class.java)
            intent.putExtra("sportCenterId", 1L)
            ctx?.startActivity(intent)
        }) {
            Text("+")
        }
    }
}


val InterSemiBold = FontFamily(
    Font(R.font.inter_semibold, FontWeight.Normal)
)
val InterRegular = FontFamily(
    Font(R.font.inter, FontWeight.Normal)
)
@Composable
fun sportCard(sport: String, imageId: Int){

    androidx.compose.material.Card(
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(size = 12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Image(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(size = 62.dp),
                painter = painterResource(id = imageId),
                contentDescription = "sport interest  image",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(width = 8.dp)) // gap between image and text
            Column(){
                Text(
                    text = "$sport - Level 5",
                    fontSize = 19.sp,
                    fontFamily = InterSemiBold

                )
                Spacer(modifier = Modifier.height(height = 5.dp)) // gap between image and text
                Text(
                    text = "Won 1st place in local 5k race.",
                    fontSize = 14.sp,
                    fontFamily = InterRegular

                )

            }

        }
    }
}