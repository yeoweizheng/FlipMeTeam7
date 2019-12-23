package sg.edu.nus.flipmeteam7;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    MusicService musicService;
    boolean continuePlaying;
    Button startGameButton;
    Button scoreButton;
    Button aboutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonOnClickListeners();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume(){
        super.onResume();
        continuePlaying = false;
        if(musicService != null) musicService.playMenuSong();
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

    void setButtonOnClickListeners(){
        startGameButton = findViewById(R.id.startGameButton);
        scoreButton = findViewById(R.id.scoreButton);
        aboutButton = findViewById(R.id.aboutButton);
        startGameButton.setOnClickListener(this);
        scoreButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onClick(View v){
        Intent intent;
        switch(v.getId()){
            case R.id.startGameButton:
                continuePlaying = true;
                intent = new Intent(this, ChooseImageActivity.class);
                startActivity(intent);
                break;
            case R.id.scoreButton:
                intent = new Intent(this, LeaderBoardActivity.class);
                intent.putExtra("currentSong", "menu");
                continuePlaying = true;
                startActivity(intent);
                break;
            case R.id.aboutButton:
                intent = new Intent(this, AboutActivity.class);
                continuePlaying = true;
                startActivity(intent);
                break;
        }
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
