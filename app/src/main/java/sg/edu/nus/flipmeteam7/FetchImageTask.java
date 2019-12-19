package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class FetchImageTask extends AsyncTask<String, Bitmap, Void> {
    ICallback callback;

    public FetchImageTask(ICallback callback){
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... urls) {
        String htmlText = getHTMLText(urls[0]);
        ArrayList<String> imgTags = getImgTags(htmlText, 25);
        for(String imgTag : imgTags){
            Log.d("weizheng", imgTag);
        }
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

    void fetchBitmaps(ArrayList<String> imgTags){
        for(int i = 1; i < 21; i++){
            String imgTag = imgTags.get(i);
            int index1 = imgTag.indexOf("http");
            int index2a = imgTag.indexOf("\"", index1);
            int index2b = imgTag.indexOf("'", index1);
            int index2;
            if(index2a == -1 || index2b == -1) index2 = index2a == -1? index2b : index2a;
            else index2 = Math.min(index2a, index2b);
            try {
                URL url = new URL(imgTag.substring(index1, index2));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                String cookie = connection.getHeaderField( "Set-Cookie");
                connection.disconnect();
                connection = (HttpURLConnection) url.openConnection();
                cookie = cookie != null? cookie.split(";")[0] : null;
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                connection.setRequestProperty("Cookie", cookie);
                connection.connect();
                Log.d("weizheng", url.toString());
                Log.d("weizheng", ""+connection.getResponseCode());
                if(connection.getResponseCode() == 200) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    publishProgress(bitmap);
                }
                connection.disconnect();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onProgressUpdate(Bitmap... bitmap){
        if(this.callback != null){
            this.callback.onBitmapReady(bitmap[0]);
        }
    }

    public interface ICallback{
        void onBitmapReady(Bitmap bitmap);
    }
}
