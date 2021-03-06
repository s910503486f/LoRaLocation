package com.sillywei.notificationdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private final int notifyId = 1;
    private final int mId2 = 2;

    private int numMessages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void simpleNotificationButtonOnClick(View view) {
        createSimpleNotification();
    }

    public void expandedNotificationButtonOnClick(View view) {
        createExpandedNotification();
    }

    public void updateNotificationButtonOnClick(View view) {
        updateNotification();
    }

    /**
         *  This function create an simple notification.
         */
    private void createSimpleNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    /**
         *  This function create an expanded notification.
         */
    private void createExpandedNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Event tracker")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        // Moves events into the expanded layout
        for (int i=0; i < events.length; i++) {
            events[i] = "" + i;
            inboxStyle.addLine(events[i]);
        }
        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Issue the notification here.
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId2, mBuilder.build());
    }
    /**
         *  This function update notification.
         */
    private void updateNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an ID for the notification, so it can be updated
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_launcher);

        // Start of a loop that processes data and then notifies the user
        String currentText = "Updated";
        mNotifyBuilder.setContentText(currentText)
                .setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                notifyId,
                mNotifyBuilder.build());
    }
}