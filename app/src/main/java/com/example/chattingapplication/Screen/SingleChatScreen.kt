package com.example.chattingapplication.Screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapplication.CommonImage
import com.example.chattingapplication.DestinationScreen
import com.example.chattingapplication.LCViewModel
import com.example.chattingapplication.R
import com.example.chattingapplication.commonDivider
import com.example.chattingapplication.data.Message
import com.example.chattingapplication.data.UserData
import com.example.chattingapplication.navigateTo
import com.example.chattingapplication.ui.theme.themeColor
import com.example.chattingapplication.ui.theme.whatsappGreen
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch


@Composable
fun SingleChatScreen(navController: NavController, vm: LCViewModel, chatId: String) {


    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val onSendReply = {
        if (reply.isNotEmpty()) {
            vm.onSendMessage(chatId = chatId, reply)
            reply = ""
        }
    }
    val myUser = vm.userData.value
    var chatMessage = vm.chatMessages
    val currentChat = vm.chats.value.first { it.chatId == chatId }
    val chatUser =
        if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1

    LaunchedEffect(key1 = Unit) {
        vm.populateMessages(chatId)
    }
    BackHandler {
        navController.popBackStack()
        vm.depopulateMessages()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        var chatUserName = remember {
            mutableStateOf("")
        }
        var chatUserImage = remember {
            mutableStateOf("")
        }

        vm.userRef.document(chatUser.userId!!).addSnapshotListener { value, error ->

            if (value != null) {
                chatUserName.value = value.toObject<UserData>()?.name.toString()
                chatUserImage.value = value.toObject<UserData>()?.imageUrl.toString()
            }
        }
        val onBackClicked = {
            navController.popBackStack()
            vm.depopulateMessages()
        }
        val onViewProfile = {
            navigateTo(
                navController = navController,
                DestinationScreen.ViewProfileScreen.createRoute(chatUser.userId)
            )
        }
        ChatHeader(
            name = chatUserName.value,
            imageUrl = chatUserImage.value,
            onBackClicked,
            onViewProfile
        )

        MessageBox(
            modifier = Modifier.weight(1f).padding(top = 10.dp),
            chatMessages = chatMessage.value,
            currentUserId = myUser?.userId ?: "", chatUserImage.value
        )

        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
    }

}

@Composable
fun MessageBox(
    modifier: Modifier,
    chatMessages: List<Message>,
    currentUserId: String,
    imageUrl: String
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        coroutineScope.launch {
            listState.animateScrollToItem(chatMessages.size)
        }
    }

    LazyColumn(modifier = modifier, state = listState) {

        items(chatMessages) { msg ->
            val alignment = if (msg.sendBy == currentUserId) Alignment.End else Alignment.Start
            val color = if (msg.sendBy == currentUserId) whatsappGreen else Color.Gray
            val showDateAndTime = remember {
                mutableStateOf(false)
            }
            val sendByMe = (msg.sendBy == currentUserId)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 2.dp, bottom = 2.dp),
                horizontalAlignment = alignment
            ) {
                var timestamp = msg.timeStamp?.split(" ")
                var time = timestamp?.get(3)
                var date = timestamp?.get(2) + " " + timestamp?.get(1) + " " + timestamp?.get(5)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showDateAndTime.value && sendByMe) {
                        showDateAndTimeFunc(date = date, time = time!!, sendByMe)
                    }
                    Text(

                        text = msg.message ?: "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = color)
                            .padding(start = 12.dp, end = 24.dp, top = 12.dp, bottom = 12.dp)
                            .widthIn(0.dp, 300.dp)
                            .clickable {
                                showDateAndTime.value = !showDateAndTime.value
                            },
                        color = Color.Black,
                    )

                    if (showDateAndTime.value && !sendByMe) {
                        showDateAndTimeFunc(date = date, time = time!!, sendByMe)
                    }

                }


            }
        }
    }
}

@Composable
fun showDateAndTimeFunc(date: String, time: String, sendByMe: Boolean) {
    val padding = if (sendByMe) PaddingValues(end = 20.dp) else PaddingValues(start = 20.dp)
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(padding)
    ) {
        Text(
            text = "Date : " + date ?: "",
            modifier = Modifier
                .wrapContentWidth(), fontSize = 12.sp,
            color = Color.Black
        )
        Text(
            text = "Time : " + time ?: "",
            modifier = Modifier
                .wrapContentWidth(), fontSize = 12.sp,
            color = Color.Black
        )

    }
}

@Composable
fun ChatHeader(
    name: String,
    imageUrl: String,
    onBackClicked: () -> Unit,
    onViewProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier.weight(1f).clickable {
                onViewProfile.invoke()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onBackClicked.invoke()
                    }
                    .padding(start = 12.dp)
            )
            CommonImage(
                data = imageUrl, modifier = Modifier
                    .padding( end = 12.dp, start = 12.dp)
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable {
                        onViewProfile.invoke()
                    }
            )

            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

    }
    commonDivider()
}

@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = reply,
                onValueChange = onReplyChange,
                maxLines = 3,
                modifier= Modifier
                    .weight(1f)

                    .padding(end = 8.dp),
                placeholder = { Text("MESSAGE") }
            )
            Button(onClick = { onSendReply.invoke() },
                Modifier.run {
                    wrapContentWidth()

                },
                colors =ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = Color.White
                )

            ) {
                Text(text = "Send",modifier=Modifier.wrapContentWidth())
            }
        }
    }
}


