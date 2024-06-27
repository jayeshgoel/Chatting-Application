package com.example.chattingapplication.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement



import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chattingapplication.DestinationScreen
import com.example.chattingapplication.R
import com.example.chattingapplication.navigateTo

enum class BottomNavigationItem(val icon: Int, val destinationScreen: DestinationScreen) {
    CHATLIST(R.drawable.chatlist, DestinationScreen.ChatList),
    STATUSLIST(R.drawable.status, DestinationScreen.StatusList),
    PROFILE(R.drawable.profile, DestinationScreen.Profile)

}

@Composable
fun BottomNavigationMenu(
    selectedItem: BottomNavigationItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(id = R.color.lightblack))
            .padding(top = 10.dp, bottom = 10.dp)
            , horizontalArrangement = Arrangement.SpaceBetween
        , verticalAlignment = Alignment.CenterVertically
    ) {
        for (item in BottomNavigationItem.values()) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)

                    .weight(1f)
                    .clickable {
                        navigateTo(navController, item.destinationScreen.route)
                    },
                colorFilter = if (item == selectedItem)
                    ColorFilter.tint(Color.White)
                else
                    ColorFilter.tint(Color.Gray)
            )
        }
    }
}