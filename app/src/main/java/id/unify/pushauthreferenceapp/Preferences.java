package id.unify.pushauthreferenceapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES =
            "id.unify.pushauthreferenceapp.SHARED_PREFERENCES";
    public static final String SDK_KEY = "id.unify.pushauthreferenceapp.SHARED_PREFERENCES.SDK_KEY";
    public static final String USER = "id.unify.pushauthreferenceapp.SHARED_PREFERENCES.USER";
    public static final String PAIRING_CODE = "id.unify.pushauthreferenceapp.SHARED_PREFERENCES.PAIRING_CODE";

    private Preferences() {
    }

    synchronized public static void initialize(Context context) {
        if (Preferences.sharedPreferences == null) {
            Preferences.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, 0);
        }
    }

    synchronized public static String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    synchronized public static void put(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    synchronized public static void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
