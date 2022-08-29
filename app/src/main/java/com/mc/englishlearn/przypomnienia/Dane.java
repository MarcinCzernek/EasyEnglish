package com.mc.englishlearn.przypomnienia;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "fiszki")
public class Dane implements Serializable {

    //tworzę id kolumny
    @PrimaryKey(autoGenerate = true)
    private int id;

    //tworzę kolumny tresc fiszki
    @ColumnInfo(name = "tresc")
    private String tresc;

    //tworzę kolumny data fiszki
    @ColumnInfo(name = "data")
    private String data;

    //tworzę kolumny czas fiszki
    @ColumnInfo(name = "czas")
    private String czas;

    //gettery i settery pól

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCzas() {
        return czas;
    }

    public void setCzas(String czas) {
        this.czas = czas;
    }
}
