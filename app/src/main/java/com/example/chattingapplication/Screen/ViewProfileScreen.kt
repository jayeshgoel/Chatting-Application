package com.example.chattingapplication.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chattingapplication.CommonImage
import com.example.chattingapplication.LCViewModel
import com.example.chattingapplication.data.UserData
import com.google.firebase.firestore.toObject

@Composable
fun ViewProfileScreen(navController: NavController, vm: LCViewModel, userId: String) {
    var userName by remember {
        mutableStateOf("")
    }
    var userImageUrl by remember {
        mutableStateOf("")
    }
    vm.userRef.document(userId).addSnapshotListener { value, error ->
        if (error != null) {
            vm.handleException(error)
        }
        if (value != null) {
            val user = value.toObject<UserData>()
            user?.let {
                userName = it.name.toString()
                userImageUrl = it.imageUrl.toString()
            }
        }
    }

    Column(
        modifier = Modifier.background(Color.Black), verticalArrangement = Arrangement.Center
    ) {
        Header(userName, { navController.popBackStack() })

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
        ) {

            CommonImage(data = userImageUrl, modifier = Modifier.fillMaxWidth().height(400.dp))

        }

    }
}

@Composable
fun Header(userName: String, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)

    ) {
        Icon(imageVector = Icons.Sharp.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 20.dp, top = 20.dp)
                .size(30.dp)
                .clickable {
                    onBackClick.invoke()
                }, tint = Color.White
        )
        Text(
            text = userName,
            modifier = Modifier.padding(18.dp),
            fontSize = 25.sp,
            color = Color.White,
        )

    }
}