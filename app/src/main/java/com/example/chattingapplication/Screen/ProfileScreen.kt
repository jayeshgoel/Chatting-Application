package com.example.chattingapplication.Screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chattingapplication.CommonImage
import com.example.chattingapplication.DestinationScreen
import com.example.chattingapplication.LCViewModel
import com.example.chattingapplication.R
import com.example.chattingapplication.commonDivider
import com.example.chattingapplication.data.UserData
import com.example.chattingapplication.navigateTo

@Composable
fun ProfileScreen(navController: NavController, vm: LCViewModel) {
    var userData=vm.userData
    var userName by remember {
        mutableStateOf(userData.value?.name)
    }
    var userNumber by remember {
        mutableStateOf(userData.value?.number)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            profileContent(
                navController = navController,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                vm = vm,
                name = userName!!,
                number = userNumber!!,
                onNameChange = {  },
                onNumberChange = { },
                onBack = {},
                onSave = {},
                onLogOut = {}
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE, navController = navController)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileContent(
    navController: NavController,
    modifier: Modifier,
    vm: LCViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onLogOut: () -> Unit
) {
    var nameState= remember{
        mutableStateOf(name)
    }
    var numberState= remember{
        mutableStateOf(number)
    }

    val imageUrl = vm.userData.value?.imageUrl


    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.lightblack))
                .padding(7.dp),

            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back",
                Modifier
                    .clickable {
                        navigateTo(navController = navController, DestinationScreen.ChatList.route)
                    }
                    .padding(10.dp),

                fontWeight = FontWeight.Bold,
                color = Color.White
                )
            Text(text = "Save",
                Modifier
                    .clickable {
                        vm.updateNameAndNumber(
                            nameState.value.toString(),
                            numberState.value.toString()
                        )
                    }
                    .padding(10.dp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

        }


        profileImage(imageUrl = imageUrl, vm = vm)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name  ", modifier = Modifier
                .width(100.dp)
                .padding(10.dp),
                fontWeight = FontWeight.Bold
            )
            TextField(
                value = nameState.value,
                onValueChange = {
                    nameState.value=it
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Black, containerColor = Color.Transparent
                ))

        }
        commonDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Number ", modifier = Modifier
                .width(100.dp)
                .padding(10.dp),
                fontWeight = FontWeight.Bold
            )
            TextField(
                value = numberState.value,
                onValueChange = {
                                numberState.value=it
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedTextColor = Color.Black, containerColor = Color.Transparent
                )

            )
        }
        commonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Log Out", modifier = Modifier
                .clickable {
                    vm.logOut(navController)
                },
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun profileImage(imageUrl: String?, vm: LCViewModel) {
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }
    }
    Box(modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                CommonImage(data = imageUrl)
            }
            Text(text = "Change Profile Picture", fontWeight = FontWeight.Bold)

        }

    }
}

