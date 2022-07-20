package com.mc.englishlearn;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mc.englishlearn.audioplayer.MyService;
import com.mc.englishlearn.audioplayer.PlayerActivity;
import com.mc.englishlearn.recording.RecordLaunchActivity;
import com.mc.englishlearn.reminder.MainReminderActivity;


public class MainActivity extends AppCompatActivity {

    Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        exit = findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
    }

    //Odtwarzacz
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNewIntentPlayer (View view){
        Intent i = new Intent(this, PlayerActivity.class);
        startActivity(i);
        Context context = getApplicationContext();
        Intent intent = new Intent(this, MyService.class); // Build the intent for the service
        context.startForegroundService(intent);
    }

    //Kalendarz
    public void createNewIntentReminder (View view){
        Intent i = new Intent(this, MainReminderActivity.class);
        startActivity(i);
    }

    //Rekorder
    public void createNewIntentRecorder (View view){
        Intent i = new Intent(this, RecordLaunchActivity.class);
        startActivity(i);
    }
}