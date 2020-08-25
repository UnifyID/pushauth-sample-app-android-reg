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

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.common.base.Strings;

import java.util.List;

import id.unify.pushauthreferenceapp.databinding.ActivityMainBinding;
import id.unify.sdk.core.CompletionHandler;
import id.unify.sdk.core.UnifyID;
import id.unify.sdk.core.UnifyIDConfig;
import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.PushAuth;
import id.unify.sdk.pushauth.PushAuthMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setup();
    }

    @Override
    public void onResume(){
        super.onResume();
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
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolBar() {
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showSetupText() {
        binding.pushauthInfoContainer.setVisibility(View.GONE);
        binding.setupText.setVisibility(View.VISIBLE);
    }

    private void showPushAuthInfoContainer(String sdkKey, String user) {
        binding.setupText.setVisibility(View.GONE);
        binding.pushauthInfoContainer.setVisibility(View.VISIBLE);
        binding.configuredSdkKey.setText("SDK Key: " + sdkKey);
        binding.configuredUser.setText("User: " + user);
    }

    private void setupPushAuth(String sdkKey, String user) {
        UnifyID.initialize(getApplicationContext(), sdkKey, user, new CompletionHandler() {
            @Override
            public void onCompletion(UnifyIDConfig config) {
                Log.d(TAG, "UnifyID initialization successful");
                PushAuth.initialize(getApplicationContext(), config);
                Utils.showAllPendingPushAuth(PushAuth.getInstance());
            }

            @Override
            public void onFailure(final UnifyIDException e) {
                Log.e(TAG, "UnifyID initialization failed", e);
                Utils.DisplayUnifyIDException(e, MainActivity.this);
            }
        });
    }

    private void setup() {
        setupToolBar();

        String sdkKey = Preferences.getString(Preferences.SDK_KEY);
        String user = Preferences.getString(Preferences.USER);
        boolean appConfigured = !Strings.isNullOrEmpty(sdkKey) && !Strings.isNullOrEmpty(user);
        if (appConfigured) {
            showPushAuthInfoContainer(sdkKey, user);
            if (PushAuth.getInstance() == null) {
                setupPushAuth(sdkKey, user);
            }
        } else {
            // If app not configured, show setup text to prompt user to do that
            showSetupText();
        }
    }
}
