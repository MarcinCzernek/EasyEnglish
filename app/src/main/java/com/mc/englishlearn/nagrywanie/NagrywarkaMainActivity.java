package com.mc.englishlearn.nagrywanie;

import static com.mc.englishlearn.nagrywanie.SerwisNagrywarki.ZATRZYMANIE_AKCJI;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mc.englishlearn.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NagrywarkaMainActivity extends AppCompatActivity {

private final static boolean DEBUGOWANIE = true;
public static String EMISJA_FALI_DZWIEKOWYCH = "com.mc.englishlearn.recording.waveform";
public static String EMISJA_DODATKOWYCH_DANYCH = "com.mc.englishlearn.recording.waveform_data";
//private SoundWaveView audioVisualizerView;
private Button nagrywaniePrzycisk;
private boolean uruchomionoNagrywanie = false;
private TextView sciezkaNagranegoPliku;

@Override
public void onCreate (Bundle savedInstanceState){
  super.onCreate(savedInstanceState);
  setContentView(R.layout.nagrywarka_aktywnosc);

  sciezkaNagranegoPliku = findViewById(R.id.infSciezkaPliku);
  nagrywaniePrzycisk = findViewById(R.id.startNagrywaniaPrzycisk);

  LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, makeIntentFilter());

  nagrywaniePrzycisk.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View v) {
      if (!uruchomionoNagrywanie) {
        if (ContextCompat.checkSelfPermission(NagrywarkaMainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(NagrywarkaMainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                  1000);
        } else {
          MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
          if (mediaProjectionManager != null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 2000);
          }
        }
      } else {
        final Intent transmisja = new Intent(ZATRZYMANIE_AKCJI);
        sendBroadcast(transmisja);
        nagrywaniePrzycisk.setText("Rozpocznij nagrywanie");
        uruchomionoNagrywanie = false;
      }
    }
  });
}

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == 2000) {
      if (data != null) {
        Intent intent = new Intent(this, SerwisNagrywarki.class);
        intent.putExtra(SerwisNagrywarki.DODATKOWY_KOD, resultCode);
        intent.putExtra(SerwisNagrywarki.DODATKOWE_DANE, data);

        ContextCompat.startForegroundService(this, intent);

        nagrywaniePrzycisk.setText("Zatrzymaj nagrywanie");
        uruchomionoNagrywanie = true;
      }
    }
  }

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, final Intent intent) {
      final String action = intent.getAction();
      if (EMISJA_FALI_DZWIEKOWYCH.equals(action) && intent.getExtras() != null) {
        final File file = (File) intent.getExtras().getSerializable(EMISJA_DODATKOWYCH_DANYCH);
        if(file == null)
          return;

        if (DEBUGOWANIE)
          sciezkaNagranegoPliku.setText(String.format("Scieżka pliku: %s", file.getAbsolutePath()));

        Uri uri = FileProvider.getUriForFile(NagrywarkaMainActivity.this, getApplicationContext().getPackageName() + ".provider", file);
        //audioVisualizerView.addAudioFileUri(uri);
      }
    }
  };

  private static IntentFilter makeIntentFilter() {
    final IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(EMISJA_FALI_DZWIEKOWYCH);
    return intentFilter;
  }

  public static byte[] zamianaPlikuNaBity(File file) {
    int size = (int) file.length();
    byte[] bytes = new byte[size];
    try {
      BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
      buf.read(bytes, 0, bytes.length);
      buf.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bytes;
  }


}
