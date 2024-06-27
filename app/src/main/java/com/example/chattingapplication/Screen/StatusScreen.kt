package com.example.chattingapplication.Screen

import android.icu.text.CaseMap.Title
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapplication.CommonRow
import com.example.chattingapplication.DestinationScreen
import com.example.chattingapplication.LCViewModel
import com.example.chattingapplication.TitleText
import com.example.chattingapplication.commonDivider
import com.example.chattingapplication.commonHeader
import com.example.chattingapplication.commonProgressBar
import com.example.chattingapplication.data.UserData
import com.example.chattingapplication.navigateTo
import com.google.firebase.firestore.toObject
import kotlin.contracts.contract

@Composable
fun StatusScreen(navController: NavController, vm: LCViewModel) {
    val inProgress = vm.inProgressStatus.value

    val statuses = vm.status.value
    val userData = vm.userData.value
    val myStatus = statuses.filter {
        it.user.userId == userData?.userId
    }
    val otherStatuses = statuses.filter {
        it.user.userId != userData?.userId
    }
    val launcher= rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {uri->
        uri?.let { vm.uploadStatus(uri) }
        
    }
    
    Scaffold(
        floatingActionButton = {
            FAB {
                launcher.launch("image/*")
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                commonHeader(text = "Status")
                if (statuses.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No Status Available")
                    }
                } else {
                    if (myStatus.isNotEmpty()) {
                        Text(text = "My Status",modifier=Modifier.padding( 10.dp), fontWeight = FontWeight.Bold, fontSize =18.sp)
                        var UserImageUrl= remember {
                            mutableStateOf("")
                        }
                        var UserName= remember {
                            mutableStateOf("")
                        }
                        var user=myStatus[0].user
                        vm.userRef.document(user.userId!!).addSnapshotListener { value, error ->
                            if(value!=null){
                                UserImageUrl.value=value.toObject<UserData>()?.imageUrl.toString()
                                UserName.value=value.toObject<UserData>()?.name.toString()
                            }
                        }
                        CommonRow(
                            imageUri = UserImageUrl.value,
                            name = UserName.value
                        ) {
                            navigateTo(
                                navController = navController,
                                DestinationScreen.SingleStatus.createRoute(myStatus[0].user.userId!!)
                            )
                        }
                    }
                    commonDivider()
                    if(otherStatuses.isNotEmpty()) {
                        Text(text = "Other Status",modifier=Modifier.padding( 10.dp), fontWeight = FontWeight.Bold, fontSize =18.sp)
                        val uniqueUsers = otherStatuses.map { it.user }.toSet().toList()
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(uniqueUsers) { user ->
                                var UserImageUrl= remember {
                                    mutableStateOf("")
                                }
                                var UserName= remember {
                                    mutableStateOf("")
                                }

                                vm.userRef.document(user.userId!!).addSnapshotListener { value, error ->
                                    if(value!=null){
                                        UserImageUrl.value=value.toObject<UserData>()?.imageUrl.toString()
                                        UserName.value=value.toObject<UserData>()?.name.toString()
                                    }
                                }

                                CommonRow(imageUri = UserImageUrl.value, name = UserName.value) {
                                    user.userId?.let {
                                        navigateTo(
                                            navController = navController,
                                            DestinationScreen.SingleStatus.createRoute(it)
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.STATUSLIST,
                    navController = navController
                )
            }
        }
    )


}


@Composable
fun FAB(
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 75.dp, end = 10.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Status", tint = Color.White)
    }
}