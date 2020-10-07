/*
 * Copyright © 2020 UnifyID, Inc. All rights reserved.
 * Unauthorized copying or excerpting via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package id.unify.pushauthreferenceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.common.base.Strings;

import id.unify.pushauthreferenceapp.databinding.ActivityMainBinding;
import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDConfig;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.PushAuth;
import id.unify.sdk.pushauth.TokenRegistrationHandler;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final int SETTING_ACTIVITY_REQUEST_CODE = 10;
    public static final String SETTING_ACTIVITY_RESULT_KEY = "SDK_INITIALIZED";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.SettingsButton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SETTING_ACTIVITY_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTING_ACTIVITY_REQUEST_CODE && data != null) {
            boolean sdkInitialized = data.getBooleanExtra(SETTING_ACTIVITY_RESULT_KEY,
                    false);
            if (sdkInitialized) {
                String sdkKey = Preferences.getString(Preferences.SDK_KEY);
                String user = Preferences.getString(Preferences.USER);
                String pairingCode = Preferences.getString(Preferences.PAIRING_CODE);
                boolean appConfigured = !Strings.isNullOrEmpty(sdkKey) && !Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(pairingCode);
                if (appConfigured) {
                    showPushAuthInfoContainer(user);
                } else {
                    showSetupText();
                }
            }
        }
    }

    private void setupToolBar() {
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showSetupText() {
        binding.pushauthInfoContainer.setVisibility(View.GONE);
        binding.configurationTextContainer.setVisibility(View.VISIBLE);
    }

    private void showPushAuthInfoContainer(String user) {
        binding.configurationTextContainer.setVisibility(View.GONE);
        binding.pushauthInfoContainer.setVisibility(View.VISIBLE);
        binding.configuredUser.setText("User: " + user);
    }

    private void setupPushAuth(String sdkKey, String user, String pairingCode) {
        UnifyID.initialize(getApplicationContext(), sdkKey, user, pairingCode, new CompletionHandler() {
            @Override
            public void onCompletion(UnifyIDConfig config) {
                Log.d(TAG, "UnifyID initialization successful");
                PushAuth.initialize(getApplicationContext(), config);
                PushAuth.getInstance().registerPushAuthToken(new TokenRegistrationHandler() {
                    @Override
                    public void onComplete() {
                        Utils.showAllPendingPushAuth(PushAuth.getInstance());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog
                                        .Builder(MainActivity.this)
                                        .setTitle("PushAuth Token Registration Failed")
                                        .setMessage("PushAuth won't work correctly, please close "
                                                + "the app and reopen it to recover.")
                                        .show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(final UnifyIDException e) {
                Log.e(TAG, "UnifyID initialization failed", e);
                Utils.displayUnifyIDException(e, MainActivity.this);
            }
        });
    }

    private void setup() {
        setupToolBar();

        String sdkKey = Preferences.getString(Preferences.SDK_KEY);
        String user = Preferences.getString(Preferences.USER);
        String pairingCode = Preferences.getString(Preferences.PAIRING_CODE);
        boolean appConfigured = !Strings.isNullOrEmpty(sdkKey) && !Strings.isNullOrEmpty(user) && !Strings.isNullOrEmpty(pairingCode);
        if (appConfigured) {
            showPushAuthInfoContainer(user);
            if (PushAuth.getInstance() == null) {
                setupPushAuth(sdkKey, user, pairingCode);
            }
        } else {
            // If app not configured, show setup text to prompt user to do that
            showSetupText();
        }
    }
}
