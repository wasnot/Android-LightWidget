package net.wasnot.android.lightwidget.app;

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
    public LightFloatService() {
    }


    private final static String TAG = MessageService.class.getSimpleName();
    private final static String ACTION_SHOW_MESSAGE = "jp.freebit.family.ACTION_SHOW_MESSAGE";
    private final static String ACTION_DISSMISS_MESSAGE
            = "jp.freebit.family.ACTION_DISSMISS_MESSAGE";

    private static final String TODAY_PATTERN = "H時mm分";
    private static final String PAST_PATTERN = "M月d日H時";
    private final static SimpleDateFormat todayFormat = new SimpleDateFormat(TODAY_PATTERN);
    private final static SimpleDateFormat pastFormat = new SimpleDateFormat(PAST_PATTERN);

    int retryInterval = 5;

    private final static int NOTIFICATION_ID = 111;
    private final static long REPEAT_INTERVAL = 10000;
    private final static boolean IS_DEBUG = false;

    private final static String ARG_MESSAGE = "meesage";
    private final static String ARG_MESSAGE_ID = "meesage_id";
    private final static String ARG_SEND_MILLI = "send_milli";

    private LinkedList<MessageItem> mMessageItems;
    private MessageItem mNowMessage;

    //    private DevicePolicyManager mDevicePolicyManager;
//    private ComponentName mName;
    private WindowManager mWindowManager;
    private View mView;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
// DevicePolicyManagerを取得する。
//        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
// ComponentNameを取得する。
//        mName = new ComponentName(this, SecureAdminReceiver.class);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mMessageItems = new LinkedList<MessageItem>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand " + (intent != null ? intent.getAction() : "null"));
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String action = intent.getAction();
        if (ACTION_SHOW_MESSAGE.equals(action)) {
            if (intent.hasExtra(ARG_MESSAGE) && intent.hasExtra(ARG_MESSAGE_ID) && intent
                    .hasExtra(ARG_SEND_MILLI)) {
                MessageItem item = new MessageItem();
                item.message = intent.getStringExtra(ARG_MESSAGE);
                item.messageId = intent.getStringExtra(ARG_MESSAGE_ID);
                item.sendDate = intent.getLongExtra(ARG_SEND_MILLI, 0L);
                mMessageItems.offer(item);
            }
            if (mNowMessage == null && mMessageItems.size() > 0) {
                mNowMessage = mMessageItems.poll();
            }
            if (mNowMessage != null) {
                startForeground(1, NotificationUtil.sendNotification(
                        this, 1, "伝言", mNowMessage.message, false));
                showLockMessage();
                setAlarm();
            }
        } else if (ACTION_DISSMISS_MESSAGE.equals(action)) {
            dissmissLockMessage();
            mNowMessage = null;
            if (mMessageItems.size() == 0) {
                cancelAlarm();
                stopSelf();
                stopForeground(false);
            } else {
                Intent i = new Intent(getApplicationContext(), MessageService.class);
                i.setAction(ACTION_SHOW_MESSAGE);
                startService(i);
            }
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

    /**
     * 解除用ボタンを表示, onClickイベントを設定
     */
    private void showLockMessage() {
        if (mNowMessage == null) {
            return;
        }
        if (mView != null) {
            updateLeaveCount();
            return;
        }
//        setIsTapping(false);
// ロック状態を取得
// KeyguardManager kgm = (KeyguardManager)
// getSystemService(Context.KEYGUARD_SERVICE);
// LogUtil.e(TAG, "lock is " + kgm.inKeyguardRestrictedInputMode());
// if (kgm.inKeyguardRestrictedInputMode()) {
        mView = LayoutInflater.from(this).inflate(R.layout.activity_message2, null);
// android.view.WindowManager.LayoutParams
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Dialog;
        params.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
// | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
// | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        ;
        params.dimAmount = 0.6f;

        if (mNowMessage.message != null) {
            ((TextView) mView.findViewById(R.id.messageTextView)).setText(mNowMessage.message);
        }
        ((TextView) mView.findViewById(R.id.timeTextView)).setText(
                getTimeString(mNowMessage.sendDate) + "送信");
        updateLeaveCount();
        View button = mView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "メッセージを確認できたらボタンを長押ししてください";
//                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
//                    ((TextView) v).setError(text);
                ButtonPopup.popup(getApplicationContext(), v, text);
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ReadMessage.ReadMessageWithRetry(mNowMessage.messageId, null);
                Intent i = new Intent(getApplicationContext(), MessageService.class);
                i.setAction(ACTION_DISSMISS_MESSAGE);
                startService(i);
                return true;
            }
        });
        mWindowManager.addView(mView, params);
// }
    }


    /**
     * ロック画面のメッセージを隠す
     */
    private void dissmissLockMessage() {
        if (mView != null) {
            mWindowManager.removeView(mView);
        }
        mView = null;
    }

    private String getTimeString(long timeMilli) {
        if (timeMilli == 0) {
            return todayFormat.format(new Date());
        }

        Date sendDate = new Date(timeMilli);
        Calendar send = Calendar.getInstance();
        send.setTimeInMillis(timeMilli);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        if (send.before(yesterday)) {
            return pastFormat.format(sendDate);
        }
        return todayFormat.format(sendDate);
    }

    private void updateLeaveCount() {
        if (mView == null || mMessageItems == null) {
            return;
        }
        int size = mMessageItems.size();
        TextView leaveCount = (TextView) mView.findViewById(R.id.leaveCountTextView);
        if (size == 0) {
            leaveCount.setText("");
        } else {
            leaveCount.setText("未読残り" + size + "件");
        }
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
        Intent intent = new Intent(con, MessageService.class);
        intent.setAction(ACTION_SHOW_MESSAGE);
// 同じpendingintentなら上書きらしい。
        return PendingIntent.getService(con, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void startService(Context context, String message, String messageId,
            long sendMilli) {
        Context con = context.getApplicationContext();
        Intent i = new Intent(con, MessageService.class);
        i.setAction(ACTION_SHOW_MESSAGE);
        i.putExtra(ARG_MESSAGE, message);
        i.putExtra(ARG_MESSAGE_ID, messageId);
        i.putExtra(ARG_SEND_MILLI, sendMilli);
        con.startService(i);
    }
}
