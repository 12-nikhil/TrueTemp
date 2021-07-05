package softwise.mechatronics.truBlueMonitor.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class HelperPreference {
    public static final String PREFS_NAME = "BluetoothApp";

    public HelperPreference() {
        super();
    }

    public static void saveString(Context context,String pref_key, String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(pref_key, text); //3
        editor.apply(); //4
    }

    public static String getString(Context context,String pref_key) {
        SharedPreferences settings;
        String text;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(pref_key, null);
        return text;
    }
    public static void saveInt(Context context,String pref_key ,int value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putInt(pref_key, value); //3
        editor.apply();
    }

    public static Integer getInteger(Context context,String pref_key) {
        SharedPreferences settings;
        int value;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        value = settings.getInt(pref_key, 0);
        return value;
    }
    public static void saveLong(Context context,String pref_key ,long value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putLong(pref_key, value); //3
        editor.apply();
    }

    public static Long getLong(Context context,String pref_key) {
        SharedPreferences settings;
        long value;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        value = settings.getLong(pref_key, 0);
        return value;
    }
    public static void saveFloat(Context context,String pref_key ,Float value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putFloat(pref_key, value); //3
        editor.apply();
    }

    public static Float getFloat(Context context,String pref_key) {
        SharedPreferences settings;
        float value;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        value = settings.getFloat(pref_key, 0f);
        return value;
    }
    public static void saveBoolean(Context context,String pref_key ,Boolean value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putBoolean(pref_key, value); //3
        editor.apply();
    }

    public static Boolean getBoolean(Context context,String pref_key) {
        SharedPreferences settings;
        boolean value;
        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        value = settings.getBoolean(pref_key, false);
        return value;
    }





    public static boolean clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
       return editor.commit();
    }

    public static void removeValue(Context context,String pref_key) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(pref_key);
        editor.apply();
    }
}
