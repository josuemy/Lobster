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

public class DailyNotificationManager {
    Context context;
    PendingIntent pendingIntent;

    DailyNotificationManager(Context context) {
        this.context = context;
    }

    public void beginDailyNotifications() {

        Intent intent = new Intent(context, DailyNotificationReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(context, DailyNotificationReceiver.REMINDER_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // can set the interval to (one minute * 5) for demo purposes
        long one_minute = 60000;

        // default start time: SystemClock.elapsedRealtime() (right now)
        // default interval: one day
        // note: can set start time to calendar.getTimeInMillis() if experimenting with a certain time
        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), one_minute * 3, pendingIntent);

    }

    public void turnOffNotifications() {

        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, DailyNotificationReceiver.REMINDER_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); alarmManager.cancel(sender);
        alarmManager.cancel(sender);

        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);

    }
}
