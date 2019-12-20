package sg.edu.nus.flipmeteam7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
    boolean disableClick;
    ArrayList<Integer> cardsOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        generateGameCards();
        generateGameMap();
        setImageOnClickListeners();
        for(int i = 0; i < 12; i++) {
            initCard(i);
            closeCard(i);
        }
        disableClick = false;
        cardsOpen = new ArrayList<Integer>();
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
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("gameImageView" + location, "id", getPackageName()));
        if(cardsOpen.size() == 0) {
            imageView.clearColorFilter();
            cardsOpen.add(location);
        } else {
            final boolean match;
            if(gameMap[cardsOpen.get(0)].getId() == gameMap[location].getId()){
                match = true;
            } else {
                match = false;
            }
            imageView.clearColorFilter();
            disableClick = true;
            cardsOpen.add(location);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!match) {
                            Thread.currentThread().sleep(1000);
                            for (int cardOpen : cardsOpen) {
                                closeCard(cardOpen);
                            }
                        }
                        cardsOpen.clear();
                        disableClick = false;
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    void closeCard(int location){
        ImageView imageView = (ImageView) findViewById(getResources().
                getIdentifier("gameImageView" + location, "id", getPackageName()));
        imageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_OVER);
    }

    public void onClick(View v){
        if(disableClick) return;
        for(int i = 0; i < 12; i++){
            if(v.getId() == getResources().getIdentifier("gameImageView" + i, "id", getPackageName())){
                openCard(i);
            }
        }
    }
}
