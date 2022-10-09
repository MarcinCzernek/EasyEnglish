package com.mc.englishlearn.przypomnienia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mc.englishlearn.R;

import java.util.ArrayList;
import java.util.List;


public class PrzypomnienieMenu extends AppCompatActivity {

  RecyclerView listaPrzypomnien;


    List<Model> listaDanych = new ArrayList<>();
    Button dodajPrzypomnieniePrzycisk, resetPrzycisk;
    LinearLayoutManager zarzadzanieListaFiszek;
    Adapter mainAdapter;
    AlertDialog.Builder konstruktor;
    BazaDanych bazadanych;

        @Override
        protected void onCreate (Bundle zapisStanuInstancji){
           super.onCreate(zapisStanuInstancji);
           setContentView(R.layout.przypomnienie_menu);
            listaPrzypomnien = findViewById(R.id.lista);
            resetPrzycisk = (Button) findViewById(R.id.resetPrzycisk);
            dodajPrzypomnieniePrzycisk = (Button) findViewById(R.id.dodajPrzypomnienie);

            bazadanych = BazaDanych.pobierzInstancjeBazyDanych(this);
            listaDanych = bazadanych.bazaDanychInterface().odczytWszystkichDanych();
            zarzadzanieListaFiszek = new LinearLayoutManager(this);
            listaPrzypomnien.setLayoutManager(zarzadzanieListaFiszek);
            mainAdapter = new Adapter(listaDanych, PrzypomnienieMenu.this);
            listaPrzypomnien.setAdapter(mainAdapter);

            dodajPrzypomnieniePrzycisk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intencja = new Intent(getApplicationContext(), Przypomnienie.class);
                    startActivity(intencja);

                }
            });

    resetPrzycisk.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            konstruktor = new AlertDialog.Builder(v.getContext());
            konstruktor.setMessage("Usunąć wszystkie fiszki?")
                    .setCancelable(false)
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bazadanych.bazaDanychInterface().usunWszystko(listaDanych);
                            listaDanych.clear();
                            listaDanych.addAll(bazadanych.bazaDanychInterface().odczytWszystkichDanych());
                            mainAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alarmDialog = konstruktor.create();
            alarmDialog.setTitle("Usunięcia wszystkich przypomnień");
            alarmDialog.show();

        }
    });
        }
    }

