package seoultech.itm.timntims.model

import java.util.Date

data class Chat(
    val chatId: String?,
    var title: String,
    val createDate: Long,
    var disabled: Boolean

)
