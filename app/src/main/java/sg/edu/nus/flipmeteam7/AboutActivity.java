package sg.edu.nus.flipmeteam7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {
    Button mainButton;
    MusicService musicService;
    boolean continuePlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Button mainButton = findViewById(R.id.mainButton);
        mainButton.setOnClickListener(this);
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainButton:
                finish();
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