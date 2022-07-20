package com.mc.englishlearn.reminder;

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

public class Reminder extends AppCompatActivity {

    Button dateButton, timeButton, sendButton;
    EditText reminderEdit;
    String timeToNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        dateButton = (Button) findViewById(R.id.date);
        timeButton = (Button) findViewById(R.id.time);
        sendButton = (Button) findViewById(R.id.add);

        reminderEdit = (EditText) findViewById(R.id.editContent);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //when we click on the choose time button it calls the select time method
            selectTime();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when we click on the choose date button it calls the select date method
                selectDate();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reminder = reminderEdit.getText().toString().trim(); //dostęp do danych z pola wprowadzania
                String date = dateButton.getText().toString().trim(); //dostęp do danych z przycisku z danymi
                String time = timeButton.getText().toString().trim(); //dostęp do danych z przycisku z czasem

                if(reminder.isEmpty()){
                    Toast.makeText(Reminder.this, "Proszę wprowadź tekst", Toast.LENGTH_SHORT).show(); //shows the toast if input field is empty
                }else{
                    if(time.equals("time") || date.equals("date")){
                        Toast.makeText(Reminder.this, "Proszę wybierz datę i czas", Toast.LENGTH_SHORT).show(); //shows toast if date and time are not selected
                    }else{
                        insertData(reminder, date, time);
                    }
                }
            }
        });
    }

    private void insertData(String reminder, String date, String time) {
        String result = new DatabaseManager(this).addreminder(reminder,date,time); //inserts the title,date,time into sql lite database
        setAlarm(reminder,date,time); //wzywa metodę aby wezwała alarm
        reminderEdit.setText("");
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

    }

    private void selectTime() {
    //this method performs the time picker task
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeToNotify = i + ":" +i1; //temp variable to store the time to set alarm
                timeButton.setText(FormatTime(i,i1)); //sets the button text as selected time
            }
        },hour,minute,true);
        timePickerDialog.show();
    }

    private String  FormatTime(int hour, int minute) {
        //this method converts the time into 12hr format and assigns am or pm

       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
       // String date = sdf.format(new Date(now));
// date jest teraz "2019-01-09 10:22:47"

        String time;
        time = "";
        String formattedMinute;
        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }
        time = hour + ":" + formattedMinute;

        return time;
    }

    /*
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
String date = sdf.format(new Date(now))
// date jest teraz "2019-01-09 10:22:47"
     */

    private void selectDate() {

    GregorianCalendar calendar= (GregorianCalendar) GregorianCalendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateButton.setText(day + "-" +(month + 1) + "-" + year); //sets the selected date as test for button
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm
        Intent intent = new Intent(getApplicationContext(), Alarm.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateandtime = date + " " + timeToNotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intentBack = new Intent(getApplicationContext(), MainReminderActivity.class);                //this intent will be called once the setting alarm is complete
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity to mainactivity
    }
}