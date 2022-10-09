package com.mc.englishlearn;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.nagrywanie.Nagrywarka;
import com.mc.englishlearn.odtwarzacz.Odtwarzacz;
import com.mc.englishlearn.przypomnienia.PrzypomnienieMenu;



public class MenuAplikacji extends AppCompatActivity {


    Button wyjsciePrzycisk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu);


        wyjsciePrzycisk = findViewById(R.id.wyjscie);

        wyjsciePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View widok) {

                Intent intencjaMenu = new Intent(Intent.ACTION_MAIN);

                intencjaMenu.addCategory( Intent.CATEGORY_HOME );

                intencjaMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intencjaMenu);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void odtwarzacz (View widok){
        Intent intencja = new Intent(this, Odtwarzacz.class);
        startActivity(intencja);

    }

    public void przypomnienia (View widok){
        Intent intencja = new Intent(this, PrzypomnienieMenu.class);
        startActivity(intencja);
    }

    public void nagrywacz (View widok){
        Intent intencja = new Intent(this, Nagrywarka.class);
        startActivity(intencja);
    }

}