package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ImageCard {
    private int id;
    private Bitmap bitmap;
    private String url;

    public ImageCard(Bitmap bitmap, int id){
        this.bitmap = bitmap;
        this.id = id;
    }

    public ImageCard(Bitmap bitmap, String url) {
        this.bitmap = bitmap;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
