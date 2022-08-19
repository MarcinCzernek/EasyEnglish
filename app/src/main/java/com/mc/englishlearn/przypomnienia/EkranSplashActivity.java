package com.mc.englishlearn.przypomnienia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.R;

public class EkranSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ekran_splash);

        int sekundyOpoznione = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(getApplicationContext(), PrzypomnienieMenuActivity.class));             //po 500 milisekundach ten blok wywołuje przypomnienieMainActivity
                finish();
            }
        }, sekundyOpoznione * 500);
    }
}