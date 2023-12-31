package seoultech.itm.timntims.model


internal class MessageOnFirebase {
    var authorID: String? = null
        private set
    var contents: String? = null
        private set
    var date: String? = null
        private set
    var dataType: String? = null
        private set
    var chatroomID: String? = null
        private set
    var addMaterial: String? = null
        private set
    var authorName: String? = null
        private set

    private constructor()
    constructor(author: String?, text: String?, date:String?, dataType:String?, roomID:String?, addMaterial:String, authorName:String?) {
        this.authorID = author
        this.contents = text
        this.date = date
        this.dataType = dataType
        this.chatroomID = roomID
        this.addMaterial = addMaterial
        this.authorName = authorName
    }
}