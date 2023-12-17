package seoultech.itm.timntims.model;

public class MessageItem extends ChatItem {
    private String message;
    private int Sentby;
    private String senderName;
    public MessageItem(String message, int Sentby, String senderName) {

        this.message = message;
        this.Sentby = Sentby ;
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }
    public int getSentBy(){ return this.Sentby ; }
    public String getSenderName(){return this.senderName;}

    @Override
    public int getType() {
        return this.getSentBy() ;
    }
}
