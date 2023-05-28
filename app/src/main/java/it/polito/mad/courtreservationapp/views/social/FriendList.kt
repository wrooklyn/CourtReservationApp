package it.polito.mad.courtreservationapp.views.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Friend
import it.polito.mad.courtreservationapp.view_model.FriendListViewModel
import kotlinx.coroutines.launch
import java.util.*


@OptIn(ExperimentalPagerApi::class)
@Composable
fun FriendList(viewModel: FriendListViewModel) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val friendListState = viewModel.friendList.observeAsState()
    val friendList: List<Friend> = friendListState.value ?: listOf()
    var unacceptedFriendsCount: Int = 0

    for (friend in friendList) {
        if (!friend.accepted) unacceptedFriendsCount++
    }

    Column(modifier = Modifier
        .fillMaxHeight()){
        Column{
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = colorResource(id = R.color.red_highlight)

                    )
                },
                backgroundColor = Color.White,
                contentColor = colorResource(id = R.color.red_highlight)
            ) {
                Tab( //friends
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = {
                        Text(
                            text = tabRowItems[0].title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = colorResource(id = R.color.red_highlight),
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.roboto_medium)),
                                fontSize = 18.sp,
                            )
                        )
                    }
                )

                Tab( //requests
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = {
                        Text(
                            text = "${tabRowItems[1].title} ($unacceptedFriendsCount)",
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.roboto_medium)),
                                fontSize = 18.sp,
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = colorResource(id = R.color.red_highlight)
                        )
                    }
                )
            }
            HorizontalPager(
                count = tabRowItems.size,
                state = pagerState,
            ) {
                tabRowItems[pagerState.currentPage].screen(viewModel)
            }
        }
    }

}

@Composable
fun TabFriendList(
    viewModel: FriendListViewModel
) {
    val friendListState = viewModel.friendList.observeAsState()
    var friendList: List<Friend> = friendListState.value ?: listOf()
    friendList = friendList.filter { friend -> friend.accepted }
    var showDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxHeight().verticalScroll(scrollState)) {
            friendList.map {
                ListTile(
                    title = it.username,
                    leading = { Icon(Icons.Default.ArrowDropDown, "profile pic") },
                )
            }
        if (showDialog) {
            CustomDialog(
                onDismiss = {
                    showDialog = false
                },
                onSendFriendRequest = { username ->
                    viewModel.addNewFriend(username)
                    showDialog = false
                })
        }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
        FloatingActionButton(
            backgroundColor = colorResource(id = R.color.red_button),
            onClick = {
                showDialog = true
            }) {
            Icon(Icons.Default.Add, "Add", tint = Color.White)
        }
        }
    }

}

@Composable
fun TabFriendRequests(
    viewModel: FriendListViewModel
) {
    val friendListState = viewModel.friendList.observeAsState()
    var friendList: List<Friend> = friendListState.value ?: listOf()
    friendList = friendList.filter { friend -> !friend.accepted }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column() {
            friendList.map {
                ListTile(
                    title = it.username,
                    leading = {
                        Icon(Icons.Default.ArrowDropDown, "profile pic")
                    },
                    trailing = {
                        Row() {
                            Icon(Icons.Default.Done, "Accept",
                                tint = colorResource(id = R.color.green_500),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable {
                                        viewModel.acceptFriend(it.username)
                                    }
                            )
                            Icon(
                                Icons.Default.Close,
                                "Decline",
                                tint = colorResource(id = R.color.red_button),
                                modifier = Modifier.clickable {
                                    viewModel.declineFriend(it.username)
                                },
                            )
                        }
                    }
                )
            }
        }
    }
}

val tabRowItems = listOf(
    TabRowItem(
        title = "FRIENDS",
        screen = { viewModel -> TabFriendList(viewModel)  },
    ),
    TabRowItem(
        title = "REQUESTS",
        screen = { viewModel -> TabFriendRequests(viewModel) },
    ),
)

data class TabRowItem(
    val title: String,
    val screen: @Composable (viewModel: FriendListViewModel) -> Unit,
)