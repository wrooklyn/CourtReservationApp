package it.polito.mad.courtreservationapp.views.social


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.More
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.courtreservationapp.R


@Composable
fun ExpandableListTile(
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    title: String,
    content: @Composable (() -> Unit),
) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp
    ) {
        Column() {
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
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(start = paddingValue)) {
                    Text(text = title,
                        fontFamily = FontFamily(Font(R.font.inter_semibold)),
                        fontSize = 15.sp,)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = "Game Invitation",
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontSize = 13.sp,)
                }


                Box(modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        isExpanded = !isExpanded
                    }, ) {
                    Row() {
                        if (trailing != null) {
                            trailing()
                        }
                        if(isExpanded){
                            Icon(Icons.Default.ExpandLess, "show more")
                        }else{
                            Icon(Icons.Default.ExpandMore, "show more")
                        }
                    }


                }

            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)){
                if (isExpanded) {
                    Box(modifier = Modifier.padding(bottom = 16.dp)) {
                        content()
                    }
                }
            }
        }

    }
}