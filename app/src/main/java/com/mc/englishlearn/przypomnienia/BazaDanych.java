package com.mc.englishlearn.przypomnienia;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Model.class},version = 1, exportSchema = false)
public abstract class BazaDanych extends RoomDatabase {

    private static String BAZADANYCH = "engLearnBazDanych";

    private static BazaDanych bazadanych;

    public synchronized static BazaDanych pobierzInstancjeBazyDanych(Context kontekst){
        if(bazadanych == null){
            bazadanych = Room.databaseBuilder(kontekst.getApplicationContext(), BazaDanych.class,BAZADANYCH)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return bazadanych;
    }

    public abstract InterfejsBazyDanych bazaDanychInterface();

}
