package sg.edu.nus.flipmeteam7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseImageActivity extends AppCompatActivity implements View.OnClickListener, FetchImageTask.ICallback, ServiceConnection {

    public static final int NO_OF_IMAGES = 20;
    int imageViewNo, imageSelectedCount;
    FetchImageTask fetchImageTask;
    HashMap<Integer, Boolean> imageSelected;
    ArrayList<ImageCard> imageCards;
    ArrayList<ImageCard> gameCards;
    private ProgressBar progressBar;
    private TextView progressTextView;
    MusicService musicService;
    boolean continuePlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        Button fetchButton = (Button) findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(this);
        imageSelected = new HashMap<Integer, Boolean>();
        imageCards = new ArrayList<ImageCard>();
        gameCards = new ArrayList<ImageCard>();
        imageSelectedCount = 0;
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressTextView = findViewById(R.id.progress_textview);
        progressTextView.setText("Click 'fetch' to download images");
        if(imageCards.size() != 0) updateSelectedImageCount();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume(){
        super.onResume();
        continuePlaying = false;
        if(musicService != null) musicService.playMenuSong();
        if(imageViewNo == NO_OF_IMAGES) {
            progressTextView.setText("Choose 6 images to start playing");
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if(musicService != null && !continuePlaying) musicService.stopPlaying();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
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
        } else {
            v.clearColorFilter();
            imageSelected.put(imgNo, false);
            imageSelectedCount--;
        }
        updateSelectedImageCount();
        if(imageSelectedCount == 6) startGameActivity();
    }

    void startFetchImageTask(){
        clearImages();
        clearImageOnClickListeners();
        progressTextView.setText("Downloading.. 0%");
        progressBar.setProgress(0);
        final EditText urlEditText = findViewById(R.id.urlEditText);
        imageViewNo = 0;
        imageCards.clear();
        if(fetchImageTask != null){
            fetchImageTask.cancel(true);
        }
        fetchImageTask = new FetchImageTask(this);
        fetchImageTask.execute(urlEditText.getText().toString());
    }

    @Override
    public void onBitmapReady(ImageCard imageCard){
        if(imageViewNo >= NO_OF_IMAGES) return;
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("chooseImageView" + imageViewNo, "id", getPackageName()));
        imageView.setImageBitmap(imageCard.getBitmap());
        imageView.setImageAlpha(255);
        imageCards.add(imageCard);
        imageViewNo++;
        int percentComplete = imageViewNo * 5;
        progressBar.setProgress(percentComplete);
        progressTextView.setText("Downloading.. " + percentComplete +" %");
        if(imageViewNo == NO_OF_IMAGES) {
            setImageOnClickListeners();
            progressTextView.setText("Choose 6 images to start playing");
        }
    }

    @Override
    public void makeToast(String message){
        final String innerMsg = message;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), innerMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    void setImageOnClickListeners(){
        for(int i = 0; i < NO_OF_IMAGES; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("chooseImageView" + i, "id", getPackageName()));
            imageView.setOnClickListener(this);
        }
    }

    void clearImageOnClickListeners(){
        for(int i = 0; i < NO_OF_IMAGES; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("chooseImageView" + i, "id", getPackageName()));
            imageView.setOnClickListener(null);
        }
    }

    void clearImages(){
        for(int i = 0; i < NO_OF_IMAGES; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("chooseImageView" + i, "id", getPackageName()));
            imageView.setImageAlpha(0);
            imageView.clearColorFilter();
            imageSelected.put(i, false);
            imageSelectedCount = 0;
        }
    }

    void resetImageToggles(){
        for(int i = 0; i < NO_OF_IMAGES; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("chooseImageView" + i, "id", getPackageName()));
            imageView.clearColorFilter();
            imageSelected.put(i, false);
        }
        imageSelectedCount = 0;
    }

    void startGameActivity(){
        generateGameCards();
        saveBitmapFiles();
        resetImageToggles();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
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
            if(file.exists()) file.delete();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file, false);
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

    void updateSelectedImageCount() {
        if(imageSelectedCount == 0) progressTextView.setText("Choose 6 images to start playing");
        else progressTextView.setText(imageSelectedCount + " / 6 chosen");
    }

    @Override
    public void onBackPressed(){
        continuePlaying = true;
        finish();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service){
        MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
        if(binder != null) {
            musicService = binder.getService();
            musicService.playMenuSong();
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){

    }
}
