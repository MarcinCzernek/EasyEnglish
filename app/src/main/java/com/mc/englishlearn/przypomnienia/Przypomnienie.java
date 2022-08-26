package com.mc.englishlearn.przypomnienia;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Przypomnienie extends AppCompatActivity {

    Button dataPrzycisk, czasPrzycisk, przeslijPrzycisk;
    EditText edycjaPrzypomnieniaTekst;
    String czasNaPowiadomienie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.przypomnienie_aktywnosc);

        dataPrzycisk = (Button) findViewById(R.id.dataPrzycisk);
        czasPrzycisk = (Button) findViewById(R.id.czasPrzycisk);
        przeslijPrzycisk = (Button) findViewById(R.id.dodajPrzycisk);

        edycjaPrzypomnieniaTekst = (EditText) findViewById(R.id.edytujZawartosc);

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

        przeslijPrzycisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String przypomnienie = edycjaPrzypomnieniaTekst.getText().toString().trim(); //dostęp do danych z pola wprowadzania
                String data = dataPrzycisk.getText().toString().trim(); //dostęp do danych z przycisku z danymi
                String czas = czasPrzycisk.getText().toString().trim(); //dostęp do danych z przycisku z czasem

                if(przypomnienie.isEmpty()){
                    Toast.makeText(Przypomnienie.this, "Proszę wprowadź tekst", Toast.LENGTH_SHORT).show(); //pokazuje toast, jeśli pole wejściowe jest puste
                }else{
                    if(czas.equals("czas") || data.equals("data")){
                        Toast.makeText(Przypomnienie.this, "Proszę wybierz datę i czas", Toast.LENGTH_SHORT).show(); //pokazuje toast, jeśli data i czas nie są wybrane
                    }else{
                        wprowadzDate(przypomnienie, data, czas);
                    }
                }
            }
        });
    }

    private void wprowadzDate(String przypomnienie, String data, String czas) {
        String wynik = new ZarzadzanieBazaDanych(this).dodajPrzypomnienie(przypomnienie,data,czas); //wstawia tytuł, datę, czas do bazy danych sql lite
        ustawAlarm(przypomnienie,data,czas); //wzywa metodę aby uaktywniła alarm
        edycjaPrzypomnieniaTekst.setText("");
        Toast.makeText(this, wynik, Toast.LENGTH_SHORT).show();

    }

    private void wybierzCzas() {
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

    private String formatowanieCzasu(int godzina, int minuta) {
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


    private void wybierzDate() {
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

    private void ustawAlarm(String tekst, String data, String czas) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //przypisanie obiektu menedżera alarmów do ustawiania alarmu
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("wydarzenie", tekst);                                                       //wysłanie danych do klasy alarmowej w celu utworzenia kanału i powiadomienia
        intent.putExtra("czas", data);
        intent.putExtra("data", czas);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dataOrazCzas = data + " " + czasNaPowiadomienie;
        DateFormat formater = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date data1 = formater.parse(dataOrazCzas);
            am.set(AlarmManager.RTC_WAKEUP, data1.getTime(), pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intentBack = new Intent(getApplicationContext(), PrzypomnienieMenuActivity.class);                //intencja ta zostanie wywołana po zakończeniu ustawiania alarmu
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //przechodzi od dodania aktywności przypominającej do aktywności głównej
    }
}