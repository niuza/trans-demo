package com.niuza.trans.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by niuza on 5.23 023.
 */

public class TransferCounter {
    private static final String fileName = "TransRecord";

    public static boolean setRecord(Context ctx, int count, long size) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        count += sharedPreferences.getInt("COUNT", 0);
        size += sharedPreferences.getLong("SIZE", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("COUNT", count);
        editor.putLong("SIZE", size);
        return editor.commit();
    }

    public static int getCount(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt("COUNT", 0);
        return count;
    }

    public static long getSize(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        long size = sharedPreferences.getLong("SIZE", 0);
        return size;
    }

    public static void clearData(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

}
