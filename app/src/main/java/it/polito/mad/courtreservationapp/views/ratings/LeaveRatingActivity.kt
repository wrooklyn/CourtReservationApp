package it.polito.mad.courtreservationapp.views.ratings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.view_model.LeaveRatingViewModel
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import it.polito.mad.courtreservationapp.views.ratings.ui.theme.CourtReservationAppTheme
import kotlinx.coroutines.delay
import java.util.*
import kotlin.concurrent.schedule

class LeaveRatingActivity : ComponentActivity() {

    //ViewModel
    val viewModel by viewModels<LeaveRatingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.courtId = intent.getStringExtra("courtId")?:""
        viewModel.reservationId = intent.getStringExtra("reservationId")?:""
        viewModel.sportCenterId = intent.getStringExtra("sportCenterId")?:""
        viewModel.userId =
            SavedPreference.EMAIL
        viewModel.init(this)


        setContent {
            CourtReservationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PageLayout()
                }
            }
        }
    }


    @Composable
    fun PageLayout() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rate Your Experience") },
                    backgroundColor = Color.White,
                    navigationIcon = {
                        IconButton(onClick = { finish() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )


            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RectangleShape)
                ) {
                    var isSubmitting by remember { viewModel.isSubmitting }
                    Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red_button)),
                        onClick = {
                            isSubmitting = true
                            if (viewModel.selectedRating != null) {
                                Timer().schedule(1000) {
                                    viewModel.submitRating()
                                }
                            } else null
                            if (viewModel.selectedRating != null) {
                                val resultIntent = Intent()
                                resultIntent.putExtra("review_submitted", true)
                                setResult(Activity.RESULT_OK, resultIntent)
                                viewModel.submitRating()
                            } else null
                        }) {
                        if(isSubmitting)
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                        else Text(
                            "Submit",
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }

            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        colorResource(id = R.color.light_blue_background),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(padding)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 25.dp), elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        LeaveRatingCard()
                    }
                }
            }
        }
    }

    @Composable
    fun LeaveRatingCard() {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        val sportCenter by viewModel.sportCenterWithCourtsLiveData.observeAsState()
        val court = sportCenter?.courts?.first { court -> court.courtId == viewModel.courtId }
        val displayName =
           "${sportCenter?.sportCenter?.name}\n${court?.sportName} Court - ${viewModel.courtId}"


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "$displayName",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.red_hat_display_bold)),
                    fontSize = 30.sp,
                )
            )
            Text(
                "Are you satisfied with the service?",
                modifier = Modifier.padding(top = 4.dp),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.red_hat_display_medium)),
                    color = Color.Gray,
                    fontSize = 15.sp,
                )
            )
            StarRatingComponent()
            Divider()
            Text(
                "Tell us what can be improved?",
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.red_hat_display_bold)),
                    fontSize = 13.sp,
                )
            )
            statisticsFeedbacks()
            OutlinedTextField(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, RoundedCornerShape(5.dp)),
                value = text,
                onValueChange = { newText ->
                    viewModel.reviewText = newText.text
                    text = newText
                    println(text)
                    println(newText)

                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.red_button),
                    cursorColor = colorResource(id = R.color.red_button)
                ),
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun statisticsFeedbacks() {
        var selectedOptions by remember { mutableStateOf(mutableListOf<Int>()) }
        val options = listOf<String>(
            "Overall Service",
            "Customer Support",
            "Speed and Efficiency",
            "Field Status",
            "Additional Services",
        )
        FlowRow() {
            for (element in options) {
                val index = options.indexOf(element)
                val color: Color
                val textColor: Color
                if (selectedOptions.contains(index)) {
                    color = colorResource(id = R.color.deep_blue)
                    textColor = Color.White
                } else {
                    color = Color.LightGray
                    textColor = Color.Black
                }


                Button(
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = color),
                    shape = CircleShape,
                    onClick = {
                        if (selectedOptions.contains(index)) {
                            selectedOptions =
                                selectedOptions.filter { i -> i != index }.toMutableList()
                        } else {
                            var newList = selectedOptions.toMutableList()
                            newList.add(index)
                            selectedOptions = newList
                        }
                        viewModel.selectedImprovements = selectedOptions
                    }) {
                    Text(element, textAlign = TextAlign.Center, fontSize = 12.sp, color = textColor)
                }
            }
        }

    }

    @Composable
    fun StarRatingComponent() {
        val maxStars = 5
        var rating by remember { mutableStateOf(0) }
        var isSubmitting by remember { viewModel.isSubmitting }

        Row(modifier = Modifier.zIndex(2f)) {
            for (index in 1..maxStars) {
                val starScale by animateFloatAsState(
                    targetValue = if (isSubmitting) 1.01f else 1f,
                    animationSpec = keyframes {
                        durationMillis = 300
                        // define keyframes for the animation
                        1f.at(0).with(LinearOutSlowInEasing) // start with initial size
                        1.5f.at(75).with(FastOutSlowInEasing) // bounce up
                        0.5f.at(175).with(LinearOutSlowInEasing) // bounce down
                        1.0f.at(275).with(LinearOutSlowInEasing) // end with final size
                        delayMillis = (index) * 150
                    }
                )
                IconButton(
                    onClick = {
                        rating = if (index == rating) 0 else index
                        viewModel.selectedRating = rating
                    },
                    modifier = Modifier.scale(if (isSubmitting && index<=rating) starScale else 1f),
                )
                {
                    val color = if (index <= rating) Color(0xFFFFC107) else Color.Gray
                    Icon(
                        Icons.Default.Star,
                        modifier = Modifier.size(size = 30.dp),
                        tint = color,
                        contentDescription = "Rating of $index",
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        CourtReservationAppTheme {
            PageLayout()
        }
    }
}