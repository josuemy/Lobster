package josue.cs190i.cs.ucsb.edu.lobster;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by seanshelton on 6/10/17.
 */

public class DailyNotificationReceiver extends BroadcastReceiver {

    private final String NOTIFICATION_STRING_TAG = "Notification String";
    public static final int REMINDER_ID = 22;
    private final int BROADCASTER_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = "Lobster - Daily Reminder";
        String msg = "Don't forget to make your daily post!";
        sendNotification(title, msg, context);
    }

    private void sendNotification(String msg, String text, Context context) {

        Intent notificationIntent = new Intent(context, StartingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, REMINDER_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // getting the notification manager to send the notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(REMINDER_ID, createNotification(msg, text, pendingIntent, context));

    }

    private Notification createNotification(String title, String text, PendingIntent notificationPendingIntent, Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(Color.GRAY)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }
}
