package it.polito.mad.courtreservationapp.views.social

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.polito.mad.courtreservationapp.R

@Composable
fun ListTile(
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    title: String,
    subtitle: String? = null
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp).border(1.dp, Color.LightGray, RoundedCornerShape(15)),
        // backgroundColor = colorResource(id = R.color.red_button),
        //contentColor = Color.White

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            if (leading != null) {
                val background= colorResource(id = R.color.light_blue_background)
                val borderColor = Color.LightGray
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    //leading()

                    Image(
                        painter = painterResource(R.drawable.profile), // Replace "image.png" with your asset file name and extension
                        contentDescription = "Asset Image",
                        modifier = Modifier
                            .size(25.dp)
                            .drawBehind {
                                val radius = size.width.coerceAtMost(size.height) / 2 + 15
                                drawCircle(color = borderColor, radius = radius+2)
                                drawCircle(color = background, radius = radius)
                            },

                        )
                }
            }
            val paddingValue= if (leading!=null) 8.dp else 0.dp
            Column(modifier = Modifier.weight(1f).padding(start = paddingValue)) {
                Text(text = title, style = MaterialTheme.typography.subtitle1)

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