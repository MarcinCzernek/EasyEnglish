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

    Button dodajPrzypomnieniePrzycisk;
    //Button addReminder;
    //Button deleteReminder;
    RecyclerView listaPrzypomnień;
    ArrayList <Model> zbiórDanych = new ArrayList<Model>();//Array list do dodawania przypomnień i wyświetlania w recycleview
    Adapter adapter;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przypomnienie_aktywnosc_menu);

        dodajPrzypomnieniePrzycisk = (Button) findViewById(R.id.dodajPrzypomnienie); //Przycisk u dołu do zmiany aktywności
        listaPrzypomnień = (RecyclerView) findViewById(R.id.lista);
        listaPrzypomnień.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //deleteReminder = (Button) findViewById(R.id.deleteReminder);

        dodajPrzypomnieniePrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Przypomnienie.class);
                startActivity(intent);                                               //Rozpoczyna nową aktywność, aby dodać Przypomnienia
            }
        });


        Cursor kursor = new ZarzadzanieBazaDanych(getApplicationContext()).czytWszystkiePrzypomnienia(); //kursor do ładowania danych z bazy
        while(kursor.moveToNext()){
            Model model = new Model(kursor.getString(1),kursor.getString(2),kursor.getString(3));
            zbiórDanych.add(model);
        }

        adapter = new Adapter(zbiórDanych);
        listaPrzypomnień.setAdapter(adapter);                   //Wiąże adapter z recyclerview
    }

    public void onBackPressed(){
        finish();                                       //Zmusza użytkownika do wyjścia z aplikacji
        super.onBackPressed();
    }

}
