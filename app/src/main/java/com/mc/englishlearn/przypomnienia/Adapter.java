package com.mc.englishlearn.przypomnienia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mc.englishlearn.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.myviewholder> {

    ArrayList<Model> zbiórDanych = new ArrayList<Model>(); //array lista do trzymania przypomnień

    public Adapter(ArrayList<Model> zbiórDanych) {
        this.zbiórDanych = zbiórDanych;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pojedynczy_plik_przypomnienia, parent, false); //wypełnia plik xml w recyclerview
        return new myviewholder(view);
    }

    public void onBindViewHolder(@NonNull myviewholder holder, int position){
        holder.tytul.setText(zbiórDanych.get(position).getTytul());               //Powiązanie pojedynczych obiektów przypomnienia z widokiem recyklera
        holder.data.setText(zbiórDanych.get(position).getData());
        holder.czas.setText(zbiórDanych.get(position).getCzas());
    }

    @Override
    public int getItemCount(){
        return zbiórDanych.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        TextView tytul, data, czas;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            tytul = (TextView) itemView.findViewById(R.id.tytul);   //przechowuje referencję materiałów do wyświetlania danych w recyclerview                            //holds the reference of the materials to show data in recyclerview
            data = (TextView) itemView.findViewById(R.id.data);
            czas = (TextView) itemView.findViewById(R.id.czas);
        }
    }
}
