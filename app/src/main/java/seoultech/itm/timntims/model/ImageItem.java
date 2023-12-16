package seoultech.itm.timntims.model;

import android.net.Uri;

public class ImageItem extends ChatItem {
    private Uri uri;
    private int Sentby;

    public ImageItem(Uri uri, int Sentby) {
        this.uri = uri;
        this.Sentby = Sentby;
    }
    public ImageItem(int Sentby) {

        this.Sentby = Sentby;
    }


    public Uri getUri() {
        return this.uri;
    }
    public int getSentBy() {
        return this.Sentby;
    }

    @Override
    public int getType() {
        return getSentBy();
    }
}
