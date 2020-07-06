package id.unify.pushauthreferenceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

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
        String sdkKey = "https://0ecee7dda4dc56e1cc7d04088d51993b@config.unify.id";
        String user = "398ytq9p43qreutp9udfojgdsl";

        /*
        // Initialize an instance of the PushAuth SDK
        List modules = new ArrayList<>();
        modules.add(new UnifyIDPushAuthModule());
        UnifyID.initialize(getApplicationContext(), sdkKey, user, modules, new CompletionHandler() {
            @Override
            public void onCompletion() {
                // Initialization was successful
                Context context = getApplicationContext();
                CharSequence text = "Initialization successful!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onFailure(UnifyIDException e) {
                // Initialization failed
            }
        });
        */
    }
}