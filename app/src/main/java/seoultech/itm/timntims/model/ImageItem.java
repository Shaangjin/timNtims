package seoultech.itm.timntims.model;

public class ImageItem extends ChatItem {
    private String url;
    private int Sentby;

    public ImageItem(String url, int Sentby) {
        this.url = url;
        this.Sentby = Sentby;
    }

    public String getUrl() {
        return this.url;
    }
    public int getSentBy() {
        return this.Sentby;
    }

    @Override
    public int getType() {
        return getSentBy();
    }
}
