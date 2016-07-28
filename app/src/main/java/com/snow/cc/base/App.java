package com.snow.cc.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.snow.cc.R;
import com.snow.cc.ui.LauncherActivity;
import com.snow.cc.utils.ShortcutUtils;

/**
 * Author   Shone
 * Date     28/07/16.
 * Github   https://github.com/shonegg
 */
public class App extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }


    public void onCreate() {
        super.onCreate();
        context = this;
        if (Config.getIsFirstLaunch()) {
            Intent intent = new Intent();
            intent.setClass(this, LauncherActivity.class);
            intent.setAction("com.snow.action.start");
            ShortcutUtils.buildShortcut("点我点我", R.drawable.widget, intent, this);
            Config.setIsFirstLaunch(false);
        }

    }
}
