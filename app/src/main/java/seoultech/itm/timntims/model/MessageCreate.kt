package seoultech.itm.timntims.model

data class MessageCreate(
    val time: Long,
    val uid: String?,
    val content: String
)
