package com.mc.englishlearn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.MenuActivity;
import com.mc.englishlearn.R;

public class EkranStartowyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ekran_startowy);

        int sekundyOpoznione = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //po 500 milisekundach ten blok wywołuje menu główne aplikacji
                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                finish();
            }
        }, sekundyOpoznione * 500);
    }
}