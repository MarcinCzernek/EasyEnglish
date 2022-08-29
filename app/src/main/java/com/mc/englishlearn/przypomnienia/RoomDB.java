package com.mc.englishlearn.przypomnienia;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Dane.class},version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {

    //Tworzę instancje bazy danych

    private static RoomDB bazadanych;

    private static String NAZWA_BAZYDANYCH = "engLearnDB";

    public synchronized static RoomDB getInstance(Context kontekst){
        if(bazadanych == null){
            //gdy baza danych  ma wartość null - inicjalizuj baze danych
            bazadanych = Room.databaseBuilder(kontekst.getApplicationContext(),RoomDB.class,NAZWA_BAZYDANYCH)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        //zwraca baze danych
        return bazadanych;
    }

    //utworzenie instancji DAO
    public abstract DAO dao();

}
