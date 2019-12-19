package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ChooseImageActivity extends AppCompatActivity implements View.OnClickListener, FetchImageTask.ICallback {

    int imageViewNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        Button fetchButton = (Button) findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(this);
        imageViewNo = 1;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.fetchButton: startFetchImageTask(); break;
        }
    }

    void startFetchImageTask(){
        EditText urlEditText = (EditText) findViewById(R.id.urlEditText);
        new FetchImageTask(this).execute(urlEditText.getText().toString());
    }

    @Override
    public void onBitmapReady(Bitmap bitmap){
        if(imageViewNo > 8) return;
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("imageView" + imageViewNo, "id", getPackageName()));
        imageView.setImageBitmap(bitmap);
        imageViewNo++;
    }
}
