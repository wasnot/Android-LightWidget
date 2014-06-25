package net.wasnot.android.lightwidget.app;

import net.wasnot.android.lightwidget.app.utils.LogUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class LightFloatService extends Service {
    private final static String TAG = LightFloatService.class.getSimpleName();
    private final static String ACTION_SHOW_WIDGET = "net.wasnot.android.lightwidget.ACTION_SHOW_WIDGET";
    private final static String ACTION_DISMISS_WIDGET
            = "net.wasnot.android.lightwidget.ACTION_DISMISS_WIDGET";
    int retryInterval = 5;

    private final static int NOTIFICATION_ID = 111;
    private final static long REPEAT_INTERVAL = 10000;

    private WindowWidgetManager mManager;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
        mManager = new WindowWidgetManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand " + (intent != null ? intent.getAction() : "null"));
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        if (ACTION_SHOW_WIDGET.equals(action)) {
//                startForeground(1, NotificationUtil.sendNotification(
//                        this, 1, "伝言", mNowMessage.message, false));
                mManager.showLockMessage();
                setAlarm();
        } else if (ACTION_DISMISS_WIDGET.equals(action)) {
            mManager.dissmissLockMessage();
                cancelAlarm();
                stopSelf();
                stopForeground(false);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /***/
    private void setAlarm() {
        cancelAlarm();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(SystemClock.elapsedRealtime());
        calendar.add(Calendar.SECOND, retryInterval);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
// one shot
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, calendar.getTimeInMillis(),
                getPendingIntent());
        LogUtil.d(TAG, "set Alarm at after " + retryInterval + " sec!");
    }

    protected void cancelAlarm() {
        LogUtil.d(TAG, "cancel alarm!");
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Context con = getApplicationContext();
        Intent intent = new Intent(con, LightFloatService.class);
        intent.setAction(ACTION_SHOW_WIDGET);
// 同じpendingintentなら上書きらしい。
        return PendingIntent.getService(con, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void startService(Context context) {
        Context con = context.getApplicationContext();
        Intent i = new Intent(con, LightFloatService.class);
        i.setAction(ACTION_SHOW_WIDGET);
        con.startService(i);
    }

    public static void stopService(Context context) {
        Context con = context.getApplicationContext();
        Intent i = new Intent(con, LightFloatService.class);
        i.setAction(ACTION_DISMISS_WIDGET);
        con.startService(i);
    }
}
