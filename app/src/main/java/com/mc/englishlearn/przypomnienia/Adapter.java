package com.mc.englishlearn.przypomnienia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mc.englishlearn.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public Activity kontekst;
    private BazaDanych bazaDanych;
    private List<Model> daneLista;
    AlertDialog.Builder konstruktorAlertu;

    public Adapter(List<Model> daneLista, Activity kontekst) {
        this.daneLista = daneLista;
        this.kontekst = kontekst;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tablica_fiszek,parent,false);
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView edycjaFiszki,usunFiszke;
        TextView tresc, data, czas;

        public ViewHolder(@NonNull  View elementWidoku) {
            super(elementWidoku);
            edycjaFiszki = elementWidoku.findViewById(R.id.edycjaFiszki);
            usunFiszke = elementWidoku.findViewById(R.id.usunFiszke);
            tresc = (TextView) elementWidoku.findViewById(R.id.tytul);
            data = (TextView) elementWidoku.findViewById(R.id.data);
            czas = (TextView) elementWidoku.findViewById(R.id.czas);
        }
    }

    @Override
    public int getItemCount() {
        return daneLista.size();
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int pozycja) {

        Model model = daneLista.get(pozycja);
        bazaDanych = BazaDanych.pobierzInstancjeBazyDanych(kontekst);
        holder.tresc.setText(model.getTresc());
        holder.data.setText(model.getData());
        holder.czas.setText(model.getCzas());


        holder.edycjaFiszki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Model dane = daneLista.get(holder.getAdapterPosition());
                final int idPobrane = dane.getId();
                String trescPobrane = dane.getTresc();
                String danePobrane = dane.getData();
                String czasPobrane = dane.getCzas();

                final Dialog oknoDialogowe = new Dialog(kontekst);
                oknoDialogowe.setContentView(R.layout.okno_dialogowe_edycji);
                int szerokoscOkna = WindowManager.LayoutParams.MATCH_PARENT;
                int wysokoscOkna = WindowManager.LayoutParams.WRAP_CONTENT;
                oknoDialogowe.getWindow().setLayout(szerokoscOkna, wysokoscOkna);
                oknoDialogowe.show();

                final EditText edytujTresc = oknoDialogowe.findViewById(R.id.edytujTresc);
                final EditText edytujDate = oknoDialogowe.findViewById(R.id.edytujDate);
                final EditText edytujCzas = oknoDialogowe.findViewById(R.id.edytujCzas);
                Button aktualizujDanePrzycisk = oknoDialogowe.findViewById(R.id.aktualizujDanePrzycisk);

                edytujTresc.setText(trescPobrane);
                edytujCzas.setText(czasPobrane);
                edytujDate.setText(danePobrane);

                aktualizujDanePrzycisk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        oknoDialogowe.dismiss();

                        String zmienionaData = edytujDate.getText().toString().trim();
                        String zmienionyCzas = edytujCzas.getText().toString().trim();
                        String zmienionaTresc = edytujTresc.getText().toString().trim();

                        bazaDanych.bazaDanychInterface().aktualizuj(idPobrane, zmienionaTresc, zmienionyCzas, zmienionaData);

                        daneLista.clear();
                        daneLista.addAll(bazaDanych.bazaDanychInterface().odczytWszystkichDanych());
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.usunFiszke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konstruktorAlertu= new AlertDialog.Builder(v.getContext());
                konstruktorAlertu.setMessage("Czy jesteś pewny aby usunąć fiszkę z przypomnieniem?")
                        .setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Model fiszka = daneLista.get(holder.getAdapterPosition());
                                bazaDanych.bazaDanychInterface().usun(fiszka);
                                int pozycja = holder.getAdapterPosition();
                                daneLista.remove(pozycja);
                                notifyItemRemoved(pozycja);
                                notifyItemRangeChanged(pozycja,daneLista.size());
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alarm = konstruktorAlertu.create();
                alarm.setTitle("Potwierdź usunięcie");
                alarm.show();
            }
        });
    }

}
