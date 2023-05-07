package it.polito.mad.courtreservationapp.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
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
    val sportsPlaceHolder = listOf<String>("Calcio", "Tennis", "Ping Pong")
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp)) {
        for(s in sportsPlaceHolder){
            Text(s)
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
