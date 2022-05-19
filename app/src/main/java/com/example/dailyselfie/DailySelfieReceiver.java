package com.example.dailyselfie;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class DailySelfieReceiver extends BroadcastReceiver {

    //Tag to log and debug
    private static final String TAG = DailySelfieReceiver.class.getSimpleName();

    private static final CharSequence textTitle   = "Time to another selfie";
    private static final CharSequence textContent = "The last time you take photos is so long ago. Please update yourself!";

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        generateNotification(context);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private void generateNotification(Context context) {
        Log.d(TAG, "on Receive");
        createNotificationChannel(context);
        Intent openApp = new Intent(context, MainActivity.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                                                                0,
                                                                openApp,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppConstants.Notification.CHANNEL_ID)
                                                                   .setSmallIcon(android.R.drawable.ic_menu_camera)
                                                                   .setContentTitle(textTitle)
                                                                   .setContentText(textContent)
                                                                   .setStyle(new NotificationCompat.BigTextStyle())
                                                                   .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                   .setContentIntent(pendingIntent)
                                                                   .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AppConstants.Notification.NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = AppConstants.Notification.CHANNEL_NAME;
            String description = AppConstants.Notification.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(AppConstants.Notification.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
