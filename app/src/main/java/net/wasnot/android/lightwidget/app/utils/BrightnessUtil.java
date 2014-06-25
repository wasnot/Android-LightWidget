package net.wasnot.android.lightwidget.app.utils;

import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by aidaakihiro on 2014/06/21.
 */
public class BrightnessUtil {

    public static int getBrightnessLevel(Context con) {
// 端末画面の明るさを取得(0～255)
        String valueStr = Settings.System.getString(con.getContentResolver(), "screen_brightness");
// Toast.makeText(con, "brightness:" + valueStr,
// Toast.LENGTH_SHORT).show();
        int value = 150;
        try {
            value = Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (value > 150) {
            return 2;
        } else if (value > 80) {
            return 1;
        } else {
            return 0;
        }
    }

    public static void setBrightnessLevel(Context con, int level, Window win) {
        int value;
        switch (level) {
            case 0:
                value = 80;
                break;
            case 1:
                value = 150;
                break;
            case 2:
            default:
                value = 255;
                break;
        }
        Settings.System.putInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
        setBrightnessInApp(win, value / 255.0f);
    }

    private static void setBrightnessInApp(Window myWin, float value) {
        if (myWin == null) {
            return;
        }
// Window myWin = dialog.getWindow(); //現在の表示されているウィンドウを取得
        WindowManager.LayoutParams lp = myWin.getAttributes(); // LayoutParams作成
// lp.screenBrightness = 1.0f; // 輝度最大
        lp.screenBrightness = value;
        myWin.setAttributes(lp); // ウィンドウにLayoutParamsを設定
    }
}
