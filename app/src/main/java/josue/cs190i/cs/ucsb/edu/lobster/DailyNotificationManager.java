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

        // calendar might no longer be necessary, unless you want to try having the notification
        // come at a certain time.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 14);

        Intent intent = new Intent(context, DailyNotificationReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(context, DailyNotificationReceiver.REMINDER_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // can set the interval to (one minute * 5) for demo purposes
        //long one_minute = 60000;

        // default start time: SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY (one day from right now)
        // note: can set start time to calendar.getTimeInMillis() if experimenting with a certain time
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, pendingIntent);

        // needed to make notifications stop coming (e.g. if interval is too short, you'll get spammed)
        //alarmManager.cancel(pendingIntent);
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
