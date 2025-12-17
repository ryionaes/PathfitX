package com.example.pathfitx;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_WATER_REMINDER = "com.example.pathfitx.ACTION_WATER_REMINDER";
    public static final String ACTION_WORKOUT_ALERT = "com.example.pathfitx.ACTION_WORKOUT_ALERT";
    private static final String CHANNEL_ID = "PathFitX_Notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        createNotificationChannel(context);

        String title = context.getString(R.string.app_name); // Default fallback
        String message = "";
        int notificationId = 1;

        if (ACTION_WATER_REMINDER.equals(intent.getAction())) {
            title = context.getString(R.string.notif_water_title);
            message = context.getString(R.string.notif_water_message);
            notificationId = 101;
        } else if (ACTION_WORKOUT_ALERT.equals(intent.getAction())) {
            title = context.getString(R.string.notif_workout_title);
            message = context.getString(R.string.notif_workout_message);
            notificationId = 102;
        } else {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent appIntent = new Intent(context, SplashActivity.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Replace with app icon if available
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PathFitX Reminders";
            String description = "Channel for Water and Workout reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
