package net.wasnot.android.lightwidget.app;

import net.wasnot.android.lightwidget.app.utils.BrightnessUtil;
import net.wasnot.android.lightwidget.app.utils.DisplayUtil;
import net.wasnot.android.lightwidget.app.utils.LogUtil;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by aidaakihiro on 2014/06/21.
 */
public class WindowWidgetManager
        implements View.OnClickListener, View.OnLongClickListener, SeekBar.OnSeekBarChangeListener,
        View.OnTouchListener {

    private final static String TAG = WindowWidgetManager.class.getSimpleName();

    private View mView;
    private WindowManager mWindowManager;
    private LayoutInflater mLayoutInflater;
    private Point mSize;

    private float mTouchDownX;
    private float mTouchDownY;
    private float mXRate = 0;
    private float mYRate = 0;
    private float mBrightness;

    public WindowWidgetManager(Context con) {
        mWindowManager = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        mLayoutInflater = LayoutInflater.from(con);
        mSize = DisplayUtil.getDisplaySize(mWindowManager);
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
        mView.findViewById(R.id.dragButton).setOnTouchListener(this);
        View button = mView.findViewById(R.id.button);
        button.setOnClickListener(this);
        button.setOnLongClickListener(this);

        mBrightness = BrightnessUtil.getBrightnessRate(mLayoutInflater.getContext());
        SeekBar seekBar = (SeekBar) mView.findViewById(R.id.seekBar);
        seekBar.setProgress((int) (mBrightness * 100));
        seekBar.setOnSeekBarChangeListener(this);
        mWindowManager.addView(mView, makeLayoutParams());
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


    @Override
    public void onClick(View v) {
        String text = "メッセージを確認できたらボタンを長押ししてください";
        Toast.makeText(mLayoutInflater.getContext(), text, Toast.LENGTH_SHORT).show();
        ((TextView) v).setError(text);
    }

    @Override
    public boolean onLongClick(View v) {
        LightFloatService.stopService(v.getContext());
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.d(TAG,
                "onTouch " + event.getX() + "," + event.getY() + " " + mXRate + "," + mYRate);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mXRate += (event.getX() - v.getWidth() / 2) / mSize.x;
                if (mXRate > 1) {
                    mXRate = 1;
                }
                if (mXRate < 0) {
                    mXRate = 0;
                }
                mYRate += (event.getY() - v.getHeight() / 2) / mSize.y;
                if (mYRate > 1) {
                    mYRate = 1;
                }
                if (mYRate < 0) {
                    mYRate = 0;
                }
                mWindowManager.updateViewLayout(mView, makeLayoutParams());
                break;
            case MotionEvent.ACTION_UP:
                mTouchDownX = 0;
                mTouchDownY = 0;
                break;
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        LogUtil.d(TAG, "onProgressChanged:" + progress);
        BrightnessUtil.setBrightness(mLayoutInflater.getContext(), progress, null);
        mBrightness = progress / 100f;
        mWindowManager.updateViewLayout(mView, makeLayoutParams());
        LogUtil.d(TAG,
                "brightness : " + BrightnessUtil.getBrightnessRate(mLayoutInflater.getContext()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private WindowManager.LayoutParams makeLayoutParams() {
// android.view.WindowManager.LayoutParams
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.horizontalMargin = mXRate;
        params.verticalMargin = mYRate;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = android.R.style.Animation_Dialog;
        params.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_DIM_BEHIND
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
//| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        ;
//        params.dimAmount = 0.6f;
        params.screenBrightness = mBrightness;
        return params;
    }
}
