package com.mc.englishlearn.nagrywanie;
//Spolszczenie w toku ...

import static com.mc.englishlearn.nagrywanie.NagrywarkaMainActivity.EMISJA_DODATKOWYCH_DANYCH;
import static com.mc.englishlearn.nagrywanie.NagrywarkaMainActivity.EMISJA_FALI_DZWIEKOWYCH;

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
    public static final String POWIADOMIENIE_NAGRYWANIU_KANAŁU = "channel_recording";

    //stała inf o zatrzymaniu żądania
    private final static int ZATRZYAMNIE_ZADANIA = 1;
    public final static String ZATRZYMANIE_AKCJI = "com.mc.englishlearn.recording.stop";

    private final static int ZADANIE_OTWARCIA_AKTYWNOSCI = 2;

    private static int CZESTOTLIWOSC_PROBKOWANIA = 44100;
    private final static int KODOWANIE_AUDIO = AudioFormat.ENCODING_PCM_16BIT;
    private final static int MASKA_KANAŁU_AUDIO = AudioFormat.CHANNEL_IN_MONO;

    public final static String DODATKOWY_KOD = "code";
    public final static String DODATKOWE_DANE = "data";

    AudioRecord nagrywarka;
    MediaProjectionManager zarządzanieProjekcjaMediow;
    MediaProjection projekcjaMediow;

    Intent daneNagrywarki;

    int kodNagrywarki = 114;

    boolean stanNagrywarki = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(){
        super.onCreate();

        // Celem jest aby uzyskać preferowany rozmiar bufora i częstotliwość próbkowania
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            CZESTOTLIWOSC_PROBKOWANIA = Integer.parseInt(rate);
        }

        final NotificationChannel channel = new NotificationChannel(POWIADOMIENIE_NAGRYWANIU_KANAŁU, "Nagrywanie", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Powiadomienie związane z nagrywaniem.");
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ZATRZYMANIE_AKCJI);
        registerReceiver(stopBroadcastReceiver, filter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        daneNagrywarki = intent.getParcelableExtra(DODATKOWE_DANE);
        kodNagrywarki = intent.getIntExtra(DODATKOWY_KOD, 114);

        final Notification notification = utworzPowiadomienie();
        startForeground(ID_POWIADOMIENIA, notification);
        startNagrywania();
        return START_STICKY;
    }

    private Notification utworzPowiadomienie() {
        final Intent parentIntent = new Intent(this, NagrywarkaMainActivity.class);
        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent targetIntent = new Intent(this, NagrywarkaMainActivity.class);

        final Intent disconnect = new Intent(ZATRZYMANIE_AKCJI);
        final PendingIntent disconnectAction = PendingIntent.getBroadcast(this, ZATRZYAMNIE_ZADANIA, disconnect, PendingIntent.FLAG_UPDATE_CURRENT);

        //Obydwa powyższe działania mają zaznaczone launchMode="singleTask" w pliku AndroidManifest.xml, więc jeśli zadanie jest już uruchomione, zostanie wznowione
        final PendingIntent pendingIntent = PendingIntent.getActivities(this, ZADANIE_OTWARCIA_AKTYWNOSCI, new Intent[]{parentIntent, targetIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, POWIADOMIENIE_NAGRYWANIU_KANAŁU);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setContentTitle("Nagrywanie Audio").setContentText("Trwa nagrywanie!");
        builder.setSmallIcon(R.drawable.ic_notifcation_record);
        builder.setColor(ContextCompat.getColor(this, R.color.colorRecording));
        builder.setOnlyAlertOnce(true).setShowWhen(true).setDefaults(0).setAutoCancel(true).setOngoing(true);
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_notifcation_stop, "Zatrzymaj nagrywanie", disconnectAction));

        return builder.build();
    }

    //Funkcja rozpoczynająca nagrywanie
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.O)
    void startNagrywania() {
        zarządzanieProjekcjaMediow = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (zarządzanieProjekcjaMediow == null)
            return;

        projekcjaMediow = zarządzanieProjekcjaMediow.getMediaProjection(kodNagrywarki, daneNagrywarki);

        AudioPlaybackCaptureConfiguration config = new AudioPlaybackCaptureConfiguration.Builder(projekcjaMediow)
                .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                .build();

        nagrywarka = new AudioRecord.Builder().setAudioFormat(
                        new AudioFormat.Builder()
                                .setEncoding(KODOWANIE_AUDIO)
                                .setSampleRate(CZESTOTLIWOSC_PROBKOWANIA)
                                .setChannelMask(MASKA_KANAŁU_AUDIO)
                                .build())
                .setAudioPlaybackCaptureConfig(config)
                .build();

        nagrywarka.startRecording();

        stanNagrywarki = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Uruchamia dowolny kod w tle, który tutaj potrzebuję
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
        final int BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(CZESTOTLIWOSC_PROBKOWANIA, MASKA_KANAŁU_AUDIO, KODOWANIE_AUDIO);

        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        long total = 0;

        File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "nagranie_" + System.currentTimeMillis() / 1000 + ".wav");
        wavFile.getParentFile().mkdir();
        wavFile.createNewFile();

        Log.d("test", "utworzone pliki: " + wavFile.getAbsolutePath());
        FileOutputStream wavOut = new FileOutputStream(wavFile);
        // Piszę nagłówek pliku wav
        zapisWavHeader(wavOut, MASKA_KANAŁU_AUDIO, CZESTOTLIWOSC_PROBKOWANIA, KODOWANIE_AUDIO);

        while (stanNagrywarki) {
            read = nagrywarka.read(buffer, 0, buffer.length);

            // WAVs cannot be > 4 GB due to the use of 32 bit unsigned integers.
            if (total + read > 4294967295L) {
                // Write as many bytes as we can before hitting the max size
                for (int i = 0; i < read && total <= 4294967295L; i++, total++) {
                    wavOut.write(buffer[i]);
                }
                stanNagrywarki = false;
            } else {
                // Wypisuję cały bufor odczytu
                wavOut.write(buffer, 0, read);
                total += read;
            }
        }
        try {
            wavOut.close();
        } catch (IOException ex) {
            //
            ex.printStackTrace();
        }
        aktualizacjaWavHeader(wavFile);
        //Transmisja pomiarowa
        final Intent broadcast = new Intent(EMISJA_FALI_DZWIEKOWYCH);
        broadcast.putExtra(EMISJA_DODATKOWYCH_DANYCH, wavFile);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    /**
     * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
     * Two size fields are left empty/null since we do not yet know the final stream size
     *
     * @param out         The stream to write the header to
     * @param channelMask An AudioFormat.CHANNEL_* mask
     * @param sampleRate  The sample rate in hertz
     * @param encoding    An AudioFormat.ENCODING_PCM_* value
     * @throws IOException
     */
    private static void zapisWavHeader(OutputStream out, int channelMask, int sampleRate, int encoding) throws IOException {
        short channels;
        switch (channelMask) {
            case AudioFormat.CHANNEL_IN_MONO:
                channels = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                channels = 2;
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowa maska kanału");
        }

        short bitDepth;
        switch (encoding) {
            case AudioFormat.ENCODING_PCM_8BIT:
                bitDepth = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitDepth = 16;
                break;
            case AudioFormat.ENCODING_PCM_FLOAT:
                bitDepth = 32;
                break;
            default:
                throw new IllegalArgumentException("Nieprawidłowe kodowanie");
        }

        zapisWavHeader(out, channels, sampleRate, bitDepth);
    }

    /**
     * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
     * Two size fields are left empty/null since we do not yet know the final stream size
     *
     * @param out        The stream to write the header to
     * @param channels   The number of channels
     * @param sampleRate The sample rate in hertz
     * @param bitDepth   The bit depth
     * @throws IOException
     */
    private static void zapisWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
        // Konwersja wielobajtowych liczb całkowitych (int) na surowe bajty w formacie little endian, zgodnie z wymaganiami specyfikacji
        byte[] littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels * (bitDepth / 8))
                .putShort((short) (channels * (bitDepth / 8)))
                .putShort(bitDepth)
                .array();

        // Niekoniecznie najlepszy, ale bardzo łatwy do zwizualizowania sposób
        out.write(new byte[]{
                // RIFF header
                'R', 'I', 'F', 'F', // ChunkID
                0, 0, 0, 0, // ChunkSize (musi zostać uaktualniony później)
                'W', 'A', 'V', 'E', // Format
                // fmt subchunk
                'f', 'm', 't', ' ', // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample
                // data subchunk
                'd', 'a', 't', 'a', // Subchunk2ID
                0, 0, 0, 0, // Subchunk2Size (musi zostać uaktualniony później)
        });
    }

    /**
     * Aktualizuje nagłówek danego pliku wav, aby uwzględnić ostateczne rozmiary porcji
     *
     * @param wav Plik wav do aktualizacji
     * @throws IOException
     */
    private static void aktualizacjaWavHeader(File wav) throws IOException {
        byte[] sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // Prawdopodobnie istnieje wiele różnych/lepszych sposobów obliczania
                // tych dwóch, biorąc pod uwagę okoliczności. Obsada powinna być bezpieczna, ponieważ jeśli WAV jest
                // > 4 GB już popełniliśmy wielki błąd.
                .putInt((int) (wav.length() - 8)) // ChunkSize
                .putInt((int) (wav.length() - 44)) // Subchunk2Size
                .array();

        RandomAccessFile accessWave = null;
        //noinspection CaughtExceptionImmediatelyRethrown
        try {
            accessWave = new RandomAccessFile(wav, "rw");
            // ChunkSize
            accessWave.seek(4);
            accessWave.write(sizes, 0, 4);

            // Subchunk2Size
            accessWave.seek(40);
            accessWave.write(sizes, 4, 4);
        } catch (IOException ex) {
            // Rethrow ale nadal zamykam accessWave w naszym finale
            throw ex;
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close();
                } catch (IOException ex) {
                    //
                }
            }
        }
    }
    /**
     * Ten broadcast receiver nasłuchuje na {@link #ZATRZYMANIE_AKCJI} który może zostać zwolniony poprzez nacisnięcie Stop przycisk akcji na powiadomieniu
     */
    private final BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            zatrzymanieNagrywania();
        }
    };


}
