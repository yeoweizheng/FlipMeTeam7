package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    ArrayList<ImageCard> gameCards;
    ImageCard[] gameMap;
    boolean[] matched;
    boolean disableClick;
    int firstCardOpen;
    int noAttempts;
    int noMatches;
    CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        generateGameCards();
        generateGameMap();
        setImageOnClickListeners();
        matched = new boolean[12];
        for(int i = 0; i < 12; i++) {
            matched[i] = false;
            initCard(i);
            closeCard(i);
        }
        disableClick = false;
        firstCardOpen = -1;
        noAttempts = 0;
        noMatches = 0;
        startTimer();
    }
    void generateGameCards(){
        gameCards = new ArrayList<ImageCard>();
        for(int i = 0; i < 6; i++){
            String filename = "bitmap" + i;
            File file = new File(getApplicationContext().getFilesDir(), filename);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                gameCards.add(new ImageCard(bitmap, i));
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    void generateGameMap(){
        gameMap = new ImageCard[12];
        ArrayList<Integer> initSequence = new ArrayList<Integer>();
        for (int i = 0; i < 12; i++) { initSequence.add(i); }
        Collections.shuffle(initSequence);
        for(int i = 0; i < 6; i++){
            gameMap[initSequence.get(i)] = gameCards.get(i);
            gameMap[initSequence.get(i + 6)] = gameCards.get(i);
        }
    }

    void setImageOnClickListeners(){
        for(int i = 0; i < 12; i++){
            ImageView imageView = (ImageView) findViewById(getResources().
                    getIdentifier("gameImageView" + i, "id", getPackageName()));
            imageView.setOnClickListener(this);
        }
    }

    void initCard(int location){
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("gameImageView" + location, "id", getPackageName()));
        imageView.setImageBitmap(gameMap[location].getBitmap());
    }
    void openCard(int location){
        if(disableClick || location == firstCardOpen) return;
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("gameImageView" + location, "id", getPackageName()));
        if(firstCardOpen == -1){ // Only 1 card open
            imageView.clearColorFilter();
            firstCardOpen = location;
            disableClick = false;
        } else {
            imageView.clearColorFilter();
            noAttempts++;
            updateNoAttempts(noAttempts);
            if(gameMap[firstCardOpen].getId() == gameMap[location].getId()){ // 2 cards open & matching
                matched[firstCardOpen] = true;
                matched[location] = true;
                disableCard(firstCardOpen);
                disableCard(location);
                closeUnmatchedCards();
                firstCardOpen = -1;
                noMatches++;
                if(noMatches == 6) stopGame();
            } else { // 2 cards open && not matching
                disableClick = true;
                firstCardOpen = -1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.currentThread().sleep(1000);
                            closeUnmatchedCards();
                            disableClick = false;
                        } catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
    void closeUnmatchedCards(){
        for(int i = 0; i < 12; i++){
            if(!matched[i]) closeCard(i);
        }
    }
    void closeCard(int location){
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("gameImageView" + location, "id", getPackageName()));
        imageView.clearColorFilter();
        imageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_OVER);
    }

    void disableCard(int location){
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("gameImageView" + location, "id", getPackageName()));
        imageView.setOnClickListener(null);
    }

    void updateNoAttempts(int n){
        TextView noAttempts = findViewById(R.id.noAttempts);
        noAttempts.setText(n + "");
    }

    void startTimer(){
        final TextView timeRemaining = (TextView) findViewById(R.id.gameTimeRemaining);
        timer = new CountDownTimer(120000, 1000){
            @Override
            public void onTick(long millis){
                int min = (int) millis / 60000;
                int sec = (int) (millis % 60000) / 1000;
                String text;
                if(sec < 10) text = min + ":0" + sec;
                else text = min + ":" + sec;
                timeRemaining.setText(text);
            }
            @Override
            public void onFinish(){

            }
        }.start();
    }

    void stopGame(){
        timer.cancel();
    }

    public void onClick(View v){
        for(int i = 0; i < 12; i++){
            if(v.getId() == getResources().getIdentifier("gameImageView" + i, "id", getPackageName())){
                openCard(i);
                return;
            }
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
