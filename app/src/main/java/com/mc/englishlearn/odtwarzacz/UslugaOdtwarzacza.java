package com.mc.englishlearn.odtwarzacz;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class UslugaOdtwarzacza extends Service implements MediaPlayer.OnPreparedListener {
    private static final String ODTWARZANIE = "com.example.action.PLAY";
    MediaPlayer odtwarzaczAudio = null;

    public int onStartCommand(Intent intencja, int flagi, int idStartu) {

        if (intencja.getAction().equals(ODTWARZANIE)) {
            odtwarzaczAudio = new MediaPlayer();
            odtwarzaczAudio.setOnPreparedListener(this);
            odtwarzaczAudio.prepareAsync();
        }

        return START_NOT_STICKY;
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}