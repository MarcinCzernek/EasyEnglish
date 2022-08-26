package com.mc.englishlearn.przypomnienia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.myviewholder> {

    ArrayList <Kartka> zbiórDanych = new ArrayList<Kartka>(); //array lista do trzymania przypomnień
    private Context context;
    ZarzadzanieBazaDanych zarzadzanieBazaDanych;


    public Adapter(ArrayList<Kartka> zbiórDanych) {
        this.zbiórDanych = zbiórDanych;

    }

    //wypełnia plik xml w recyclerview
    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pojedynczy_plik_przypomnienia, parent, false);
        return new myviewholder(view);
    }

    //Powiązanie pojedynczych obiektów przypomnienia z widokiem recyklera
    public void onBindViewHolder(@NonNull myviewholder holder, int position){
        holder.tytul.setText(zbiórDanych.get(position).getTytul());
        holder.data.setText(zbiórDanych.get(position).getData());
        holder.czas.setText(zbiórDanych.get(position).getCzas());
    }

    @Override
    public int getItemCount(){
        return zbiórDanych.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView tytul, data, czas;
        ImageView btEdit,btDelete;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
           //przechowuje referencję materiałów do wyświetlania danych w recyclerview
            tytul = (TextView) itemView.findViewById(R.id.tytul);
            data = (TextView) itemView.findViewById(R.id.data);
            czas = (TextView) itemView.findViewById(R.id.czas);

        }
    }
}
