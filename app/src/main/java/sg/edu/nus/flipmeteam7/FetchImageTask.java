package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class FetchImageTask extends AsyncTask<String, Bitmap, Void> {
    ICallback callback;

    public FetchImageTask(ICallback callback){
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... strings) {

        return null;
    }

    public interface ICallback{

    }
}
