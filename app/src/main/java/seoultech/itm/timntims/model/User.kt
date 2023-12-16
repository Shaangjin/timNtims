package seoultech.itm.timntims.model

data class User(
    val uid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val profileImage: String?
)