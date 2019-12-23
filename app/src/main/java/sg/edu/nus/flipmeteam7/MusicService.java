package sg.edu.nus.flipmeteam7;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    MediaPlayer menuMusicPlayer;
    MediaPlayer gameMusicPlayer;
    MediaPlayer happyMusicPlayer;
    MediaPlayer sadMusicPlayer;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

    public void playMenuSong(){
        if(gameMusicPlayer != null) gameMusicPlayer.pause();
        if(happyMusicPlayer != null) happyMusicPlayer.pause();
        if(sadMusicPlayer != null) sadMusicPlayer.pause();
        if(menuMusicPlayer == null) {
            menuMusicPlayer = MediaPlayer.create(this, R.raw.menusong);
            menuMusicPlayer.setLooping(true);
        }
        menuMusicPlayer.start();
    }

    public void playGameSong(){
        if(menuMusicPlayer != null) menuMusicPlayer.pause();
        if(happyMusicPlayer != null) happyMusicPlayer.pause();
        if(sadMusicPlayer != null) sadMusicPlayer.pause();
        if(gameMusicPlayer == null) {
            gameMusicPlayer = MediaPlayer.create(this, R.raw.gamesong);
            gameMusicPlayer.setLooping(true);
        }
        gameMusicPlayer.start();
    }
    public void playHappySong(){
        if(menuMusicPlayer != null) menuMusicPlayer.pause();
        if(gameMusicPlayer != null) gameMusicPlayer.pause();
        if(sadMusicPlayer != null) sadMusicPlayer.pause();
        if(happyMusicPlayer == null) {
            happyMusicPlayer = MediaPlayer.create(this, R.raw.happymusic);
            happyMusicPlayer.setLooping(true);
        }
        happyMusicPlayer.start();
    }
    public void playSadSong(){
        if(menuMusicPlayer != null) menuMusicPlayer.pause();
        if(gameMusicPlayer != null) gameMusicPlayer.pause();
        if(happyMusicPlayer != null) happyMusicPlayer.pause();
        if(sadMusicPlayer == null) {
            sadMusicPlayer = MediaPlayer.create(this, R.raw.sadmusic);
            sadMusicPlayer.setLooping(true);
        }
        sadMusicPlayer.start();
    }
    public void stopPlaying(){
        if(menuMusicPlayer != null) menuMusicPlayer.pause();
        if(gameMusicPlayer != null) gameMusicPlayer.pause();
        if(happyMusicPlayer != null) happyMusicPlayer.pause();
        if(sadMusicPlayer != null) sadMusicPlayer.pause();
    }

}
