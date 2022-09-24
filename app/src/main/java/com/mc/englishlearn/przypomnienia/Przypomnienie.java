package com.mc.englishlearn.przypomnienia;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Przypomnienie extends AppCompatActivity {

    AlertDialog.Builder konstruktor;
    List<Model> listaDanych = new ArrayList<>();
    BazaDanych bazadanych;
    Button dataPrzycisk, czasPrzycisk, przeslijPrzycisk;
    EditText edycjaPrzypomnieniaTekst;
    String czasNaPowiadomienie;

    @Override
    protected void onCreate(Bundle zapisStanuInstancji) {
        super.onCreate(zapisStanuInstancji);
        setContentView(R.layout.przypomnienie_dodaj);
        dataPrzycisk = (Button) findViewById(R.id.dataPrzycisk);
        czasPrzycisk = (Button) findViewById(R.id.czasPrzycisk);
        przeslijPrzycisk = (Button) findViewById(R.id.dodajPrzycisk);
        bazadanych = BazaDanych.pobierzInstancjeBazyDanych(this);
        listaDanych = bazadanych.bazaDanychInterface().odczytWszystkichDanych();
        edycjaPrzypomnieniaTekst = (EditText) findViewById(R.id.wpiszTresc);

        przeslijPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tresc = edycjaPrzypomnieniaTekst.getText().toString().trim();
                String data = dataPrzycisk.getText().toString().trim();
                String czas = czasPrzycisk.getText().toString().trim();

                if (!tresc.equals("")) {
                    Model model = new Model();

                    model.setTresc(tresc);
                    model.setCzas(czas);
                    model.setData(data);

                    bazadanych.bazaDanychInterface().wprowadzDane(model);
                    edycjaPrzypomnieniaTekst.setText("");
                    czasPrzycisk.setText("CZAS");
                    dataPrzycisk.setText("DATA");

                    listaDanych.clear();
                    Toast.makeText(Przypomnienie.this, "Sukces!", Toast.LENGTH_LONG).show();

                } else {
                    konstruktor = new AlertDialog.Builder(Przypomnienie.this);
                    konstruktor.setMessage("Pole tekstowe nie może być puste!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alarm = konstruktor.create();
                    alarm.setTitle("Brak tekstu");
                    alarm.show();
                }
            }
        });

        czasPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wybierzCzas();
            }
        });

        dataPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wybierzDate();
            }
        });

    }

    public void wybierzCzas() {
        Calendar kalendarz = Calendar.getInstance();
        int godzina = kalendarz.get(Calendar.HOUR_OF_DAY);
        final int minuta = kalendarz.get(Calendar.MINUTE);
        TimePickerDialog dialogWyboruCzasu = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker wyborCzasu, int minutnik, int sekundnik) {
                czasNaPowiadomienie = minutnik + ":" +sekundnik;
                czasPrzycisk.setText(formatowanieCzasu(minutnik,sekundnik));
            }
        },godzina,minuta,true);
        dialogWyboruCzasu.show();
    }

    public String formatowanieCzasu(int godzina, int minuta) {
        String czas;
        czas = "";
        String formatowanaMinuta;
        if (minuta / 10 == 0) {
            formatowanaMinuta = "0" + minuta;
        } else {
            formatowanaMinuta = "" + minuta;
        }
        czas = godzina + ":" + formatowanaMinuta;
        return czas;
    }

    public void wybierzDate() {
    GregorianCalendar kalendarz = (GregorianCalendar) GregorianCalendar.getInstance();
        int dzien = kalendarz.get(Calendar.DAY_OF_MONTH);
        int rok = kalendarz.get(Calendar.YEAR);
        int miesiac = kalendarz.get(Calendar.MONTH);

        DatePickerDialog oknoWyboruDaty = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker wyborDanych, int rok, int miesiac, int dzien) {
                dataPrzycisk.setText(dzien + "-" +(miesiac + 1) + "-" + rok);
            }
        },rok,miesiac,dzien);
        oknoWyboruDaty.show();
    }
}