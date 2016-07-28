package com.snow.cc.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author   Shone
 * Date     28/07/16.
 * Github   https://github.com/shonegg
 */
public class Config {

    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();
    private static SharedPreferences sharedPreference;
    private static SharedPreferences.Editor editor;

    static {
        sharedPreference = App.getContext().getSharedPreferences("app_config",
                Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
    }


    public static boolean getIsFirstLaunch() {
        return sharedPreference.getBoolean("key_is_first_launch", true);
    }


    public static void setIsFirstLaunch(boolean isFirstLaunch) {
        Config.editor.putBoolean("key_is_first_launch", isFirstLaunch);
        Config.submit();
    }

    private static void submit() {
        if (Build.VERSION.SDK_INT > 9) {
            editor.apply();
        } else {
            POOL.execute(new Runnable() {
                public void run() {
                    Config.editor.commit();
                }
            });
        }
    }
}
