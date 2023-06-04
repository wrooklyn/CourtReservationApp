package it.polito.mad.courtreservationapp.views.homeManager

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment.Companion.getUserLocation

const val LOCATION_PERMISSION_REQUEST_CODE = 1001
@Composable
fun LocationFilter(
    ctx: MainActivity,
    viewModel: SportCenterViewModel,
    isPopupOpen: MutableState<Boolean>,
    changeFragment:() -> Unit
) {

    if (isPopupOpen.value) {
        MyPopup(viewModel, changeFragment, ctx, isPopupOpen)
        if (ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permissions if they have not been granted
            ctx.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions have already been granted, proceed with getting the location
            getUserLocation(ctx)
        }
    }
}



@Composable
fun MyPopup(viewModel: SportCenterViewModel, changeFragment:() -> Unit, ctx: MainActivity, isPopupOpen: MutableState<Boolean>) {
    var initialValue= viewModel.distanceFilterValue
    var sliderValue by remember { mutableStateOf(initialValue?.toInt()?:1) }

    AlertDialog(
        onDismissRequest = {
            isPopupOpen.value = false
        },
        containerColor = Color.White,
        title = { Text("Look around you") },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(Color(0xFFF16E64)),
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            ctx,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // not granted
                        null
                    }else{
                        // granted
                        getUserLocation(ctx){
                            isPopupOpen.value = false
                            viewModel.distanceFilterValue = sliderValue.toDouble()
                            changeFragment()
                        }
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(
                border = BorderStroke(2.dp, Color(0xFFF16E64)),
                onClick = {
                    isPopupOpen.value = false
                    viewModel.distanceFilterValue = null
                    changeFragment()
                }
            ) {
                Text(text="Reset",
                    color = Color(0xFFF16E64),
                )
            }
        },

        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Search in a radius of $sliderValue KM")
                Slider(
                    colors = SliderDefaults.colors(
                        thumbColor = colorResource(id = R.color.red_button),
                        activeTrackColor = colorResource(id = R.color.red_button),
                    ),
                    value = sliderValue.toFloat(),
                    onValueChange = { value -> sliderValue = value.toInt() },
                    valueRange = 1f..50f,
                    steps = 50
                )
            }
        }
    )
}