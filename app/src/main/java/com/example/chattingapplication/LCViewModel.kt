package com.example.chattingapplication

import android.net.Uri
import android.util.Log

import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController

import com.example.chattingapplication.data.CHATS
import com.example.chattingapplication.data.ChatData
import com.example.chattingapplication.data.ChatUser
import com.example.chattingapplication.data.Event
import com.example.chattingapplication.data.MESSAGE
import com.example.chattingapplication.data.Message
import com.example.chattingapplication.data.STATUS
import com.example.chattingapplication.data.Status
import com.example.chattingapplication.data.UserData
import com.example.chattingapplication.data.User_NODE
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db: FirebaseFirestore,
    val storage: FirebaseStorage
) :
    ViewModel() {
    var inProcess = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    var inProgressStatus = mutableStateOf(false)
    val inProgressChatMessage = mutableStateOf(false)

    val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val status = mutableStateOf<List<Status>>(listOf())

    var currentChatMessageListener: ListenerRegistration? = null


    val userRef=db.collection(User_NODE)
    val chatsRef=db.collection(CHATS)

    init {

        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }
    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(User_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot retrieve User")
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProcess.value = false
                populateChats()
                populateStatuses()
            }
        }
    }
    fun populateChats() {
        inProcessChat.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(exception = error)
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProcessChat.value = false
            }
        }
    }

    fun populateStatuses() {
        val timeDelta=24L * 60 *60*100 // 24hr time span
        val cutoff=System.currentTimeMillis()-timeDelta
        inProgressStatus.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
            }
            if (value != null) {
                val currentConnection = arrayListOf(userData.value?.userId)
                chats.value.forEach { chat ->
                    if (chat.user1.userId == userData.value?.userId) {
                        currentConnection.add(chat.user2.userId)
                    } else if (chat.user2.userId == userData.value?.userId){
                        currentConnection.add(chat.user1.userId)
                    }
                }
                db.collection(STATUS).whereIn("user.userId", currentConnection)
                    .addSnapshotListener { value, error ->
                        if(error!=null){
                            handleException(error)
                        }
                        if(value!=null){
                            status.value=value.toObjects()
                            inProgressStatus.value=false
                        }
                    }
            }

        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            inProcess.value = true
            if (it.isSuccessful) {
                Log.d("Tag", "SignUp : User Logged in ")
                signIn.value = true
                createOrUpdateProfile(name, number)

            } else {
                handleException(it.exception, "Sign Up Failed")

            }
        }
    }


    private fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            uid,
            name ?: userData.value?.name,
            number ?: userData.value?.number,
            imageUrl ?: userData.value?.imageUrl
        )

        // checking if user exist or not
        uid?.let {
            inProcess.value = true
            db.collection(User_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    // update user data
                    db.collection(User_NODE).document(uid).set(userData)

                } else {
                    db.collection(User_NODE).document(uid).set(userData)
                    inProcess.value = false
                    getUserData(uid)
                }
            }.addOnFailureListener {
                handleException(it, "Cannot Retrieve user")
            }
        }

    }



    fun handleException(exception: Exception? = null, customMessage: String? = "") {
        Log.e("TAG", "Live chat exception", exception)
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMessage else customMessage
        eventMutableState.value = Event(message)
        inProcess.value = false
    }

    fun loginIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill all the details")
        } else {
            inProcess.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }

                } else {
                    handleException(it.exception, "Login Failed")
                }
            }
        }
    }
    fun logOut(navController: NavController){
        auth.signOut()
        signIn.value=false

        navController.navigate(DestinationScreen.Login.route){

            popUpTo(0)
        }




    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)

        }

    }

    fun onAddChat(number: String) {
        if (number.isEmpty() || !number.isDigitsOnly()) {
            handleException(customMessage = "Number must contain digits only")
        } else {
            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(

                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)

                        )

                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(User_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(customMessage = "number not found")
                            } else {
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ),
                                    ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)

                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }
                } else {
                    handleException(customMessage = "Chats already exists")
                }
            }
        }
    }



    fun populateMessages(chatId: String) {
        inProgressChatMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatId).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error)
                }
                if (value != null) {
                    chatMessages.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.timeStamp }
                    inProgressChatMessage.value = false
                }

            }
    }

    fun depopulateMessages() {
        chatMessages.value = listOf()
        currentChatMessageListener = null
    }

    fun onSendMessage(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(userData.value?.userId, message.trim(), time)
        db.collection(CHATS).document(chatId).collection(MESSAGE).document().set(msg)
    }

    fun uploadStatus(uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())
        }

    }

    fun createStatus(imageUrl: String) {
        val newStatus = Status(
            ChatUser(
                userData.value?.userId,
                userData.value?.name,
                userData.value?.imageUrl,
                userData.value?.number
            ),
            imageUrl,
            System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(newStatus)
    }



    fun updateNameAndNumber(name:String,number:String){
        createOrUpdateProfile(name=name,number=number)
    }


}