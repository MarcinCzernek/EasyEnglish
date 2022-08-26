package com.mc.englishlearn.przypomnienia;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ZarzadzanieBazaDanych extends SQLiteOpenHelper{

    //nazwa tabeli do przechowywania przypomnień w sqlite
    private static String nazwaBazyDanych = "przypomnienia";

    public ZarzadzanieBazaDanych(@Nullable Context context){
        super(context,nazwaBazyDanych,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteBazaDanych) {
      String zapytanieTworzące = "create table przypomnienia(id integer primary key autoincrement,tytul text, data text, czas text)";
        sqLiteBazaDanych.execSQL(zapytanieTworzące);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteBazaDanych, int i, int i1) {

        //sql kwerenda aby sprawdzić tablicę z tą samą nazwą lub inną nazwą
        String zapytanieSprawdzające = "DROP TABLE IF EXISTS przypomnienia";

        //wykonuje zapytanie sql
        sqLiteBazaDanych.execSQL(zapytanieSprawdzające);
        onCreate(sqLiteBazaDanych);
    }

    public String dodajPrzypomnienie(String tytul, String data, String czas) {
        SQLiteDatabase bazaDanych = this.getReadableDatabase();

        //Wprowadza dane do bazy danych sqlite
        ContentValues trescWartosci = new ContentValues();
        trescWartosci.put("tytul", tytul);
        trescWartosci.put("data", data);
        trescWartosci.put("czas", czas);

        //zwraca -1 jeśli dane pomyślnie wprowadzi do bazy danych
        float wynik = bazaDanych.insert("przypomnienia", null, trescWartosci);

        if (wynik == -1) {
            return "Nie udało się";
        } else {
            return "Przypomnienie wprowadzone";
        }

    }

    public Cursor czytWszystkiePrzypomnienia() {
        SQLiteDatabase bazaDanych = this.getWritableDatabase();
        //Kwerenda sql do odrzyskania danych z bazy danych
        String zapytanieOdczytujace = "select * from przypomnienia order by id desc";
        Cursor kursor = bazaDanych.rawQuery(zapytanieOdczytujace, null);
        return kursor;

    }

    public void usunWszystkiePrzypomnienia(){
        String zapytanieUsuwawacze = "DELETE FROM przypomnienia";
        SQLiteDatabase bazaDanych = this.getWritableDatabase();
        //SPKG //usuwa wszystkie wiersze w tablicy przypomnienia
        bazaDanych.execSQL(zapytanieUsuwawacze);
       // bazaDanych.close();

    }



}

