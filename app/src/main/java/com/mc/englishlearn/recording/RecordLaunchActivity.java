package com.mc.englishlearn.recording;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.mc.englishlearn.R;

public class RecordLaunchActivity extends AppCompatActivity {

    private Button btnStart, btnStop, btnPlay, btnStopPlay;

    private TextView tvStatus;

    private MediaRecorder mRecorder;

    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;

    // constant for audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        // get views by id
        tvStatus = findViewById(R.id.tvStatus);
        btnStart = findViewById(R.id.btn_start_rec);
        btnStop = findViewById(R.id.btn_stop_rec);
        btnPlay = findViewById(R.id.btn_play_rec);
        btnStopPlay = findViewById(R.id.btn_stop_playing);

        //setting buttons background color
        btnStop.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnStopPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start audio recording.
                startRecording();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop audio recording.
                stopRecording();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // play the recorded audio
                playAudio();
            }
        });
        btnStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop playing the recorded audio
                stopPlaying();
            }
        });
    }

    private void startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record and store the audio.
        if (CheckPermissions()) {

            //setting buttons background color
            btnStop.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnStart.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnStopPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            //initializing filename variable
            // with the path of the recorded audio file.
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName += "/NagranieAudio.wav";


            //initializing media recorder class
            mRecorder = new MediaRecorder();

            //Sets the audio source to be used for recording.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // set the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // set the audio encoder for recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            //set the output file location for recorded audio
            mRecorder.setOutputFile(mFileName);
            try {

                //Prepares the recorder to begin capturing and encoding data.
                mRecorder.prepare();

            } catch (IOException e) {

                Log.e("TAG", "prepare() failed");
            }

            // start the audio recording.
            mRecorder.start();

            tvStatus.setText("Nagrywanie rozpoczęte");
        } else {

            // if audio recording permissions are
            // not granted by user this method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Pozwolenie przyznane", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Pozwolenie odrzucone", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(RecordLaunchActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    //play the recorded audio
    public void playAudio() {
        btnStop.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnStart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnStopPlay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // using media player class for playing recorded audio
        mPlayer = new MediaPlayer();
        try {

            // set the data source which will be our file name
            mPlayer.setDataSource(mFileName);

            //prepare media player
            mPlayer.prepare();

            // start media player.
            mPlayer.start();
            tvStatus.setText("Odtwarzanie nagrywanie rozpoczęte");

        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    public void stopRecording() {
        btnStop.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnStart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnPlay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnStopPlay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // stop the audio recording.
        mRecorder.stop();

        // release the media recorder object.
        mRecorder.release();
        mRecorder = null;

        tvStatus.setText("Nagrywanie zakończone");
    }

    public void stopPlaying() {

        // release the media player object
        // and stop playing recorded audio.
        mPlayer.release();
        mPlayer = null;
        btnStop.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btnStart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnPlay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnStopPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        tvStatus.setText("Odtwarzanie nagrywania zatrzymane");
    }

    @Override
    public void onStop() {
        super.onStop();
        // releasing the media player and the media recorder object
        // i ustawienie go na wartość null
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

}

