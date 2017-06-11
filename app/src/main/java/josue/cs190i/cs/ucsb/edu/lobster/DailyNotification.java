package josue.cs190i.cs.ucsb.edu.lobster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

/**
 * Created by seanshelton on 6/10/17.
 */

public class DailyNotification {
    Context context;

    DailyNotification(Context context) {
        this.context = context;
    }

    public void beginDailyNotifications() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 14);

        Intent intent = new Intent(context, DailyNotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DailyNotificationReceiver.REMINDER_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long one_minute = 60000;
        //long one_second = 1000;

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), one_minute, pendingIntent);
    }

    public void turnOffNotifications() {

        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, DailyNotificationReceiver.REMINDER_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); alarmManager.cancel(sender);
        alarmManager.cancel(sender);

    }
}
