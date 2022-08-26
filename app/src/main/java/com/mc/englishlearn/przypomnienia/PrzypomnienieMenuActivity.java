package com.mc.englishlearn.przypomnienia;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mc.englishlearn.R;


import java.util.ArrayList;

public class PrzypomnienieMenuActivity extends AppCompatActivity {

    RecyclerView listaPrzypomnien;

    Adapter adapter;

    Button dodajPrzypomnieniePrzycisk, usun;

    //Array lista do dodawania kartek z przypomnieniami i wyświetlania ich w Recycleview
    ArrayList <Kartka> zbiórDanych = new ArrayList<Kartka>();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przypomnienie_aktywnosc_menu);

        usun = (Button) findViewById(R.id.usun);

        //Przycisk u dołu do zmiany aktywności
        dodajPrzypomnieniePrzycisk = (Button) findViewById(R.id.dodajPrzypomnienie);
        listaPrzypomnien = (RecyclerView) findViewById(R.id.lista);
        listaPrzypomnien.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


         //Przycisk rozpoczyna nową aktywność, aby dodać Przypomnienie
        dodajPrzypomnieniePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Przypomnienie.class);
                startActivity(intent);
            }
        });

        usun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZarzadzanieBazaDanych zarzadzanieBazaDanych = new ZarzadzanieBazaDanych(getApplicationContext());
                zarzadzanieBazaDanych.usunWszystkiePrzypomnienia();
                adapter.notifyDataSetChanged();

            }
        });


          //kursor do ładowania danych z bazy
        Cursor kursor = new ZarzadzanieBazaDanych(getApplicationContext()).czytWszystkiePrzypomnienia();

        while(kursor.moveToNext()){
            Kartka kartka = new Kartka(kursor.getString(1),kursor.getString(2),kursor.getString(3));
            zbiórDanych.add(kartka);
        }

    //Wiąże adapter z recyclerview
        adapter = new Adapter(zbiórDanych);
        listaPrzypomnien.setAdapter(adapter);
    }


    //Zmusza użytkownika do wyjścia z aplikacji
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }


}
