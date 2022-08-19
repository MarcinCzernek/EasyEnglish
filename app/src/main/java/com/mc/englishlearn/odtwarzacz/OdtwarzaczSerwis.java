package com.mc.englishlearn.odtwarzacz;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class OdtwarzaczSerwis extends Service implements MediaPlayer.OnPreparedListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer odtwarzaczAudio = null;

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {
            odtwarzaczAudio = new MediaPlayer();
            odtwarzaczAudio.setOnPreparedListener(this);
            odtwarzaczAudio.prepareAsync(); // przygotowuję async, aby nie blokować głównego wątku
        }

        return START_NOT_STICKY;
    }

    /** Wezwany gdy odtwarzacz jest gotowy */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}