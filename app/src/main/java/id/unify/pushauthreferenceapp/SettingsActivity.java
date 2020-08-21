/*
 * Copyright © 2020 UnifyID, Inc. All rights reserved.
 * Unauthorized copying or excerpting via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package id.unify.pushauthreferenceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.UnifyIDPushAuthModule;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button confirmButton = (Button) findViewById(R.id.ConfirmButton);
        confirmButton.setOnClickListener(confirmButtonListener);
    }

    private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText sdkKeyInput = (EditText) findViewById(R.id.SDKKeyInput);
            final String sdkKey = sdkKeyInput.getText().toString();
            EditText userIDInput = (EditText) findViewById(R.id.UserIDInput);
            final String user = userIDInput.getText().toString();

            // Initialize an instance of the PushAuth SDK
            List modules = new ArrayList<>();
            modules.add(new UnifyIDPushAuthModule());

            UnifyID.initialize(getApplicationContext(), sdkKey, user, modules, new CompletionHandler() {
                @Override
                public void onCompletion() {
                    // Initialization successful
                    Log.d(TAG, "Initialization successful");

                    Intent intent = new Intent(SettingsActivity.this, WaitingActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }

                @Override
                public void onFailure(UnifyIDException e) {
                    // Initialization failed
                    Log.e(TAG, "Initialization failed", e);

                    final String errorMessage = "Initialization failed: " + e.getMessage();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    };
}
