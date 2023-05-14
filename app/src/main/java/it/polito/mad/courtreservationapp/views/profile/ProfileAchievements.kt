package it.polito.mad.courtreservationapp.views

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.profile.AddEditInterestActivity
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import it.polito.mad.courtreservationapp.views.reservationManager.BrowseReservationsFragment
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity
import it.polito.mad.courtreservationapp.views.ui.theme.CourtReservationAppTheme
import org.w3c.dom.Text


@Composable
fun AchievementSection(ctx : FragmentActivity?){
    val sportsPlaceHolder = listOf<String>("Soccer", "Tennis", "Ice Skate")
    val imageId = mapOf("Soccer" to R.drawable.soccer_ball, "Tennis" to R.drawable.tennis, "Ice Skate" to R.drawable.ice_skate)
    var mainLL: ConstraintLayout? = ctx?.findViewById(R.id.mainLL)
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(30.dp)) {
        for(s in sportsPlaceHolder){
            imageId[s]?.let { sportCard(s, it, ctx, mainLL) }
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
@SuppressLint("MissingInflatedId")
@Composable
fun sportCard(sport: String, imageId: Int, ctx: FragmentActivity?, mainLL: ConstraintLayout?){

    androidx.compose.material.Card(
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(size = 12.dp),
        modifier = Modifier.fillMaxWidth().clickable( interactionSource = MutableInteractionSource(),
            indication = null,){

            val inflater = ctx?.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var popupView = inflater.inflate(R.layout.popup_details_achievements, null)
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it
            var popupWindow = PopupWindow(popupView, width, height, focusable)
            val closeButton = popupView.findViewById<Button>(R.id.closeButtonAchievements)

            popupView.findViewById<ImageView>(R.id.achievementImageView).setImageResource(imageId)
            popupView.findViewById<TextView>(R.id.mastery_level).text="$sport - Beginner"
            popupView.findViewById<TextView>(R.id.achievementsTV).text="Scored the decisive goal in the final minute of the championship match, securing our team's first regional title in over a decade. This achievement was the culmination of years of rigorous training, team coordination, and unwavering determination."
            popupWindow.showAtLocation(mainLL, Gravity.CENTER, 0, 0)
            mainLL?.foreground?.alpha  = 160

            popupWindow.setOnDismissListener {
                mainLL?.foreground?.alpha = 0
            }

            closeButton.setOnClickListener {
                popupWindow.dismiss()
            }
        }
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
                    text = "$sport - Beginner",
                    fontSize = 19.sp,
                    fontFamily = InterSemiBold

                )
                Spacer(modifier = Modifier.height(height = 5.dp)) // gap between image and text
                Text(
                    text = "Scored the decisive goal in the final minute of the championship match, securing our team's first regional title in over a decade. This achievement was the culmination of years of rigorous training, team coordination, and unwavering determination.",
                    fontSize = 14.sp,
                    fontFamily = InterRegular,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )

            }

        }
    }
}