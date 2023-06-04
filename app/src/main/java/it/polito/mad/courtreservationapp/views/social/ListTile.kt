package it.polito.mad.courtreservationapp.views.social

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.courtreservationapp.R


@Composable
fun ListTile(
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    request: Boolean
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            if (leading != null) {
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    Image(
                        painter = painterResource(R.drawable.defaultprofile), // Replace "profile" with your image name in the drawable folder
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp) // Change size as per your need
                            .clip(CircleShape)
                    )
                }
            }
            val paddingValue= if (leading!=null) 8.dp else 0.dp
            Column(modifier = Modifier.weight(1f).padding(start = paddingValue)) {
                if(request){
                    Text(text = title,
                        fontFamily = FontFamily(Font(R.font.inter_semibold)),
                        fontSize = 15.sp,)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Friend Request",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 13.sp,)
                }else{
                    Text(text = title,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 20.sp,)

                }

                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.body2)
                }
            }

            if (trailing != null) {
                Box(modifier = Modifier.padding(start = 16.dp)) {
                    trailing()
                }
            }
        }
    }
}