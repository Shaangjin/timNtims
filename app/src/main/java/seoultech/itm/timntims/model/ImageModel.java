package seoultech.itm.timntims.model;

public class ImageModel {
    private String url = "image.jpg";
    private int Sentby ;
    public ImageModel(String url, int Sentby) {

        this.url = url;
        this.Sentby = Sentby;
    }

    public ImageModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
