package id.unify.pushauthreferenceapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.UnifyIDPushAuthModule;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button confirmButton = (Button) findViewById(R.id.ConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText sdkKeyInput = (EditText) findViewById(R.id.SDKKeyInput);
                String sdkKey = sdkKeyInput.getText().toString();
                EditText userIDInput = (EditText) findViewById(R.id.UserIDInput);
                String user = userIDInput.getText().toString();

                // Initialize an instance of the PushAuth SDK
                List modules = new ArrayList<>();
                modules.add(new UnifyIDPushAuthModule());

                UnifyID.initialize(getApplicationContext(), sdkKey, user, modules, new CompletionHandler() {
                    @Override
                    public void onCompletion() {
                        // Initialization successful
                        Log.d(TAG, "Initialization successful");
                    }

                    @Override
                    public void onFailure(UnifyIDException e) {
                        // Initialization failed
                        Log.e(TAG, "Initialization failed", e);
                    }
                });
            }
        });
    }
}