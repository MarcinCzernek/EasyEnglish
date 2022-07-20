package com.mc.englishlearn.reminder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mc.englishlearn.R;


import java.util.ArrayList;

public class MainReminderActivity extends AppCompatActivity {

    FloatingActionButton addReminder;
    Button deleteReminder;
    RecyclerView listReminder;
    ArrayList <Model> dataHold = new ArrayList<Model>();//Array list do dodawania przypomnień i wyświetlania w recycleview
    Adapter adapter;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_main);

        addReminder = (FloatingActionButton) findViewById(R.id.addReminder); //Floating action button to change activity
        listReminder = (RecyclerView) findViewById(R.id.list);
        listReminder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        deleteReminder = (Button) findViewById(R.id.deleteReminder);

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Reminder.class);
                startActivity(intent);                                               //Starts the new activity to add Reminders
            }
        });




        Cursor cursor = new DatabaseManager(getApplicationContext()).readallreminders(); //Cursor To Load data From the database
        while(cursor.moveToNext()){
            Model model = new Model(cursor.getString(1),cursor.getString(2),cursor.getString(3));
            dataHold.add(model);
        }

        adapter = new Adapter(dataHold);
        listReminder.setAdapter(adapter);                   //Binds the adapter with recyclerview
    }

    public void onBackPressed(){
        finish();                                       //Makes the user to exit from the app
        super.onBackPressed();
    }

}
