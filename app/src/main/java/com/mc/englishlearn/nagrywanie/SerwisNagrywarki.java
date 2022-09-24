package com.mc.englishlearn.nagrywanie;

import static com.mc.englishlearn.nagrywanie.Nagrywarka.DANE_DODATKOWE;
import static com.mc.englishlearn.nagrywanie.Nagrywarka.NADAWANIE_AUDIO;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mc.englishlearn.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SerwisNagrywarki extends Service {

    public static final int ID_POWIADOMIENIA = 1000;

    public static final String POWIADOMIENIE_NAGRYWANIU_KANAŁU = "kanalNagrywania";

    private final static int ZATRZYMANIE_ZADANIA = 1;

    public final static String ZATRZYMANIE_AKCJI = "com.mc.englishlearn.recording.stop";

    private final static int ZADANIE_OTWARCIA_AKTYWNOSCI = 2;

    private final static int MASKA_KANAŁU_AUDIO = AudioFormat.CHANNEL_IN_MONO;
    private final static int KODOWANIE_AUDIO = AudioFormat.ENCODING_PCM_16BIT;
    private static int CZESTOTLIWOSC_PROBKOWANIA = 44100;

    public final static String DODATKOWY_KOD = "kod";
    public final static String DODATKOWE_DANE = "dane";

    MediaProjectionManager zarzadzanieProjekcja;
    AudioRecord nagrywarka;
    MediaProjection projekcja;

    Intent daneNagrywarki;

    int kodNagrywarki = 114;

    boolean stanNagrywarki = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(){
        super.onCreate();

        AudioManager zarzadzAudio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (zarzadzAudio != null) {
            String wskaznik = zarzadzAudio.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            CZESTOTLIWOSC_PROBKOWANIA = Integer.parseInt(wskaznik);
        }

        final NotificationChannel kanal = new NotificationChannel(POWIADOMIENIE_NAGRYWANIU_KANAŁU, "Nagrywanie", NotificationManager.IMPORTANCE_HIGH);
        kanal.setDescription("Powiadomienie związane z nagrywaniem.");
        kanal.setShowBadge(false);
        kanal.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        final NotificationManager zarzdzPowiadomieniem = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (zarzdzPowiadomieniem != null) {
            zarzdzPowiadomieniem.createNotificationChannel(kanal);
        }

        final IntentFilter filtr = new IntentFilter();
        filtr.addAction(ZATRZYMANIE_AKCJI);
        registerReceiver(zatrzymajTransmisje, filtr);


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(zatrzymajTransmisje);
    }

    @Override
    public int onStartCommand(Intent intencja, int flagi, int idStartu) {
        daneNagrywarki = intencja.getParcelableExtra(DODATKOWE_DANE);
        kodNagrywarki = intencja.getIntExtra(DODATKOWY_KOD, 114);

        final Notification powiadomienie = utworzPowiadomienie();
        startForeground(ID_POWIADOMIENIA, powiadomienie);
        startNagrywania();
        return START_STICKY;
    }

    private Notification utworzPowiadomienie() {
        final Intent cel = new Intent(this, Nagrywarka.class);
        final Intent admin = new Intent(this, Nagrywarka.class);

        admin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent zatrzymanie = new Intent(ZATRZYMANIE_AKCJI);

        final PendingIntent zatrzymanieZadania = PendingIntent.getBroadcast(this, ZATRZYMANIE_ZADANIA, zatrzymanie, PendingIntent.FLAG_UPDATE_CURRENT);

        final PendingIntent intencjaOczekujaca = PendingIntent.getActivities(this, ZADANIE_OTWARCIA_AKTYWNOSCI, new Intent[]{admin, cel}, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder konstruktor = new NotificationCompat.Builder(this, POWIADOMIENIE_NAGRYWANIU_KANAŁU);
        konstruktor.setPriority(NotificationCompat.PRIORITY_MAX);
        konstruktor.setContentIntent(intencjaOczekujaca);
        konstruktor.setColor(ContextCompat.getColor(this, R.color.gold));
        konstruktor.setSmallIcon(R.drawable.nagrywanie);
        konstruktor.setContentTitle("Nagrywanie Audio").setContentText("Trwa nagrywanie!");
        konstruktor.addAction(new NotificationCompat.Action(R.drawable.zatrzymanie, "Zatrzymaj nagrywanie", zatrzymanieZadania));
        konstruktor.setOnlyAlertOnce(true).setShowWhen(true).setDefaults(0).setAutoCancel(true).setOngoing(true);
        return konstruktor.build();
    }


    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.O)
    void startNagrywania() {
        zarzadzanieProjekcja = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (zarzadzanieProjekcja == null)
            return;

        projekcja = zarzadzanieProjekcja.getMediaProjection(kodNagrywarki, daneNagrywarki);

        AudioPlaybackCaptureConfiguration konfiguracja = new AudioPlaybackCaptureConfiguration.Builder(projekcja)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        nagrywarka = new AudioRecord.Builder().setAudioFormat(
                        new AudioFormat.Builder()
                                .setEncoding(KODOWANIE_AUDIO)
                                .setSampleRate(CZESTOTLIWOSC_PROBKOWANIA)
                                .setChannelMask(MASKA_KANAŁU_AUDIO)
                                .build())
                .setAudioPlaybackCaptureConfig(konfiguracja)
                .build();

        nagrywarka.startRecording();

        stanNagrywarki = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    zapiszPlik();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void zatrzymanieNagrywania() {
        try {
            stanNagrywarki = false;
            if (nagrywarka.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                nagrywarka.stop();
            if (nagrywarka.getState() == AudioRecord.STATE_INITIALIZED)
                nagrywarka.release();
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zapiszPlik() throws IOException {
        final int ROZMIAR_BUFORA = 2 * AudioRecord.getMinBufferSize(CZESTOTLIWOSC_PROBKOWANIA, MASKA_KANAŁU_AUDIO, KODOWANIE_AUDIO);
        byte[] bufor = new byte[ROZMIAR_BUFORA];
        int odczyt;
        long suma = 0;

        File plikWAV = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "nagranie_" + System.currentTimeMillis() / 1000 + ".wav");
        plikWAV.getParentFile().mkdir();
        plikWAV.createNewFile();

        Log.d("test", "utworzone pliki: " + plikWAV.getAbsolutePath());
        FileOutputStream wyjsciePlikuWAV = new FileOutputStream(plikWAV);
        zapisWavHeader(wyjsciePlikuWAV, MASKA_KANAŁU_AUDIO, CZESTOTLIWOSC_PROBKOWANIA, KODOWANIE_AUDIO);

        while (stanNagrywarki) {
            odczyt = nagrywarka.read(bufor, 0, bufor.length);

            if (suma + odczyt > 4294967295L) {

                for (int i = 0; i < odczyt && suma <= 4294967295L; i++, suma++) {
                    wyjsciePlikuWAV.write(bufor[i]);
                }
                stanNagrywarki = false;
            } else {

                wyjsciePlikuWAV.write(bufor, 0, odczyt);
                suma += odczyt;
            }
        }
        try {
            wyjsciePlikuWAV.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        aktualizacjaWavHeader(plikWAV);

        final Intent transmisja = new Intent(NADAWANIE_AUDIO);
        transmisja.putExtra(DANE_DODATKOWE, plikWAV);
        LocalBroadcastManager.getInstance(this).sendBroadcast(transmisja);
    }

    private static void zapisWavHeader(OutputStream strumienWyjsciowy, short kanalyLiczba, int czestProbkowania, short rozdzBitowa) throws IOException {
        byte[] mniejszeBajty = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(kanalyLiczba)
                .putInt(czestProbkowania)
                .putInt(czestProbkowania * kanalyLiczba * (rozdzBitowa / 8))
                .putShort((short) (kanalyLiczba * (rozdzBitowa / 8)))
                .putShort(rozdzBitowa)
                .array();
        strumienWyjsciowy.write(new byte[]{
                'R', 'I', 'F', 'F',
                0, 0, 0, 0,
                'W', 'A', 'V', 'E',
                'f', 'm', 't', ' ',
                16, 0, 0, 0,
                1, 0,
                mniejszeBajty[0], mniejszeBajty[1],
                mniejszeBajty[2], mniejszeBajty[3], mniejszeBajty[4], mniejszeBajty[5],
                mniejszeBajty[6], mniejszeBajty[7], mniejszeBajty[8], mniejszeBajty[9],
                mniejszeBajty[10], mniejszeBajty[11],
                mniejszeBajty[12], mniejszeBajty[13],
                'd', 'a', 't', 'a',
                0, 0, 0, 0,
        });
    }

    private static void aktualizacjaWavHeader(File plikWAV) throws IOException {
        byte[] formaty = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) (plikWAV.length() - 8))
                .putInt((int) (plikWAV.length() - 44))
                .array();

        RandomAccessFile dostepAudio = null;

        try {
            dostepAudio = new RandomAccessFile(plikWAV, "rw");
            dostepAudio.seek(4);
            dostepAudio.write(formaty, 0, 4);
            dostepAudio.seek(40);
            dostepAudio.write(formaty, 4, 4);
        } catch (IOException wyjatek) {

            throw wyjatek;
        } finally {
            if (dostepAudio != null) {
                try {
                    dostepAudio.close();
                } catch (IOException wyjatek) {

                }
            }
        }
    }

    private static void zapisWavHeader(OutputStream strumienWyjsciowy, int maskaKanalu, int czestProbkowania, int kodowanie) throws IOException {
        short kanaly;
        switch (maskaKanalu) {
            case AudioFormat.CHANNEL_IN_MONO:
                kanaly = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                kanaly = 2;
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowa maska kanału");
        }

        short rozdzBitowa;
        switch (kodowanie) {
            case AudioFormat.ENCODING_PCM_8BIT:
                rozdzBitowa = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                rozdzBitowa = 16;
                break;
            case AudioFormat.ENCODING_PCM_FLOAT:
                rozdzBitowa = 32;
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowe enkodowanie");
        }

        zapisWavHeader(strumienWyjsciowy, kanaly, czestProbkowania, rozdzBitowa);
    }

    private final BroadcastReceiver zatrzymajTransmisje= new BroadcastReceiver() {
        @Override
        public void onReceive(final Context kontekst, final Intent intencja) {
            zatrzymanieNagrywania();
        }
    };


}
