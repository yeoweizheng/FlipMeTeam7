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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startGameButton = (Button) findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(this);
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(musicService != null) musicService.playMenuSong();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(musicService != null) musicService.stopPlaying();
        unbindService(this);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.startGameButton:
                Intent intent = new Intent(this, ChooseImageActivity.class);
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
