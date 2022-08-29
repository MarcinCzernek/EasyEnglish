package com.mc.englishlearn.przypomnienia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mc.englishlearn.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AdapterActivity extends RecyclerView.Adapter<AdapterActivity.ViewHolder> {

    //Zmienne
    private List<Dane> daneLista;
    public Activity kontekst;
    private RoomDB bazaDanych;

    AlertDialog.Builder builder;

    public AdapterActivity(List<Dane> daneLista, Activity kontekst) {
        this.daneLista = daneLista;
        this.kontekst = kontekst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inicjalizuję widok
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_przypomnien,parent,false);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //inicjalizuję dane fiszki
        Dane dane = daneLista.get(position);
        //inicjalizuję baze danych
        bazaDanych = RoomDB.getInstance(kontekst);
        //ustawiam tresc, date i czas w widoku tekstowym
        holder.tresc.setText(dane.getTresc());
        holder.data.setText(dane.getData());
        holder.czas.setText(dane.getCzas());


        holder.edycjaFiszki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inicjalizuję główne dane
                Dane dane = daneLista.get(holder.getAdapterPosition());
                //pobieram id fiszki
                final int sID = dane.getId();
                //pobieram tresc
                String sText = dane.getTresc();
                //pobieram date
                String sData = dane.getData();
                //pobieram czas
                String sCzas = dane.getCzas();

                //tworzę okno dialogowe
                final Dialog oknoDialogowe = new Dialog(kontekst);
                // ustawiam widok treści
                oknoDialogowe.setContentView(R.layout.okno_dialogowe_edycji);

                //inicjalizuję szerokość okna dialgowego
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                //inicjalizuję wysokośc okna dialgowego
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                //ustawiam layout
                oknoDialogowe.getWindow().setLayout(width, height);
                //wyświetlanie okna dialogowego
                oknoDialogowe.show();

                //inicjalizuję i przypisuję zmienną
                final EditText edytujTresc = oknoDialogowe.findViewById(R.id.edytujTresc);
                final EditText edytujDate = oknoDialogowe.findViewById(R.id.edytujDate);
                final EditText edytujCzas = oknoDialogowe.findViewById(R.id.edytujCzas);
                Button aktualizujDanePrzycisk = oknoDialogowe.findViewById(R.id.aktualizujDanePrzycisk);

                //ustawiam tekst w edycji tekstu
                edytujTresc.setText(sText);
                edytujCzas.setText(sCzas);
                edytujDate.setText(sData);

                aktualizujDanePrzycisk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // zamknij okno dialogowe
                        oknoDialogowe.dismiss();

                        //pobierz tekst aktualizacji z tekstu edycji
                        String zmienionaData = edytujDate.getText().toString().trim();
                        String zmienionyCzas = edytujCzas.getText().toString().trim();
                        String zmienionaTresc = edytujTresc.getText().toString().trim();
                        //zaktualizuj tekst w bazie danych

                        bazaDanych.dao().aktualizuj(sID, zmienionaTresc, zmienionyCzas, zmienionaData);
                        //powiadom gdy dane są zaktualizowane

                        daneLista.clear();
                        daneLista.addAll(bazaDanych.dao().czytajWszystkie());
                        notifyDataSetChanged();
                    }
                });


            }
        });

        holder.usunFiszke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder= new AlertDialog.Builder(v.getContext());
                //Ustawianie wiadomości i wykonywanie akcji po kliknięciu przycisku
                builder.setMessage("Czy jesteś pewny aby usunąć tą kartkę z przypomnieniem?")
                        .setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Dane d=daneLista.get(holder.getAdapterPosition());
                                //usuń treść z bazy danych

                                bazaDanych.dao().usun(d);
                                //powiadom, gdy dane zostaną wybrane
                                int pozycja = holder.getAdapterPosition();
                                daneLista.remove(pozycja);
                                notifyItemRemoved(pozycja);
                                notifyItemRangeChanged(pozycja,daneLista.size());

                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Akcja dla wyboru przycisku 'Nie'
                                dialog.cancel();
                            }
                        });
                //Twozenie okna dialogowego
                AlertDialog alarm = builder.create();
                //Ręczne ustawianie tytułu
                alarm.setTitle("Potwierdź usunięcie");
                alarm.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return daneLista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //inicjalizacja zmiennych
        TextView tresc, data, czas;
        ImageView edycjaFiszki,usunFiszke;

        public ViewHolder(@NonNull  View elementWidoku) {
            super(elementWidoku);

            //Przypisanie zmiennych
            tresc = (TextView) elementWidoku.findViewById(R.id.tytul);
            data = (TextView) elementWidoku.findViewById(R.id.data);
            czas = (TextView) elementWidoku.findViewById(R.id.czas);
            edycjaFiszki = elementWidoku.findViewById(R.id.edycjaFiszki);
            usunFiszke = elementWidoku.findViewById(R.id.usunFiszke);
        }
    }




}
