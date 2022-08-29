package com.mc.englishlearn.przypomnienia;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Przypomnienie extends AppCompatActivity {

    Button dataPrzycisk, czasPrzycisk, przeslijPrzycisk;
    EditText edycjaPrzypomnieniaTekst;
    String czasNaPowiadomienie;

    List<Dane> listaDanych=new ArrayList<>();

    RoomDB bazadanych;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przypomnienie_aktywnosc);

        dataPrzycisk = (Button) findViewById(R.id.dataPrzycisk);
        czasPrzycisk = (Button) findViewById(R.id.czasPrzycisk);
        przeslijPrzycisk = (Button) findViewById(R.id.dodajPrzycisk);

        //inicjalizuj baze danych
        bazadanych = RoomDB.getInstance(this);

        //przechowuj wartości z bazy danych w datalist
        listaDanych = bazadanych.dao().czytajWszystkie();

        edycjaPrzypomnieniaTekst = (EditText) findViewById(R.id.wpiszTresc);

        przeslijPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pobieram string z edytujTresc
                String tresc = edycjaPrzypomnieniaTekst.getText().toString().trim(); //dostęp do danych z pola wprowadzania
                String data = dataPrzycisk.getText().toString().trim(); //dostęp do danych z przycisku z danymi
                String czas = czasPrzycisk.getText().toString().trim(); //dostęp do danych z przycisku z czasem

                if (!tresc.equals("")) {
                    //gdy tekst nie ma wartości null
                    //inicjalizuję główne dane
                    Dane dane = new Dane();

                    //ustawiam tekst na głównych danych
                    dane.setTresc(tresc);
                    dane.setCzas(czas);
                    dane.setData(data);

                    //wprowadz  tekst do bazy danych
                    bazadanych.dao().wprowadz(dane);

                    //wyczyść pole tekstowe
                    edycjaPrzypomnieniaTekst.setText("");
                    czasPrzycisk.setText("CZAS");
                    dataPrzycisk.setText("DATA");

                    //informuję gdy dane są wprowadzone
                    listaDanych.clear();
                    Toast.makeText(Przypomnienie.this, "Sukces!", Toast.LENGTH_LONG).show();

                } else {
                    builder = new AlertDialog.Builder(Przypomnienie.this);
                    //Ręczne ustawianie wiadomości i wykonywanie akcji po kliknięciu przycisku
                    builder.setMessage("Pole tekstowe nie może być puste!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //Tworzenie okienka dialogowego
                    AlertDialog alert = builder.create();
                    //Ręczne ustawianie tytułu
                    alert.setTitle("Brak tekstu");
                    alert.show();
                }
            }
        });

        czasPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gdy klikniemy na przycisk wybierz czas, wywoła on metodę wybierzCzas
                wybierzCzas();
            }
        });

        dataPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gdy klikniemy na przycisk data przycisk wywoła on metode wybierzDate
                wybierzDate();
            }
        });

    }

    public void wybierzCzas() {
        //ta metoda wykonuje zadanie wyboru czasu z menu
        Calendar kalendarz = Calendar.getInstance();
        int godzina = kalendarz.get(Calendar.HOUR_OF_DAY);
        int minuta = kalendarz.get(Calendar.MINUTE);
        TimePickerDialog dialogWyboruCzasu = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                //zmienna temp. do przechowywania czasu do ustawienia alarmu
                czasNaPowiadomienie = i + ":" +i1;
                //ustawia tekst przycisku jako wybrany czas
                czasPrzycisk.setText(formatowanieCzasu(i,i1));
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
    int rok = kalendarz.get(Calendar.YEAR);
    int miesiac = kalendarz.get(Calendar.MONTH);
    int dzien = kalendarz.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int rok, int miesiac, int dzien) {
                //ustawia wybraną datę jako test dla przycisku
                dataPrzycisk.setText(dzien + "-" +(miesiac + 1) + "-" + rok);
            }
        },rok,miesiac,dzien);
        datePickerDialog.show();
    }

}