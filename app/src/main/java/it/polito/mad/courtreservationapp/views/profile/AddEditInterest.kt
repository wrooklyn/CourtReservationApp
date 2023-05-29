package it.polito.mad.courtreservationapp.views.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.view_model.SportMasteryViewModel
import it.polito.mad.courtreservationapp.view_model.UserViewModel
import it.polito.mad.courtreservationapp.views.login.SavedPreference

class AddEditInterestActivity : ComponentActivity() {
    private val sportMasteryViewModel: SportMasteryViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sportMasteryViewModel.email = SavedPreference.EMAIL
        setContent {
            it.polito.mad.courtreservationapp.views.ratings.ui.theme.CourtReservationAppTheme {
                // A surface container using the 'background' color from the theme
                androidx.compose.material.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material.MaterialTheme.colors.background
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
                    title = { androidx.compose.material.Text("Pick your interests") },
                    backgroundColor = Color.White,
                    navigationIcon = {
                        IconButton(onClick = {
                            finish()
                        }) {
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
                    // Add your components here
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red_button)),
                        onClick = {
                            sportMasteryViewModel.saveMastery()
                            // In the activity that is being finished
                            val data = Intent()
                            data.putExtra("email", sportMasteryViewModel.email)
                            data.putExtra("sport", sportMasteryViewModel.sport)
                            data.putExtra("level", sportMasteryViewModel.level.toString())
                            data.putExtra("achievement", sportMasteryViewModel.achievement)
                            setResult(Activity.RESULT_OK, data)
                            finish()
                        }) {
                        androidx.compose.material.Text(
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
                        AddEditInterest()
                    }
                }
            }
        }
    }

    @Composable
    fun AddEditInterest() {
        InterestList()
        MasteryLevel()
        Achievements()
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun InterestList() {
        var selectedSport by remember { mutableStateOf("") }
        val sportIconsId: Map<String, Pair<Int, Int>> = mapOf(
            Pair("Soccer", Pair(R.drawable.soccer_ball, 1)),
            Pair("Volley", Pair(R.drawable.volleyball, 2)),
            Pair("Hockey", Pair(R.drawable.hockey, 3)),
            Pair("Basketball", Pair(R.drawable.basketball_icon, 4)),
            Pair("Tennis", Pair(R.drawable.tennis, 5)),
            Pair("Iceskate", Pair(R.drawable.ice_skate, 6)),
            Pair("Rugby", Pair(R.drawable.rugby, 7))
        )

        val sportIconSize = 5.dp
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(sportIconsId.toList()) {
                val thisButtonSelected = selectedSport == it.first
                val backgroundColor =
                    if (thisButtonSelected) colorResource(id = R.color.red_button) else Color.LightGray
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                        selectedSport = if (thisButtonSelected) "" else it.first
                        sportMasteryViewModel.sport = selectedSport.lowercase()
                    }) {
                    Image(
                        modifier = Modifier
                            .padding(vertical = sportIconSize + 5.dp, horizontal = sportIconSize)
                            .height(50.dp)
                            .width(50.dp),
                        painter = painterResource(id = it.second.first),
                        contentDescription = it.first,
                    )
                }


            }

        }
    }

    @Composable
    fun MasteryLevel() {
        var isExpanded by remember { mutableStateOf(false) }
        val items = listOf("Beginner", "Intermediate", "Expert", "Professional")
        var selectedIndex by remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)
                .padding(top = 8.dp)
        ) {

            Row(
                modifier = Modifier.clickable(
                    onClick = { isExpanded = true }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    items[selectedIndex],
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.red_hat_display_medium)),
                        fontSize = 25.sp,
                    )
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand dropdown")
            }
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                items.forEachIndexed { index, s ->
                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        sportMasteryViewModel.level = index
                        isExpanded = false
                    }) {
                        Text(
                            text = s,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.red_hat_display_medium)),
                                fontSize = 20.sp,
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Achievements() {
        var text by remember { mutableStateOf(TextFieldValue("")) }
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White, RoundedCornerShape(5.dp)),
            value = text,
            onValueChange = { newText ->
                text = newText
                sportMasteryViewModel.achievement = newText.text
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.red_button),
                cursorColor = colorResource(id = R.color.red_button)
            ),
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        it.polito.mad.courtreservationapp.views.ratings.ui.theme.CourtReservationAppTheme {
            PageLayout()
        }
    }
}