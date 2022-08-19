package com.mc.englishlearn.przypomnienia;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ZarzadzanieBazaDanych extends SQLiteOpenHelper{

    private static String nazwaBazyDanych = "przypomnienia"; //nazwa tabeli do przechowywania przypomnień w sqlite

    public ZarzadzanieBazaDanych(@Nullable Context context){
        super(context,nazwaBazyDanych,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteBazaDanych) {
      String zapytanie = "create table przypomnienia(id integer primary key autoincrement,tytul text, data text, czas text)";
        sqLiteBazaDanych.execSQL(zapytanie);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteBazaDanych, int i, int i1) {
        String zapytanie = "DROP TABLE IF EXISTS przypomnienia";                                         //sql kwerenda aby sprawdzić tablicę z tą samą nazwą lub inną nazwą
        sqLiteBazaDanych.execSQL(zapytanie);                                                              //wykonuje zapytanie sql
        onCreate(sqLiteBazaDanych);
    }

    public String dodajPrzypomnienie(String tytul, String data, String czas) {
        SQLiteDatabase bazaDanych = this.getReadableDatabase();

        ContentValues trescWartosci = new ContentValues();
        trescWartosci.put("tytul", tytul);                                                          //Wprowadza dane do bazy  danych sqlite
        trescWartosci.put("data", data);
        trescWartosci.put("czas", czas);

        float wynik = bazaDanych.insert("przypomnienia", null, trescWartosci);    //zwraca -1 jeśli dane pomyslnie wprowadzi do bazy danych

        if (wynik == -1) {
            return "Nie udało się";
        } else {
            return "Sukcesywnie wprowadzono";
        }

    }

    public Cursor czytWszystkiePrzypomnienia() {
        SQLiteDatabase bazaDanych = this.getWritableDatabase();
        String zapytanie = "select * from przypomnienia order by id desc";                               //Kwerenda sql do odrzyskania danych z bazy danych
        Cursor kursor = bazaDanych.rawQuery(zapytanie, null);
        return kursor;
    }
}

