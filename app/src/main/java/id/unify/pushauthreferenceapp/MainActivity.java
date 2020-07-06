package id.unify.pushauthreferenceapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.UnifyIDPushAuthModule;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Temporarily hardcoded SDK key and user identifier
        final String sdkKey = "https://be77a55d572220cd7180e5dc0460476d@config.unify.id";
        final String user = "398ytq9p43qreutp9udfojgdsl";

        // Initialize an instance of the PushAuth SDK
        List modules = new ArrayList<>();
        modules.add(new UnifyIDPushAuthModule());
        UnifyID.initialize(getApplicationContext(), sdkKey, user, modules, new CompletionHandler() {
            @Override
            public void onCompletion() {
                // Initialization successful
                Log.d("info", "Initialization successful");
            }

            @Override
            public void onFailure(UnifyIDException e) {
                // Initialization failed
                Log.d("info", "Initialization failed");
            }
        });
    }
}