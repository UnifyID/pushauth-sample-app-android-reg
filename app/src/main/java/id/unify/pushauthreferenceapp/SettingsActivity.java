/*
 * Copyright © 2020 UnifyID, Inc. All rights reserved.
 * Unauthorized copying or excerpting via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package id.unify.pushauthreferenceapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import id.unify.pushauthreferenceapp.databinding.ActivitySettingsBinding;
import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDConfig;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.PushAuth;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.confirmBtn.setOnClickListener(confirmButtonListener);
    }

    private void storeConfiguration(String sdkKey, String user) {
        Preferences.put(Preferences.SDK_KEY, sdkKey);
        Preferences.put(Preferences.USER, user);
    }

    private void showConfirmUI() {
        binding.confirmBtn.setVisibility(View.GONE);
        binding.settingActivityPb.setVisibility(View.VISIBLE);
    }

    private void hideConfirmUI() {
        binding.confirmBtn.setVisibility(View.VISIBLE);
        binding.settingActivityPb.setVisibility(View.GONE);
    }

    private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showConfirmUI();

            final String sdkKey = binding.sdkKeyInput.getText().toString().trim();
            final String user = binding.userInput.getText().toString().trim();

            UnifyID.initialize(getApplicationContext(), sdkKey, user, new CompletionHandler() {
                @Override
                public void onCompletion(UnifyIDConfig config) {
                    Log.d(TAG, "UnifyID initialization successful");
                    PushAuth.initialize(getApplicationContext(), config);
                    storeConfiguration(sdkKey, user);
                    Utils.showAllPendingPushAuth(PushAuth.getInstance());
                    finish();
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
                    Utils.DisplayUnifyIDException(e, SettingsActivity.this);
                }
            });
        }
    };
}
