package com.example.chattingapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattingapplication.Screen.ChatListScreen
import com.example.chattingapplication.Screen.LoginScreen
import com.example.chattingapplication.Screen.ProfileScreen
import com.example.chattingapplication.Screen.SignUpScreen
import com.example.chattingapplication.Screen.SingleChatScreen
import com.example.chattingapplication.Screen.SingleStatusScreen
import com.example.chattingapplication.Screen.StatusScreen
import com.example.chattingapplication.Screen.ViewProfileScreen
import com.example.chattingapplication.ui.theme.ChattingApplicationTheme
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(var route:String){
    object SignUp:DestinationScreen("signUp")
    object Login:DestinationScreen("login")
    object Profile:DestinationScreen("profile")
    object ChatList:DestinationScreen("chatlist")
    object SingleChat:DestinationScreen("singleChat/{chatId}"){
        fun createRoute(id:String)="singleChat/$id"
    }
    object StatusList:DestinationScreen("statusList")
    object SingleStatus:DestinationScreen("singleStatus/{userId}"){
        fun createRoute(userId:String)="singleStatus/$userId"
    }
    object ViewProfileScreen:DestinationScreen("viewProfile/{userId}"){
        fun createRoute(userId:String)="viewProfile/$userId"
    }

}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation()
                }
            }
        }
    }
    @Composable
    fun ChatAppNavigation(){
        val navController= rememberNavController()
        var vm= hiltViewModel<LCViewModel>()
        NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route){
            composable(DestinationScreen.SignUp.route){
                SignUpScreen(navController ,vm)
            }
            composable(DestinationScreen.Login.route){
                LoginScreen(vm,navController)
            }
            composable(DestinationScreen.ChatList.route){
                ChatListScreen(navController,vm)
            }
            composable(DestinationScreen.SingleChat.route){
                val chatId=it.arguments?.getString("chatId")
                chatId?.let {
                    SingleChatScreen(navController =navController,vm=vm, chatId = chatId)
                }
            }
            composable(DestinationScreen.StatusList.route){
                StatusScreen(navController,vm)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController,vm)
            }
            composable(DestinationScreen.Profile.route){
                ProfileScreen(navController,vm)
            }
            composable(DestinationScreen.SingleStatus.route){
                val userId=it.arguments?.getString("userId")
                userId?.let{
                    SingleStatusScreen(navController,vm, userId = it)
                }
            }
            composable(DestinationScreen.ViewProfileScreen.route){
                val userId=it.arguments?.getString("userId")
                userId?.let{
                    ViewProfileScreen(navController,vm,userId=it)
                }
            }

        }
    }
}

