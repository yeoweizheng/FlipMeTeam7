package sg.edu.nus.flipmeteam7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    ArrayList<ImageCard> gameCards;
    ImageCard[] gameMap;
    boolean[] matched;
    boolean disableClick;
    int firstCardOpen;
    int noAttempts;
    int noMatches;
    CountDownTimer timer;
    MusicService musicService;
    AlertDialog alertDialog;
    int timeRemaining;
    int score;

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
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(musicService != null) musicService.playGameSong();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(musicService != null) musicService.stopPlaying();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);
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
        final TextView timeRemainingView = (TextView) findViewById(R.id.gameTimeRemaining);
        timer = new CountDownTimer(60000, 1000){
            @Override
            public void onTick(long millis){
                int min = (int) millis / 60000;
                int sec = (int) (millis % 60000) / 1000;
                String text;
                if(sec < 10) text = min + ":0" + sec;
                else text = min + ":" + sec;
                timeRemainingView.setText(text);
                timeRemaining = (int) millis / 1000;
            }
            @Override
            public void onFinish(){
                stopGame();
            }
        }.start();
    }

    void stopGame(){
        timer.cancel();
        calculateScore();
        showEnterNamePrompt();
    }

    void calculateScore(){
        score = (int)(10000 * (9 / (float)noAttempts) * ((float)(timeRemaining + 10)/ 60) * ((float)noMatches / 6));
    }

    void showEnterNamePrompt(){
        boolean win = false;
        if(noMatches == 6) win = true;
        LayoutInflater layoutInflater = LayoutInflater.from(GameActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_enter_name, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false);
        alertDialog = alertDialogBuilder.create();
        final Button submitButton = promptView.findViewById(R.id.submitBtn);
        submitButton.setOnClickListener(this);
        submitButton.setEnabled(false);
        submitButton.setAlpha(0.5f);
        TextView dialogGreeting = promptView.findViewById(R.id.dialogGreeting);
        TextView scoreView = promptView.findViewById(R.id.score);
        scoreView.setText(" " + score + " pts");
        EditText nameInput = promptView.findViewById(R.id.nameInput);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView textView = promptView.findViewById(R.id.nameInput);
                if(textView.getText().length() == 0) {
                    submitButton.setEnabled(false);
                    submitButton.setAlpha(0.5f);
                }
                else {
                    submitButton.setEnabled(true);
                    submitButton.setAlpha(1);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        if(win) {
            dialogGreeting.setText("Congratulations!");
            dialogGreeting.setTextColor(ContextCompat.getColor(this, R.color.ourGreen));
        } else {
            dialogGreeting.setText("Time's up... Try harder!");
            dialogGreeting.setTextColor(ContextCompat.getColor(this, R.color.ourRed));
        }
        alertDialog.show();
    }

    public void onClick(View v){
        if(v.getId() == R.id.submitBtn){
            alertDialog.dismiss();
            Intent intent = new Intent(this, LeaderBoardActivity.class);
            startActivity(intent);
        }
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
    @Override
    public void onServiceConnected(ComponentName name, IBinder service){
        MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
        if(binder != null) {
            musicService = binder.getService();
            musicService.playGameSong();
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){

    }
}
