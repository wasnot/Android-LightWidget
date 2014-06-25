package net.wasnot.android.lightwidget.app;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/**
 * Created by aidaakihiro on 2014/06/21.
 */
public class WindowWidgetManager {

    private final static String TAG = WindowWidgetManager.class.getSimpleName();

    private static final String TODAY_PATTERN = "H時mm分";
    private static final String PAST_PATTERN = "M月d日H時";
    private final static SimpleDateFormat todayFormat = new SimpleDateFormat(TODAY_PATTERN);
    private final static SimpleDateFormat pastFormat = new SimpleDateFormat(PAST_PATTERN);

    private View mView;
    private WindowManager mWindowManager;
    private LayoutInflater mLayoutInflater;

    public WindowWidgetManager(Context con) {
        mWindowManager = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = LayoutInflater.from(con);
    }

    /**
     * 解除用ボタンを表示, onClickイベントを設定
     */
    public void showLockMessage() {
        if (mView != null) {
            return;
        }
//        setIsTapping(false);
// ロック状態を取得
// KeyguardManager kgm = (KeyguardManager)
// getSystemService(Context.KEYGUARD_SERVICE);
// LogUtil.e(TAG, "lock is " + kgm.inKeyguardRestrictedInputMode());
// if (kgm.inKeyguardRestrictedInputMode()) {
        mView = mLayoutInflater.inflate(R.layout.layout_float_simple, null);
// android.view.WindowManager.LayoutParams
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = android.R.style.Animation_Dialog;
        params.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
//| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        ;
//        params.dimAmount = 0.6f;
        View button = mView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "メッセージを確認できたらボタンを長押ししてください";
                Toast.makeText(mLayoutInflater.getContext(), text, Toast.LENGTH_SHORT).show();
                ((TextView) v).setError(text);
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ReadMessage.ReadMessageWithRetry(mNowMessage.messageId, null);
                LightFloatService.stopService(v.getContext());
                return true;
            }
        });
        mWindowManager.addView(mView, params);
// }
    }

    /**
     * ロック画面のメッセージを隠す
     */
    public void dissmissLockMessage() {
        if (mView != null) {
            mWindowManager.removeView(mView);
        }
        mView = null;
    }
}
