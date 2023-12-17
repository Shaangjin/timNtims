package seoultech.itm.timntims.model;

import android.net.Uri;

public class ImageItem extends ChatItem {
    private String imgName;
    private int Sentby;

    private String senderName;

    public ImageItem(String imgName, int Sentby, String senderName) {
        this.imgName = imgName;
        this.Sentby = Sentby;
        this.senderName = senderName;
    }
    public ImageItem(int Sentby) {

        this.Sentby = Sentby;
    }


    public String getUri() {
        return this.imgName;
    }
    public int getSentBy() {
        return this.Sentby;
    }
    public String getSenderName(){return this.senderName;}
    @Override
    public int getType() {
        return getSentBy();
    }
}
