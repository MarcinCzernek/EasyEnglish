package com.mc.englishlearn.nagrywanie;

import static com.mc.englishlearn.nagrywanie.UslugaNagrywarki.ZATRZYMANIE_AKCJI;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mc.englishlearn.R;

import java.io.File;

public class Nagrywarka extends AppCompatActivity {


  private TextView nagranyPlikSciezka;

  public static String NADAWANIE_AUDIO = "com.mc.englishlearn.nagrywanie.audio";
  public static String DANE_DODATKOWE = "com.mc.englishlearn.nagrywanie.audioDane";

  private Button nagrywaniePrzycisk;


  private boolean uruchomionoNagrywanie = false;

  private final static boolean TRYB_DEBUGOWANIA = true;


@Override
public void onCreate (Bundle zapisStanuInstancji){
  super.onCreate(zapisStanuInstancji);
  setContentView(R.layout.nagrywarka);

  nagrywaniePrzycisk = findViewById(R.id.startNagrywaniaPrzycisk);

  nagranyPlikSciezka = findViewById(R.id.infSciezkaPliku);


  LocalBroadcastManager.getInstance(this).registerReceiver(komunikacja, stworzFiltrIntencji());



  nagrywaniePrzycisk.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View widok) {

      if (!uruchomionoNagrywanie) {

        if (ContextCompat.checkSelfPermission(Nagrywarka.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

          ActivityCompat.requestPermissions(Nagrywarka.this, new String[]{Manifest.permission.RECORD_AUDIO},
                  1000);

        } else {

          MediaProjectionManager zarzadzProjekcjiMediow = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

          if (zarzadzProjekcjiMediow != null) {
            startActivityForResult(zarzadzProjekcjiMediow.createScreenCaptureIntent(), 2000);
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


  private final BroadcastReceiver komunikacja = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context kontekst, final Intent intencja) {

      final String dzialanie = intencja.getAction();

      if (NADAWANIE_AUDIO.equals(dzialanie) && intencja.getExtras() != null) {

        final File plik = (File) intencja.getExtras().getSerializable(DANE_DODATKOWE);
        if(plik == null)
          return;

        if (TRYB_DEBUGOWANIA)
          nagranyPlikSciezka.setText(String.format("Scieżka pliku: %s", plik.getAbsolutePath()));

      }
    }
  };


  @Override
  protected void onActivityResult(int kodWymagany, int kodWynik, @Nullable Intent dane) {
    super.onActivityResult(kodWymagany, kodWynik, dane);

    if (kodWynik == Activity.RESULT_OK && kodWymagany == 2000) {
      if (dane != null) {

        Intent intencja = new Intent(this, UslugaNagrywarki.class);

        intencja.putExtra(UslugaNagrywarki.DODATKOWY_KOD, kodWynik);
        intencja.putExtra(UslugaNagrywarki.DODATKOWE_DANE, dane);

        ContextCompat.startForegroundService(this, intencja);

        nagrywaniePrzycisk.setText("Zatrzymaj nagrywanie");

        uruchomionoNagrywanie = true;
      }
    }
  }



  private static IntentFilter stworzFiltrIntencji() {
    final IntentFilter filtrIntencji = new IntentFilter();
    filtrIntencji.addAction(NADAWANIE_AUDIO);
    return filtrIntencji;
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();

    LocalBroadcastManager.getInstance(this).unregisterReceiver(komunikacja);
  }


}
