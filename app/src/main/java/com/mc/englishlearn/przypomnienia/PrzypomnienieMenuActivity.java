package com.mc.englishlearn.przypomnienia;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mc.englishlearn.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PrzypomnienieMenuActivity extends AppCompatActivity {

  RecyclerView listaPrzypomnien;


    Button dodajPrzypomnieniePrzycisk, resetPrzycisk;

    //Array lista do dodawania kartek z przypomnieniami i wyświetlania ich w Recycleview
    List<Dane> listaDanych = new ArrayList<>();

    LinearLayoutManager linearLayoutManager;

    RoomDB bazadanych;

    AdapterActivity mainAdapter;

    AlertDialog.Builder builder;

        @Override
        protected void onCreate (Bundle savedInstanceState){
           super.onCreate(savedInstanceState);
           setContentView(R.layout.przypomnienie_aktywnosc_menu);
           listaPrzypomnien = findViewById(R.id.lista);

           //inicjuję baze danych
            resetPrzycisk = (Button) findViewById(R.id.resetPrzycisk);

            dodajPrzypomnieniePrzycisk = (Button) findViewById(R.id.dodajPrzypomnienie);

            bazadanych = RoomDB.getInstance(this);

            //przechowuję wartosc bazy danych wliście danych
            listaDanych = bazadanych.dao().czytajWszystkie();

            //inicjuję menedżera linear layout
            linearLayoutManager = new LinearLayoutManager(this);

            //ustawiam layout manager
            listaPrzypomnien.setLayoutManager(linearLayoutManager);

            //inicjalizuję adapter
            mainAdapter = new AdapterActivity(listaDanych, PrzypomnienieMenuActivity.this);

            //ustawiam adapter
            listaPrzypomnien.setAdapter(mainAdapter);

            //Przycisk rozpoczyna nową aktywność, aby dodać Przypomnienie
            dodajPrzypomnieniePrzycisk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Przypomnienie.class);
                    startActivity(intent);

                }
            });

    resetPrzycisk.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            builder = new AlertDialog.Builder(v.getContext());
            //Ręczne ustawianie wiadomości i wykonywanie akcji po kliknięciu przycisku
            builder.setMessage("Czy na pewno chcesz usunąć wszystkie kartki z przypomnieniami?")
                    .setCancelable(false)
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //delete all data from database
                            bazadanych.dao().resetowanie(listaDanych);
                            //notify when all data deteled
                            listaDanych.clear();
                            listaDanych.addAll(bazadanych.dao().czytajWszystkie());
                            mainAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Akcja po wyborze 'nie'
                            dialog.cancel();
                        }
                    });
            //Tworzenie okna dialogowego
            AlertDialog alert = builder.create();
            //Ręczne ustawianie tytułu
            alert.setTitle("Potwierdzenie usunięcia wszystkich przypomnień");
            alert.show();

        }
    });



        }

    }

