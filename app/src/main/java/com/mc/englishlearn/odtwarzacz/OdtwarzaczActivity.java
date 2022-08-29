package com.mc.englishlearn.odtwarzacz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OdtwarzaczActivity extends AppCompatActivity {

    RangeSeekBar<Integer> mRangeSeekBar;
    MediaPlayer mMediaPlayer;
    Button playButton, openButton, replayButton;
    static int max;
    static int min;
    SeekBar mSeekBar;
    public static final int PICK_FILE =99;
    ScheduledExecutorService timer;
    TextView title, elapse, mEndTextView;
    String duration;
    Boolean isRepeat = false;
    Runnable mRunnable;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //zdefiniowanie zmiennych obiektów np. przycisk PLAY
        super.onCreate(savedInstanceState);
        setContentView(R.layout.odtwarzacz_aktywnosc);
        openButton = findViewById(R.id.open);
        replayButton = findViewById(R.id.replayButton);
        mRangeSeekBar = findViewById(R.id.rangeSeekBar);
        mSeekBar = findViewById(R.id.seekBar);
        title = (TextView) findViewById(R.id.title);
        elapse = (TextView) findViewById(R.id.elapse);
        playButton = findViewById(R.id.play);
        mHandler = new Handler();
        mRangeSeekBar.setNotifyWhileDragging(true);


        //Odtwórz |> Tutaj jest logika odpowiedzialna za odtwarzanie utworu czyli aktywuje się po naciśnięciu przycisku w odtwarzaczu PLAY:
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        playButton.setText("ODTWÓRZ");
                       // timer.shutdown();
                    } else {
                        mMediaPlayer.start();
                        playButton.setText("PAUZA");//po starcie odtwarzania zmiana tekstu na przycisku na pauze

                        //minutes = (max / 1000) / 60;//konwersja na minuty
                        //seconds = ((max / 1000) % 60);//konwersja na sekundy
                        //duration.setText(minutes + ":" + seconds);


                        String infoMs = String.valueOf(max);
                        Log.i("Max", infoMs);

                        /*
                         *  Zadanie Timer do aktualizacji postępu paska wyszukiwania zgodnie z mediaplayer Aktualna pozycja
                         * */
                        new Timer().scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                                    if (mSeekBar.getProgress() == max || mMediaPlayer.getCurrentPosition() == max) {
                                        if(isRepeat == true) {
                                            mMediaPlayer.seekTo(min);
                                        }else{
                                            mMediaPlayer.seekTo(min);
                                            mMediaPlayer.pause();
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }, 0, 1000);
                    }
                }
            }
            });

        //Przycisk otwarcia pliku audio z dysku
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, PICK_FILE);
            }
        });


        //Podwójny suwak mający ustawiać początek i koniec:
        mRangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                if(mMediaPlayer != null){
                    //mRangeSeekBar.setNotifyWhileDragging(true);
                mMediaPlayer.seekTo(minValue);
                max = maxValue;
                min = minValue;
                //max = mediaPlayer.getCurrentPosition();
                //seekbar.setProgress(max);
                mSeekBar.setMax((int)max);

                String infoMax = String.valueOf(max);
                Log.i("MAX", infoMax);
                String infoMin = String.valueOf(min);
                Log.i("MIN", infoMin);
                }
            }
        });


        //A tutaj za ten zielony pasek z jednym punktem reprezentujący trwanie utworu:
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMediaPlayer != null){
                    int millis = mMediaPlayer.getCurrentPosition();
                    long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
                    long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
                    long secs = total_secs - (mins*60);
                    elapse.setText(mins + ":" + secs + " / " + duration);
                    //Log.i("Duration:", duration);
                    //onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) wywołuje się kiedy postęp kontrolki zostanie zmieniony.
                    // Drugi argument to obecny postęp kontrolki, a trzeci mówi nam o tym czy zmiana została spowodowana przez uzytkownika czy przez program.
                    // Jeśli przez użytkownika to zmienna fromUser jest zainicjowana wartością true.
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                //onStartTrackingTouch(SeekBar seekBar)
                // wywołuje się kiedy użytkownik zacznie zmieniać postęp (przytrzyma suwak)
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                   mMediaPlayer.seekTo(seekBar.getProgress());
                }
                //onStopTrackingTouch(SeekBar seekBar)
                // wywołuje się kiedy użytkownik przestanie zmieniać postęp (puści suwak)
            }
        });

        //funkcja powtórki uruchamiana przyciskiem REPLAY
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMediaPlayer != null) {
                    if (isRepeat) {
                        isRepeat = false;
                        mMediaPlayer.setLooping(false);
                        replayButton.setText("Powtórka wyłączona");
                        Toast.makeText(OdtwarzaczActivity.this, "Tryb powtórki jest wyłączony", Toast.LENGTH_SHORT).show();
                    } else {
                        isRepeat = true;
                        mMediaPlayer.setLooping(true);
                        replayButton.setText("Powtórka włączona");
                        Toast.makeText(OdtwarzaczActivity.this, "Tryb powtórki jest włączony", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        playButton.setEnabled(false);
    }

    //stworzenie aktywności po otwarciu pliku
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

    //stworzenie odtwarzacza po otwarciu pliku
    public void createMediaPlayer(Uri uri){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
            title.setText(getNameFromUri(uri));
            playButton.setEnabled(true);

            mRangeSeekBar.setNotifyWhileDragging(true);

            max = mMediaPlayer.getDuration();
            mRangeSeekBar.setRangeValues(0, mMediaPlayer.getDuration());

          mSeekBar.setMax(mMediaPlayer.getDuration());

            long total_secs = TimeUnit.SECONDS.convert(max, TimeUnit.MILLISECONDS);
            long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
            long secs = total_secs - (mins*60);
            duration = mins + ":" + secs;
            elapse.setText("00:00 / " + duration);
            //mRangeSeekBar.setRangeValues(min, max);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                }
            });
        } catch (IOException e){
            title.setText(e.toString());
        }
    }

    //ta metoda pobiera tytuł otwartego pliku i wyświetla go w odtwarzaczu
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

    //Koniec sesji
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    //koniec odtwarzanego pliku
    public void releaseMediaPlayer(){
        if (timer != null) {
            timer.shutdown();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        playButton.setEnabled(false);
        elapse.setText("TYTUL");
        elapse.setText("00:00 / 00:00");
        mSeekBar.setMax(100);
        mSeekBar.setProgress(0);
    }

}