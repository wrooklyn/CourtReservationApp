package it.polito.mad.courtreservationapp.views.homeManager

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity

@Composable
fun LocationFilter(
    ctx: MainActivity,
    viewModel: SportCenterViewModel,
    changeFragment:() -> Unit
) {

    var isPopupOpen = viewModel.isPopupOpen.observeAsState()
    Box(
        modifier = Modifier
            .width(25.dp)
            .height(25.dp),
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.red_button)),
            onClick = {
                viewModel.isPopupOpen.value = true
            }) {
            Icon(Icons.Default.Filter, "Add")
        }
    }

    if (isPopupOpen?.value == true) {
        MyPopup(viewModel, changeFragment)
    }
}

@Composable
fun MyPopup(viewModel: SportCenterViewModel, changeFragment:() -> Unit) {
    var initialValue= viewModel.distanceFilterValue
    var sliderValue by remember { mutableStateOf(initialValue?.toInt()?:1) }

    AlertDialog(
        onDismissRequest = {
            viewModel.isPopupOpen.value = false
        },
        title = { Text("Look around you") },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.isPopupOpen.value = false
                    viewModel.distanceFilterValue = sliderValue.toDouble()
                    changeFragment()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    viewModel.isPopupOpen.value = false
                    viewModel.distanceFilterValue = null
                    changeFragment()
                }
            ) {
                Text("Reset")
            }
        },

        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Search in a radius of $sliderValue KM")
                Slider(
                    value = sliderValue.toFloat(),
                    onValueChange = { value -> sliderValue = value.toInt() },
                    valueRange = 1f..50f,
                    steps = 50
                )
            }
        }
    )
}