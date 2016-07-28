package com.snow.cc.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Author   Shone
 * Date     28/07/16.
 * Github   https://github.com/shonegg
 */
public class ShortcutUtils {


    private static ScheduledThreadPoolExecutor EXECUTOR;

    static {
        ShortcutUtils.EXECUTOR = new ScheduledThreadPoolExecutor(2);
    }


    private static boolean addShortcut(String name, int iconId, Context context, Intent intent) {
        Intent intent1 = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent1.putExtra("duplicate", false);
        intent1.putExtra("android.intent.extra.shortcut.NAME", name);
        intent1.putExtra("android.intent.extra.shortcut.ICON", BitmapFactory.decodeResource(context.getResources(),
                iconId));
        intent1.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(
                context, iconId));
        intent1.putExtra("android.intent.extra.shortcut.INTENT", intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//65536
        context.sendBroadcast(intent1);
        return true;
    }

    public static void buildShortcut(final String name, final int iconId, final Intent intent, final Context context) {
        EXECUTOR.execute(new Runnable() {
            public void run() {
                if (!LauncherUtil.isShortCutExist(context, name)) {
                    addShortcut(name, iconId, context, intent);
                }
            }
        });
    }

}
