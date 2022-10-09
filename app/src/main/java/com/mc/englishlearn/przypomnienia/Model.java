package com.mc.englishlearn.przypomnienia;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "fiszki")
public class Model implements Serializable {


    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "czas")
    private String czas;
    @ColumnInfo(name = "tresc")
    private String tresc;
    @ColumnInfo(name = "data")
    private String data;


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
