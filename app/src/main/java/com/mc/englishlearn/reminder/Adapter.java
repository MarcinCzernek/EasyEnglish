package com.mc.englishlearn.reminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.mc.englishlearn.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.myviewholder> {

    ArrayList<Model> dataholder = new ArrayList<Model>(); //array lista do trzymania przypomnień

    public Adapter(ArrayList<Model> dataholder) {
        this.dataholder = dataholder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reminder_file, parent, false); //inflates the xml file in recyclerview
        return new myviewholder(view);
    }

    public void onBindViewHolder(@NonNull myviewholder holder, int position){
        holder.mTitle.setText(dataholder.get(position).getTitle());               //Binds the single reminder objects to recycler view
        holder.mDate.setText(dataholder.get(position).getDate());
        holder.mTime.setText(dataholder.get(position).getTime());
    }

    @Override
    public int getItemCount(){
        return dataholder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        TextView mTitle, mDate, mTime;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);   //holds the reference of the materials to show data in recyclerview                            //holds the reference of the materials to show data in recyclerview
            mDate = (TextView) itemView.findViewById(R.id.date);
            mTime = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
