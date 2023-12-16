package seoultech.itm.timntims.model

data class RoomItem(
    val chatId: String? = null,
    var title: String = "",
    val createDate: Long = 0L,
    var disabled: Boolean = false
)
