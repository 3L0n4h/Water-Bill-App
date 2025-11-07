package com.example.bilingsystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat;


public class Push_Notif {

    Context context;
    String title, body;

    public Push_Notif(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public void sendNotification(String title, String body, int count) {

        int notifyID = count;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Water Bill Notification";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle(title)
                .setContentText(body).setOnlyAlertOnce(true) ;

        if (notifyID <= 5) { //if value > 5, it won't show notification
            notificationManager.notify(notifyID, mBuilder.build());
        }

    }

}
