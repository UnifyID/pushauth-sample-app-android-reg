/*
 * Copyright © 2020 UnifyID, Inc. All rights reserved.
 * Unauthorized copying or excerpting via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package id.unify.pushauthreferenceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDConfig;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.PushAuth;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button confirmButton = findViewById(R.id.ConfirmButton);
        confirmButton.setOnClickListener(confirmButtonListener);
    }

    private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText sdkKeyInput = findViewById(R.id.SDKKeyInput);
            final String sdkKey = sdkKeyInput.getText().toString();
            EditText userIDInput = findViewById(R.id.UserIDInput);
            final String user = userIDInput.getText().toString();

            // Initialize an instance of the PushAuth SDK
            UnifyID.initialize(getApplicationContext(), sdkKey, user, new CompletionHandler() {
                @Override
                public void onCompletion(UnifyIDConfig config) {
                    Log.d(TAG, "Initialization successful");

                    PushAuth.initialize(getApplicationContext(), config);

                    Intent intent = new Intent(SettingsActivity.this,
                            WaitingActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

                @Override
                public void onFailure(UnifyIDException e) {
                    Log.e(TAG, "Initialization failed", e);

                    final String errorMessage = "Initialization failed: " + e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SettingsActivity.this, errorMessage,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    };
}
