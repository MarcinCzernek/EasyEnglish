package com.mc.englishlearn;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.nagrywanie.NagrywarkaMainActivity;
import com.mc.englishlearn.odtwarzacz.OdtwarzaczActivity;
import com.mc.englishlearn.przypomnienia.PrzypomnienieMenuActivity;


public class MenuActivity extends AppCompatActivity {

    Button wyjsciePrzycisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_aktywnosc);

        wyjsciePrzycisk = findViewById(R.id.wyjscie);

        wyjsciePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
    }

    //Odtwarzacz
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewIntentPlayer (View view){
        Intent i = new Intent(this, OdtwarzaczActivity.class);
        startActivity(i);
        Context context = getApplicationContext();
        Intent intent = new Intent(this, OdtwarzaczActivity.class); // Zbudowanie intencji dla usługi
        context.startForegroundService(intent);
    }

    //Kalendarz
    public void createNewIntentReminder (View view){
        Intent i = new Intent(this, PrzypomnienieMenuActivity.class);
        startActivity(i);
    }

    //Nagrywarka
    public void createNewIntentRecorder (View view){
        Intent i = new Intent(this, NagrywarkaMainActivity.class);
        startActivity(i);
    }
}