package com.mc.englishlearn.przypomnienia;

//Klasa Model jest używana do ustawiania i pobierania danych z bazy danych

public class Model {

    String tytul, data, czas;

    public Model() {
    }

    public Model(String tytul, String data, String czas) {
        this.tytul = tytul;
        this.data = data;
        this.czas = czas;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
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
