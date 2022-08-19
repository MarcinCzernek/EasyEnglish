package com.mc.englishlearn.przypomnienia;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.mc.englishlearn.R;

public class Alarm extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle pakiet = intent.getExtras();
        String tekst = pakiet.getString("wydarzenie");
        String data = pakiet.getString("data") + " " + pakiet.getString("czas");

        //Kliknięcie na powiadomienie
        Intent intent1 = new Intent(context, WiadomoscPowiadamiajaca.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("wiadomość", tekst);

        //Konstruktor powiadomień
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");

        //tutaj ustawiam wszystkie właściwości powiadomienia
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.przypomnienie_powiadomienie_uklad);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        contentView.setOnClickPendingIntent(R.id.flashPrzycisk, pendingSwitchIntent);
        contentView.setTextViewText(R.id.wiadomosc, tekst);
        contentView.setTextViewText(R.id.data, data);
        mBuilder.setSmallIcon(R.drawable.alarm);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
        mBuilder.setContent(contentView);
        mBuilder.setContentIntent(pendingIntent);

        //tworzę kanał powiadomień po poziomie API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            NotificationChannel channel = new NotificationChannel(channelId, "nazwa kanału", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        Notification notification = mBuilder.build();
        notificationManager.notify(1, notification);

    }
}
