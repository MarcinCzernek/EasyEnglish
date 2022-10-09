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

public class Odtwarzacz extends AppCompatActivity {


    RangeSeekBar<Integer> pasekZakresu;

    MediaPlayer odtwarzaczAudio;

    Button grajPrzycisk, otworzPrzycisk, powtorkaPrzycisk;

    static int max;

    static int min;

    SeekBar pasekPostepu;


    public static final int PLIK_WYBRANY =99;

    ScheduledExecutorService regCzasowy;

    TextView tytulUtworu, minionyCzas;

    String trwanieUtworu;

    Boolean trybPowtorki = false;

    Handler obsluga;

    @Override
    protected void onCreate(Bundle zapisStanuInstancji) {
        super.onCreate(zapisStanuInstancji);
        setContentView(R.layout.odtwarzacz);

        otworzPrzycisk = findViewById(R.id.otworz);
        powtorkaPrzycisk = findViewById(R.id.powtorkaPrzycisk);
        pasekZakresu = findViewById(R.id.pasekZasiegu);
        pasekPostepu = findViewById(R.id.pasekPostepu);
        tytulUtworu = (TextView) findViewById(R.id.tytulUtworu);
        minionyCzas = (TextView) findViewById(R.id.minionyCzas);
        grajPrzycisk = findViewById(R.id.graj);
        obsluga = new Handler();

        pasekZakresu.setNotifyWhileDragging(true);


        grajPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (odtwarzaczAudio != null) {

                    if (odtwarzaczAudio.isPlaying()) {
                        odtwarzaczAudio.pause();
                        grajPrzycisk.setText("ODTWÓRZ");
                    } else {

                        odtwarzaczAudio.start();
                        grajPrzycisk.setText("PAUZA");
                        String infoMs = String.valueOf(max);
                        Log.i("Max", infoMs);


                        new Timer().scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    pasekPostepu.setProgress(odtwarzaczAudio.getCurrentPosition());

                                    if (pasekPostepu.getProgress() == max || odtwarzaczAudio.getCurrentPosition() == max) {
                                        if(trybPowtorki == true) {
                                            odtwarzaczAudio.seekTo(min);
                                        }else{
                                            odtwarzaczAudio.seekTo(min);
                                            odtwarzaczAudio.pause();
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

        otworzPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent otwarciePliku = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                otwarciePliku.addCategory(Intent.CATEGORY_OPENABLE);

                otwarciePliku.setType("audio/*");

                startActivityForResult(otwarciePliku, PLIK_WYBRANY);
            }
        });

        pasekZakresu.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minWartosc, Integer maxWartosc) {
                if(odtwarzaczAudio != null){
                    odtwarzaczAudio.seekTo(minWartosc);

                max = maxWartosc;
                min = minWartosc;

                pasekPostepu.setMax((int)max);

                String infoMin = String.valueOf(min);
                Log.i("MIN", infoMin);
                String infoMax = String.valueOf(max);
                Log.i("MAX", infoMax);
                }
            }
        });


        pasekPostepu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar pasekPostepu, int postep, boolean uzytkownik) {
                if (odtwarzaczAudio != null){
                    int pomiarCzasu = odtwarzaczAudio.getCurrentPosition();
                    long maxSekund = TimeUnit.SECONDS.convert(pomiarCzasu, TimeUnit.MILLISECONDS);
                    long minuty = TimeUnit.MINUTES.convert(maxSekund, TimeUnit.SECONDS);
                    long sekundy = maxSekund - (minuty*60);
                    minionyCzas.setText(minuty + ":" + sekundy + " / " + trwanieUtworu);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar pasekPostepu) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar pasekPostepu) {
                if (odtwarzaczAudio != null) {
                    odtwarzaczAudio.seekTo(pasekPostepu.getProgress());
                }
            }
        });

        powtorkaPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (odtwarzaczAudio != null) {
                    if (trybPowtorki) {
                        trybPowtorki = false;

                        odtwarzaczAudio.setLooping(false);
                        powtorkaPrzycisk.setText("Powtórka wyłączona");
                        Toast.makeText(Odtwarzacz.this, "Tryb powtórki jest wyłączony", Toast.LENGTH_SHORT).show();
                    } else {
                        trybPowtorki = true;

                        odtwarzaczAudio.setLooping(true);
                        powtorkaPrzycisk.setText("Powtórka włączona");
                        Toast.makeText(Odtwarzacz.this, "Tryb powtórki jest włączony", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        grajPrzycisk.setEnabled(false);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        zwolnienieOdtwarzacza();
    }


    public void zwolnienieOdtwarzacza(){

        if (regCzasowy != null) {
            regCzasowy.shutdown();
        }

        if (odtwarzaczAudio != null) {
            odtwarzaczAudio.release();
            odtwarzaczAudio = null;
        }

        grajPrzycisk.setEnabled(false);


        pasekPostepu.setProgress(0);
        pasekPostepu.setMax(100);


        minionyCzas.setText("TYTUL");
        minionyCzas.setText("00:00 / 00:00");
    }



    @Override
    protected void onActivityResult(int zadanie, int wynik, @Nullable Intent dane) {
        super.onActivityResult(zadanie, wynik, dane);

        if (zadanie == PLIK_WYBRANY && wynik == RESULT_OK){
            if (dane != null){
                Uri uri = dane.getData();

                stworzOdtwAudio(uri);
            }
        }
    }

    public void stworzOdtwAudio(Uri uri){

        odtwarzaczAudio = new MediaPlayer();
        odtwarzaczAudio.setAudioAttributes(

                new AudioAttributes.Builder()

                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)

                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            odtwarzaczAudio.setDataSource(getApplicationContext(), uri);
            odtwarzaczAudio.prepare();

            tytulUtworu.setText(pobierzTytul(uri));
            grajPrzycisk.setEnabled(true);

            pasekZakresu.setNotifyWhileDragging(true);

            max = odtwarzaczAudio.getDuration();
            pasekZakresu.setRangeValues(0, odtwarzaczAudio.getDuration());

          pasekPostepu.setMax(odtwarzaczAudio.getDuration());

            long sumaSekund = TimeUnit.SECONDS.convert(max, TimeUnit.MILLISECONDS);
            long minuty = TimeUnit.MINUTES.convert(sumaSekund, TimeUnit.SECONDS);
            long sekundy = sumaSekund - (minuty*60);
            trwanieUtworu = minuty + ":" + sekundy;
            minionyCzas.setText("00:00 / " + trwanieUtworu);

            odtwarzaczAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    zwolnienieOdtwarzacza();
                }
            });
        } catch (IOException e){
            tytulUtworu.setText(e.toString());
        }
    }


    @SuppressLint("Range")
    public String pobierzTytul(Uri uri){
        String nazwaPliku = "";
        Cursor kursor = null;

        kursor = getContentResolver().query(uri, new String[]{
                MediaStore.Images.ImageColumns.DISPLAY_NAME
        }, null, null, null);

        if (kursor != null && kursor.moveToFirst()) {

            nazwaPliku = kursor.getString(kursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        }

        if (kursor != null) {
            kursor.close();
        }

        return nazwaPliku;
    }



}