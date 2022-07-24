package com.mc.englishlearn.audioplayer;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.database.Cursor;

import android.media.AudioAttributes;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.R;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

     MediaPlayer mediaPlayer;
     Button play, replay, open;
     SeekBar seekBar;
     TextView title, elapse;
     String duration;
     ScheduledExecutorService timer;
     public static final int PICK_FILE =99;
     boolean isRepeat = false;



    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        play = (Button)findViewById(R.id.play);
        replay = (Button) findViewById(R.id.replay);
        open = (Button) findViewById(R.id.open);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        title = (TextView) findViewById(R.id.title);
        elapse = (TextView) findViewById(R.id.elapsed);

        //Otwieranie pliku dzwiękowego
                    open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("audio/*");
                            startActivityForResult(intent, PICK_FILE);

                        }
                    });

                    //odtwarzanie pliku
                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(mediaPlayer != null){
                                if(mediaPlayer.isPlaying()){
                                    mediaPlayer.pause();
                                    play.setText("ODTWÓRZ");
                                    timer.shutdown();
                                }else{
                                    mediaPlayer.start();
                                    play.setText("PAUZA");

                                    timer = Executors.newScheduledThreadPool(1);
                                    timer.scheduleAtFixedRate(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mediaPlayer != null) {
                                                if (!seekBar.isPressed()) {
                                                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                                }
                                            }
                                        }
                                    },10,10, TimeUnit.MILLISECONDS);
                                }
                            }
                        }
                    });

                    //Audio line
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if (mediaPlayer != null){
                                int millis = mediaPlayer.getCurrentPosition();
                                long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
                                long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
                    long secs = total_secs - (mins*60);
                    elapse.setText(mins + ":" + secs + " / " + duration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        //Loop
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //repeat = false
                if(isRepeat){
                    isRepeat = false;
                    mediaPlayer.setLooping(false);
                    Toast.makeText(PlayerActivity.this, "Tryb powtórki jest wyłączony", Toast.LENGTH_SHORT).show();
                }else{
                    isRepeat = true;
                    mediaPlayer.setLooping(true);
                    Toast.makeText(PlayerActivity.this, "Tryb powtórki jest włączony", Toast.LENGTH_SHORT).show();
                }

                   //mediaPlayer.setLooping(true);
                  // Toast.makeText(PlayerActivity.this, "Repeat if ON", Toast.LENGTH_SHORT).show();
            }
        });

        play.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                createMediaPlayer(uri);
            }
        }
    }


    //Creating audio player
    public void createMediaPlayer(Uri uri){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();

            title.setText(getNameFromUri(uri));
            play.setEnabled(true);

            int millis = mediaPlayer.getDuration();
            long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
            long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
            long secs = total_secs - (mins*60);
            duration = mins + ":" + secs;
            elapse.setText("00:00 / " + duration);
            seekBar.setMax(millis);
            seekBar.setProgress(0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();

                }
            });
        } catch (IOException e){
            title.setText(e.toString());
        }
    }

    @SuppressLint("Range")
    public String getNameFromUri(Uri uri){
        String fileName = "";
        Cursor cursor = null;
        cursor = getContentResolver().query(uri, new String[]{
                MediaStore.Images.ImageColumns.DISPLAY_NAME
        }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        }
        if (cursor != null) {
            cursor.close();
        }
        return fileName;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    public void releaseMediaPlayer(){
        if (timer != null) {
            timer.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        play.setEnabled(false);
        elapse.setText("TYTUL");
        elapse.setText("00:00 / 00:00");
        seekBar.setMax(100);
        seekBar.setProgress(0);
    }
}


/*
    protected void run(){
        int playerStopPoint = mediaPlayer.getCurrentPosition();
        int playerTotal = seekBar.getMax();
        int playerPosition = seekBar.getProgress();
        while (mediaPlayer != null && playerPosition < playerTotal){
            try{
                Thread.sleep(500); //
                if(mediaPlayer.isPlaying()){
                    playerPosition = mediaPlayer.getCurrentPosition();
                    if(playerPosition == playerStopPoint){
                        mediaPlayer.stop();
                        break;
                    }
                }
            }catch(InterruptedException e){
                return;
            }catch(Exception e){
                return;
            }
        }
 */