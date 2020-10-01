/*
 * Copyright © 2020 UnifyID, Inc. All rights reserved.
 * Unauthorized copying or excerpting via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package id.unify.pushauthreferenceapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.common.base.Strings;

import id.unify.pushauthreferenceapp.databinding.ActivitySettingsBinding;
import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDConfig;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.PushAuth;
import id.unify.sdk.pushauth.PushAuthException;
import id.unify.sdk.pushauth.TokenRegistrationHandler;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.confirmBtn.setOnClickListener(confirmButtonListener);
        setup();
    }

    private void setup() {
        String existingSdkKey = Preferences.getString(Preferences.SDK_KEY);
        String existingUser = Preferences.getString(Preferences.USER);
        String existingPairingCode = Preferences.getString(Preferences.PAIRING_CODE);
        if (!Strings.isNullOrEmpty(existingSdkKey)) {
            binding.sdkKeyInput.setText(existingSdkKey);
        }
        if (!Strings.isNullOrEmpty(existingUser)) {
            binding.userInput.setText(existingUser);
        }
        if (!Strings.isNullOrEmpty(existingPairingCode)) {
            binding.pairingCodeInput.setText(existingPairingCode);
        }
    }

    private void storeConfiguration(String sdkKey, String user, String pairingCode) {
        Preferences.put(Preferences.SDK_KEY, sdkKey);
        Preferences.put(Preferences.USER, user);
        Preferences.put(Preferences.PAIRING_CODE, pairingCode);
    }

    private void showConfirmUI() {
        binding.confirmBtn.setVisibility(View.GONE);
        binding.settingActivityPb.setVisibility(View.VISIBLE);
    }

    private void hideConfirmUI() {
        binding.confirmBtn.setVisibility(View.VISIBLE);
        binding.settingActivityPb.setVisibility(View.GONE);
    }

    private void showDeregisterFailureDialog() {
        new AlertDialog
                .Builder(SettingsActivity.this)
                .setTitle("PushAuth Deregister Failed")
                .setMessage(
                        "Currently registered PushAuth client wasn't "
                                + "deregistered "
                                + "successfully, please retry submit.")
                .show();
    }

    private boolean deregisterUnifyID() {
        boolean deregistered = false;
        try {
            PushAuth.getInstance().deregisterCurrentClient();
            deregistered = true;
        } catch (PushAuthException e) {
            Log.e(TAG, "Failed to deregister PushAuth client", e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDeregisterFailureDialog();
                    hideConfirmUI();
                }
            });
        }
        return deregistered;
    }

    private void setupUnifyID(final String sdkKey, final String user, final String pairingCode) {
        UnifyID.initialize(getApplicationContext(), sdkKey, user, pairingCode, new CompletionHandler() {
            @Override
            public void onCompletion(UnifyIDConfig config) {
                Log.d(TAG, "UnifyID initialization successful");
                PushAuth.initialize(getApplicationContext(), config);
                storeConfiguration(sdkKey, user, pairingCode);

                PushAuth.getInstance().registerPushAuthToken(new TokenRegistrationHandler() {
                    @Override
                    public void onComplete() {
                        Utils.showAllPendingPushAuth(PushAuth.getInstance());

                        // close activity with result
                        Intent intent = new Intent();
                        intent.putExtra(MainActivity.SETTING_ACTIVITY_RESULT_KEY, true);
                        setResult(MainActivity.SETTING_ACTIVITY_REQUEST_CODE, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog
                                        .Builder(SettingsActivity.this)
                                        .setTitle("PushAuth Token Registration Failed")
                                        .setMessage("PushAuth is not fully initialized, "
                                                + "please submit again to recover.")
                                        .show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(UnifyIDException e) {
                Log.e(TAG, "UnifyID initialization failed", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideConfirmUI();
                    }
                });
                Utils.displayUnifyIDException(e, SettingsActivity.this);
            }
        });
    }

    private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showConfirmUI();

            final String sdkKey = binding.sdkKeyInput.getText().toString().trim();
            final String user = binding.userInput.getText().toString().trim();
            final String pairingCode = binding.pairingCodeInput.getText().toString().trim();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (PushAuth.getInstance() != null) {
                        if (!deregisterUnifyID()) {
                            return;
                        }
                    }
                    setupUnifyID(sdkKey, user, pairingCode);
                }
            }).start();
        }
    };
}
