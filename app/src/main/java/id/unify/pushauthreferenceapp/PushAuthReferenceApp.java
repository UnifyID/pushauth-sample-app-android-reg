package id.unify.pushauthreferenceapp;

import android.app.Application;

public class PushAuthReferenceApp extends Application {
    public void onCreate() {
        Preferences.initialize(getApplicationContext());
        super.onCreate();
    }
}
