package sg.edu.nus.flipmeteam7;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button resetButton;
    Button playAgainButton;
    MusicService musicService;
    String currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        sharedPreferences = getSharedPreferences("leaderBoard", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setButtonOnClickListeners();
        showLeaderBoard();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        currentSong = getIntent().getStringExtra("currentSong");
    }

    void playMusic(){
        if(musicService != null) {
            switch(currentSong){
                case "menu":
                    musicService.playGameSong();
                    break;
                case "happy":
                    musicService.playHappySong();
                    break;
                case "sad":
                    musicService.playSadSong();
                    break;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        playMusic();
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

    void setButtonOnClickListeners(){
        resetButton = findViewById(R.id.resetButton);
        playAgainButton = findViewById(R.id.playAgainButton);
        resetButton.setOnClickListener(this);
        playAgainButton.setOnClickListener(this);
    }

    void showLeaderBoard(){
        List<LeaderboardRowItem> list = getLeaderboardList();
        Collections.sort(list);
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRank("#" + (i+1));
        }
        ListView listView =  findViewById(R.id.leaderBoard);
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, R.layout.activity_leader_board, list);
        listView.setAdapter(adapter);
    }

    ArrayList<LeaderboardRowItem> getLeaderboardList(){
        ArrayList<LeaderboardRowItem> list = new ArrayList<LeaderboardRowItem>();
        int lastIndex, i = 0;
        while(true){
            if(!sharedPreferences.contains("name" + i)) break;
            i++;
        }
        lastIndex = i;
        for(i = 0; i < lastIndex; i++){
            LeaderboardRowItem rowItem = new LeaderboardRowItem(
                sharedPreferences.getString("name" + i, null), sharedPreferences.getInt("score" + i, 0));
            list.add(rowItem);
        }
        return list;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.resetButton:
                editor.clear().commit();
                showLeaderBoard();
                break;
            case R.id.playAgainButton:
                finish();
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service){
        MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
        if(binder != null) {
            musicService = binder.getService();
            playMusic();
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){

    }
}
