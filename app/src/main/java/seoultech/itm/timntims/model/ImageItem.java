package seoultech.itm.timntims.model;

import android.net.Uri;

public class ImageItem extends ChatItem {
    private String imgName;
    private int Sentby;

    public ImageItem(String imgName, int Sentby) {
        this.imgName = imgName;
        this.Sentby = Sentby;
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

    @Override
    public int getType() {
        return getSentBy();
    }
}
