package net.wasnot.android.lightwidget.app.utils;

import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by aidaakihiro on 2014/07/04.
 */
public class DisplayUtil {

    public static Point getDisplaySize(WindowManager wm) {
        Point point = new Point();
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(point);
//            display.getRealSize(point);
        } else {
            point.set(display.getWidth(), display.getHeight());
        }
        return point;
    }
}
