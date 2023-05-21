package it.polito.mad.courtreservationapp.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportMasteryWithName
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName
import it.polito.mad.courtreservationapp.views.profile.AddEditInterestActivity


@Composable
fun AchievementSection(
    ctx: MainActivity,
    userWithSportMasteriesAndName: UserWithSportMasteriesAndName
){
//    val sportsPlaceHolder = listOf<String>("Soccer", "Tennis", "Ice Skate")

    val imageId = mapOf("soccer" to R.drawable.soccer_ball,
        "volley" to R.drawable.volleyball,
        "hocky" to R.drawable.hockey,
        "basketball" to R.drawable.basketball_icon,
        "tennis" to R.drawable.tennis,
        "iceskate" to R.drawable.ice_skate,
        "rugby" to R.drawable.rugby
    )

    var mainLL: ConstraintLayout? = ctx?.findViewById(R.id.mainLL)
    Column(modifier = Modifier
        .fillMaxWidth()

        .padding(30.dp, 10.dp)) {
        for(s in userWithSportMasteriesAndName.masteries){
            imageId[s.sport.name]?.let { sportCard(s, it, ctx, mainLL) }
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red_button)),
            onClick = {

            val interestId = 1L
            val intent = Intent(ctx, AddEditInterestActivity::class.java)
            intent.putExtra("email", userWithSportMasteriesAndName.user.email)
            ctx.registerForActivity.launch(intent)
        }) {
            Icon(Icons.Default.Add, "Add")
        }
    }
}


val InterSemiBold = FontFamily(
    Font(R.font.inter_semibold, FontWeight.Normal)
)
val InterRegular = FontFamily(
    Font(R.font.inter, FontWeight.Normal)
)

private fun mapMastery(level: Int): String{
    return when(level){
        0 -> "Beginner"
        1 -> "Intermediate"
        2 -> "Expert"
        3 -> "Professional"
        else -> "Error"
    }

}
@SuppressLint("MissingInflatedId")
@Composable
fun sportCard(sport: SportMasteryWithName, imageId: Int, ctx: FragmentActivity?, mainLL: ConstraintLayout?){

    androidx.compose.material.Card(
        elevation = 4.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(size = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
            ) {

                val inflater =
                    ctx?.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                var popupView = inflater.inflate(R.layout.popup_details_achievements, null)
                val width = LinearLayout.LayoutParams.WRAP_CONTENT
                val height = LinearLayout.LayoutParams.WRAP_CONTENT
                val focusable = true // lets taps outside the popup also dismiss it
                var popupWindow = PopupWindow(popupView, width, height, focusable)
                val closeButton = popupView.findViewById<Button>(R.id.closeButtonAchievements)

                popupView
                    .findViewById<ImageView>(R.id.achievementImageView)
                    .setImageResource(imageId)
                popupView.findViewById<TextView>(R.id.mastery_level).text =
                    "${sport.sport.name.uppercase()} - ${mapMastery(sport.sportMastery.level)}"
                popupView.findViewById<TextView>(R.id.achievementsTV).text =
                    sport.sportMastery.achievement
                popupWindow.showAtLocation(mainLL, Gravity.CENTER, 0, 0)
                mainLL?.foreground?.alpha = 160

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
                    text = "${sport.sport.name} - ${mapMastery(sport.sportMastery.level)} ",
                    fontSize = 19.sp,
                    fontFamily = InterSemiBold

                )
                Spacer(modifier = Modifier.height(height = 5.dp)) // gap between image and text
                Text(
                    text = sport.sportMastery.achievement ?: "",
                    fontSize = 14.sp,
                    fontFamily = InterRegular,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )

            }

        }
    }
}