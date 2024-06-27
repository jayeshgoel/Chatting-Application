package com.example.chattingapplication.Screen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chattingapplication.CommonImage
import com.example.chattingapplication.LCViewModel
import com.google.android.gms.common.internal.service.Common


enum class State {
    INITIAL, ACTIVE, COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleStatusScreen(navController: NavController, vm: LCViewModel, userId: String) {
    // getting all the status of current user
    val statuses = vm.status.value.filter {
        it.user.userId == userId
    }.reversed()

    if (statuses.isNotEmpty()) {
        val currentStatus = remember {
            mutableStateOf(0)
        }

        val currentProgress = remember {
            mutableStateOf(1F / statuses.size.toFloat())
        }
        val numberOfValuesBetween = remember {
            mutableStateOf(0/statuses.size.toInt())
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)

        ) {


            CommonImage(
                data = statuses[currentStatus.value].imageUrl,
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (currentStatus.value == statuses.size - 1) {
                            navController.popBackStack()

                        } else {
                            currentStatus.value += 1
                        }
                        currentProgress.value = (currentStatus.value + 1) / (statuses.size.toFloat())
                    }
                    .pointerInput(Unit){
                        detectTapGestures { offset ->
                            if(offset.x<=size.width/2){
                                if (currentStatus.value ==0) {
                                    navController.popBackStack()

                                } else {
                                    currentStatus.value -= 1
                                }
                                currentProgress.value = (currentStatus.value + 1) / (statuses.size.toFloat())
                            }
                            else {
                                if (currentStatus.value == statuses.size - 1) {
                                    navController.popBackStack()

                                } else {
                                    currentStatus.value += 1
                                }
                                currentProgress.value = (currentStatus.value + 1) / (statuses.size.toFloat())
                            }
                        }
                    }
                ,
                contentScale = ContentScale.Fit
            )

            LinearProgressIndicator(
                progress = currentProgress.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = Color.LightGray,
                trackColor = Color.DarkGray
            )

//            Row(modifier = Modifier.fillMaxWidth()) {
//                statuses.forEachIndexed { index, status ->
//                    CustomerProgressIndicator(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(7.dp)
//                            .padding(1.dp),
//                        state = if (currentStatus.value < index) State.INITIAL
//                                else if (currentStatus.value == index) State.ACTIVE
//                            else State.COMPLETED
//                    ) {
//                        if(currentStatus.value<statuses.size-1) currentStatus.value +=1
//                        else navController.popBackStack()
//                    }
//                }
//            }

        }


    }

}

//@ExperimentalMaterial3Api
//@Composable
//fun CustomerProgressIndicator(modifier: Modifier, state: State, onComplete: () -> Unit) {
//    var progress = if (state == State.INITIAL) 0f else 1f
//    if (state == State.ACTIVE) {
//        val toggleState = remember {
//            mutableStateOf(false)
//        }
//        LaunchedEffect(toggleState) {
//            toggleState.value = true
//        }
//        val p: Float by animateFloatAsState(if (toggleState.value) 1f else 0f,
//            animationSpec = tween(5000),
//            finishedListener = { onComplete.invoke() })
//
//        progress = p
//        LinearProgressIndicator( modifier = modifier, progress = progress, color = Color.Red)
//
//    }
//
//}





