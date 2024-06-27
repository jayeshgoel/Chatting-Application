package com.example.chattingapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }

}

@Composable
fun commonHeader(text:String){
    Text(
        text = text,
        modifier = Modifier
            .background(colorResource(id = R.color.lightblack))
            .fillMaxWidth()
            .padding(start = 20.dp, top = 15.dp, bottom = 15.dp),
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}


@Composable
fun commonProgressBar() {
    Box(
        modifier = Modifier
            .alpha(0.5f) // Semi-transparent background
            .background(Color.LightGray) // Background color
            .fillMaxSize()
            .clickable(enabled = false) {} // Disabled click
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center) // Center the progress indicator
        )
    }
}


@Composable
fun checkSignedIn(vm: LCViewModel, navController: NavController) {

    val alreadySignIn = remember { mutableStateOf(false) }
    val signedIn = vm.signIn.value
    // Making sure user Cant Login with Same Credential
    if (signedIn && !alreadySignIn.value) {
        alreadySignIn.value = true
        navController.navigate(DestinationScreen.ChatList.route) {
            popUpTo(0)
        }

    }
}

@Composable
fun commonDivider() {
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun CommonImage(
    data: String?,
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}

@Composable
fun TitleText(txt: String) {
    Text(
        text = txt,
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun CommonRow(imageUri: String?, name: String?, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { onItemClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonImage(
            data = imageUri,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
                .paint(painterResource(id = R.drawable.user_profile_svgrepo_com))
        )

        Text(
            text = name ?: "---",
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp)
        )

    }
}

@Composable
fun dropDownMenu(options: List<String>, onItemClick: () -> Unit) {
    var mExpanded by remember {
        mutableStateOf(false)
    }
    DropdownMenu(
        expanded = mExpanded,
        onDismissRequest = { mExpanded = false },
        modifier = Modifier
            .width(50.dp)
    ) {
        options.forEach { label ->
           DropdownMenuItem(text = {label }, onClick = {
               onItemClick.invoke()
               mExpanded=false
           })
        }
    }
}