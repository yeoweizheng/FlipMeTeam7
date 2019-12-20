package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class FetchImageTask extends AsyncTask<String, ImageCard, Void> {
    ICallback callback;
    int offset;

    public FetchImageTask(ICallback callback){
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... urls) {
        offset = 10;
        String htmlText = getHTMLText(urls[0]);
        ArrayList<String> imgTags = getImgTags(htmlText, ChooseImageActivity.NO_OF_IMAGES + offset);
        fetchBitmaps(imgTags);
        return null;
    }

    String getHTMLText(String urlString){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            while(scanner.hasNext()){
                stringBuffer.append(scanner.nextLine());
            }
            connection.disconnect();
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
        return stringBuffer.toString();
    }

    ArrayList<String> getImgTags(String htmlText, int count){
        ArrayList<String> imgTags = new ArrayList<String>();
        int lastIndex = 0;
        for(int i = 0; i < count; i++){
            int index1 = htmlText.indexOf("<img src=", lastIndex);
            int index2 = htmlText.indexOf(">", index1);
            imgTags.add(htmlText.substring(index1, index2 + 1));
            lastIndex = index2;
        }
        return imgTags;
    }

    void fetchBitmaps(ArrayList<String> imgTags) {
        for (int i = offset; i < offset + ChooseImageActivity.NO_OF_IMAGES; i++) {
            String imgTag = imgTags.get(i);
            int index1 = imgTag.indexOf("http");
            int index2a = imgTag.indexOf("\"", index1);
            int index2b = imgTag.indexOf("'", index1);
            int index2;
            if (index2a == -1 || index2b == -1) index2 = index2a == -1 ? index2b : index2a;
            else index2 = Math.min(index2a, index2b);
            try {
                final URL url = new URL(imgTag.substring(index1, index2));
                Log.d("weizheng", "message");
                //downloadImage(url);
                // Download images in separate threads
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{ downloadImage(url);}
                        catch(IOException e){e.printStackTrace();}
                    }
                }).start();
            } catch(IOException e){
                e.printStackTrace();
            }
            //try{ Thread.sleep(100); } catch(InterruptedException e){e.printStackTrace();}
        }
    }

    void downloadImage(URL url) throws IOException{
        if (isCancelled()) return;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        String cookie = connection.getHeaderField( "Set-Cookie");
        connection.disconnect();
        connection = (HttpURLConnection) url.openConnection();
        cookie = cookie != null? cookie.split(";")[0] : null;
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
        connection.setRequestProperty("Cookie", cookie);
        connection.connect();
        if(connection.getResponseCode() == 200) {
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            publishProgress(new ImageCard(bitmap, url.toString()));
        }
        connection.disconnect();
    }

    @Override
    protected void onProgressUpdate(ImageCard... imageCards){
        if(this.callback != null){
            this.callback.onBitmapReady(imageCards[0]);
        }
    }

    @Override
    protected void onPostExecute(Void v){
    }

    public interface ICallback{
        void onBitmapReady(ImageCard imageCard);
    }
}
