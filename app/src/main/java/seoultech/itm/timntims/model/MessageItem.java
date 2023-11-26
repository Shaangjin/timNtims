package seoultech.itm.timntims.model;

public class MessageItem extends ChatItem {
    private String message;
    private int Sentby;
    public MessageItem(String message, int Sentby) {

        this.message = message;
        this.Sentby = Sentby ;
    }

    public String getMessage() {
        return message;
    }
    public int getSentBy(){ return this.Sentby ; }

    @Override
    public int getType() {
        return this.getSentBy() ;
    }
}
