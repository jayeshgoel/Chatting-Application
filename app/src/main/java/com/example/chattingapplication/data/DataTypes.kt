package com.example.chattingapplication.data

import com.google.firebase.Timestamp

data class UserData(
    var userId: String? = "",
    var name: String? = "",
    var number: String? = "",
    var imageUrl: String? = ""
) {
    fun toMap() {
        mapOf(
            "userId" to userId,
            "name" to name,
            "number" to number,
            "imageUrl" to imageUrl
        )
    }
}
data class ChatData(
    val chatId: String? ="",
    val user1: ChatUser= ChatUser(),
    val user2: ChatUser=ChatUser()
) {

}
data class ChatUser(
    val userId:String? = null,
    val name:String? = null,
    val imageUrl:String? = null,
    val number:String? = null
)

data class Message(
    var sendBy:String? = "",
    val message:String?= "",
    val timeStamp:String?=""
)
data class Status(
    val user: ChatUser=ChatUser(),
    val imageUrl:String? =null,
    val timeStamp: Long? = null

)