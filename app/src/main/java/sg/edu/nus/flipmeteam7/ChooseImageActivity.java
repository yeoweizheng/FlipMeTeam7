package sg.edu.nus.flipmeteam7;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseImageActivity extends AppCompatActivity implements View.OnClickListener, FetchImageTask.ICallback {

    public static final int NO_OF_IMAGES = 20;
    int imageViewNo, imageSelectedCount;
    FetchImageTask fetchImageTask;
    HashMap<Integer, Boolean> imageSelected;
    ArrayList<ImageCard> imageCards;
    ArrayList<ImageCard> gameCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        Button fetchButton = (Button) findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(this);
        setImageOnClickListeners();
        imageSelected = new HashMap<Integer, Boolean>();
        imageCards = new ArrayList<ImageCard>();
        gameCards = new ArrayList<ImageCard>();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fetchButton) startFetchImageTask();
        for(int i = 0; i < NO_OF_IMAGES; i++){
            if(v.getId() == getResources().getIdentifier("chooseImageView" + i, "id", getPackageName())){
                toggleImageView(i, (ImageView) v);
            }
        }
    }
    void toggleImageView(int imgNo, ImageView v){
        if(!imageSelected.get(imgNo)){
            v.setColorFilter(Color.YELLOW, PorterDuff.Mode.OVERLAY);
            imageSelected.put(imgNo, true);
            imageSelectedCount++;
            if(imageSelectedCount == 6) startGameActivity();
        } else {
            v.clearColorFilter();
            imageSelected.put(imgNo, false);
            imageSelectedCount--;
        }
    }

    void startFetchImageTask(){
        clearImages();
        final EditText urlEditText = findViewById(R.id.urlEditText);
        imageViewNo = 0;
        if(fetchImageTask != null){
            fetchImageTask.cancel(true);
        }
        fetchImageTask = new FetchImageTask(this);
        fetchImageTask.execute(urlEditText.getText().toString());
    }

    @Override
    public void onBitmapReady(ImageCard imageCard){
        if(imageViewNo > NO_OF_IMAGES) return;
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("chooseImageView" + imageViewNo, "id", getPackageName()));
        imageView.setImageBitmap(imageCard.getBitmap());
        imageCards.add(imageCard);
        imageViewNo++;
    }

    void setImageOnClickListeners(){
        for(int i = 0; i < NO_OF_IMAGES; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("chooseImageView" + i, "id", getPackageName()));
            imageView.setOnClickListener(this);
        }
    }

    void clearImages(){
        for(int i = 0; i < NO_OF_IMAGES; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("chooseImageView" + i, "id", getPackageName()));
            imageView.setImageResource(R.color.white);
            imageView.clearColorFilter();
            imageSelected.put(i, false);
            imageSelectedCount = 0;
        }
    }

    void startGameActivity(){
        generateGameCards();
        saveBitmapFiles();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("mode", "singlePlayer");
        startActivityForResult(intent, 0);
    }

    void generateGameCards(){
        gameCards.clear();
        for(int i = 0; i < NO_OF_IMAGES; i++){
            if(imageSelected.get(i)) this.gameCards.add(imageCards.get(i));
        }
    }

    void saveBitmapFiles(){
        for(int i = 0; i < 6; i++){
            String filename = "bitmap" + i;
            File file = new File(getApplicationContext().getFilesDir(), filename);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                gameCards.get(i).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
