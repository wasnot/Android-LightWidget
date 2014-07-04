package net.wasnot.android.lightwidget.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by aidaakihiro on 2014/06/21.
 */
public class NotificationUtil {


    public static Notification makeNotification(Context con) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(con);
        b.setContentIntent(makePendingIntent(con));
        b.setContentTitle(con.getString(R.string.app_name));
        b.setContentText("light");
        b.setPriority(Integer.MIN_VALUE);
        b.setSmallIcon(R.drawable.ic_launcher);
        return b.getNotification();
    }

    private static PendingIntent makePendingIntent(Context con) {
        Intent i = new Intent(con, MainActivity.class);
        return PendingIntent.getActivity(con, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
