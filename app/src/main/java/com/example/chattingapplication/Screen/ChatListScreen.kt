package com.example.chattingapplication.Screen

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chattingapplication.CommonImage
import com.example.chattingapplication.CommonRow
import com.example.chattingapplication.DestinationScreen
import com.example.chattingapplication.LCViewModel
import com.example.chattingapplication.TitleText
import com.example.chattingapplication.commonHeader
import com.example.chattingapplication.commonProgressBar
import com.example.chattingapplication.data.ChatData
import com.example.chattingapplication.data.ChatUser
import com.example.chattingapplication.data.MESSAGE
import com.example.chattingapplication.data.UserData
import com.example.chattingapplication.dropDownMenu
import com.example.chattingapplication.navigateTo
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject

@Composable
fun ChatListScreen(navController: NavController, vm: LCViewModel) {
    val inProgress = vm.inProcessChat

    if (inProgress.value) {
        commonProgressBar()
    } else {
        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick: () -> Unit = { showDialog.value = true }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            vm.onAddChat(it)
            showDialog.value = false
        }
        Scaffold(floatingActionButton = {
            FAB(
                showDialog = showDialog.value, onFabClick, onDismiss, onAddChat
            )
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {


                commonHeader(text = "Chats")


                if (chats.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No Chats Available")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)

                    ) {

                        items(chats) { chat ->
                            // ensuring not chatting with yourself
                            val chatUser = if (chat.user1.userId == userData?.userId) {
                                chat.user2
                            } else {
                                chat.user1
                            }
                            var chatUserImageUrl = remember {
                                mutableStateOf("")
                            }
                            var chatUserName = remember {
                                mutableStateOf("")
                            }

                            vm.userRef.document(chatUser.userId!!)
                                .addSnapshotListener { value, error ->
                                    if (value != null) {
                                        chatUserImageUrl.value =
                                            value.toObject<UserData>()?.imageUrl.toString()
                                        chatUserName.value =
                                            value.toObject<UserData>()?.name.toString()
                                    }
                                }
                            val deleteChat = {
                                vm.chatsRef.document(chat.chatId!!).delete()
                            }
                            Chat(
                                chatUserName.value,
                                chatUserImageUrl.value,
                                chat.chatId!!,
                                chatUser.userId,
                                navController,
                            ) {
                                vm.chatsRef.document(chat.chatId!!).delete()
                                vm.chatsRef.document(chat.chatId!!).collection(MESSAGE).document()
                                    .delete()
                            }

                        }


                    }
                }
                BottomNavigationMenu(
                    selectedItem = BottomNavigationItem.CHATLIST, navController = navController
                )
            }
        })
    }


}

@Composable
fun Chat(
    name: String,
    imageUrl: String,
    chatId: String,
    userId: String,
    navController: NavController,
    deleteChat: () -> Unit
) {

    Row(
        Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable {
                navigateTo(
                    navController,
                    DestinationScreen.SingleChat.createRoute(id = chatId)
                )
            },
        verticalAlignment = Alignment.CenterVertically

    ) {
        CommonImage(
            data = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(50.dp)
                .clip(CircleShape)
                .clickable {
                    navigateTo(
                        navController,
                        DestinationScreen.ViewProfileScreen.createRoute(userId)
                    )
                }

        )

        Text(
            text = name ?: "---",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(4.dp)
                .weight(1f)

        )
        var mExpanded by remember { mutableStateOf(false) }

        Column(modifier = Modifier.background(Color.Transparent).padding(top = 25.dp,end = 10.dp)) {
            var list = listOf("View Profile", "Delete")
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .clickable { mExpanded = true }
            )
            DropdownMenu(
                expanded = mExpanded,
                modifier = Modifier

                    .background(Color.White)

                    .wrapContentWidth(),
                onDismissRequest = { mExpanded = false }


            ) {

                list.forEach { label ->
                    DropdownMenuItem(
                        text = {
                            Text(text = label)
                        },
                        onClick = {
                            mExpanded = false

                            if (label == "Delete") {
                                deleteChat()
                            } else if (label == "View Profile") {
                                navigateTo(
                                    navController,
                                    DestinationScreen.ViewProfileScreen.createRoute(userId)
                                )
                            }
                        }
                    )

                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean, onFabClick: () -> Unit, onDismiss: () -> Unit, onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")

    }
    if (showDialog) {
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        }, confirmButton = {
            Button(onClick = {
                onAddChat(addChatNumber.value)
                addChatNumber.value = ""
            }) {
                Text(text = "Add Chat")
            }
        }, title = {
            Text(text = "Add Chat")
        }, text = {
            OutlinedTextField(
                value = addChatNumber.value, onValueChange = {
                    addChatNumber.value = it

                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        })

    }
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 75.dp, end = 10.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.White)
    }
}