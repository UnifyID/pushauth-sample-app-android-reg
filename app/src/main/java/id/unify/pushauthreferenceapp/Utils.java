package id.unify.pushauthreferenceapp;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import id.unify.sdk.core.UnifyIDException;
import id.unify.sdk.pushauth.PushAuth;
import id.unify.sdk.pushauth.PushAuthMessage;

public class Utils {
    static public void displayUnifyIDException(UnifyIDException e, final Activity activity) {
        String msg = "Unknown exception happened, PushAuth feature won't work correctly.";
        switch (e.getErrorCode()) {
            case UnifyIDException.INITIALIZATION_ERROR_INVALID_KEY:
                msg = "SDK key is invalid, please re-configure the SDK key.";
                break;
            case UnifyIDException.INITIALIZATION_ERROR_DEVICE_REGISTRATION_FAILED:
                msg = "Device registration failed, PushAuth feature won't work correctly.";
                break;
            case UnifyIDException.INITIALIZATION_ERROR_INITIALIZATION_FAILED:
                msg = "UnifyID initialization failed, PushAuth feature won't work correctly.";
                break;
            case UnifyIDException.INITIALIZATION_ERROR_INVALID_USER:
                msg = "User is invalid, please re-configure the user.";
                break;
            case UnifyIDException.NETWORK_ERROR_INVALID_RESPONSE:
                msg = "Unexpected network response, please close the app and reopen.";
                break;
            case UnifyIDException.NETWORK_ERROR_NOT_AUTHORIZED:
                msg = "Unauthorized, please re-configure SDK key and user.";
                break;
            case UnifyIDException.NETWORK_ERROR_REQUEST_FAILURE:
                msg = "Request failed, please close the app and reopen.";
                break;
            case UnifyIDException.NETWORK_ERROR_UNAVAILABLE:
                msg = "No network access, please make sure the device is connected to "
                        + "internet, close the app and reopen.";
                break;
            default:
                break;
        }

        final String finalMsg = msg;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog
                        .Builder(activity)
                        .setTitle("UnifyID Initialization Failed")
                        .setMessage(finalMsg)
                        .show();
            }
        });
    }

    static public void showAllPendingPushAuth(PushAuth pushAuth) {
        List<PushAuthMessage> pendingPushAuth = pushAuth.getPendingPushAuth();
        if (!pendingPushAuth.isEmpty()) {
            for (PushAuthMessage pushAuthMessage: pendingPushAuth) {
                pushAuth.showPushAuth(pushAuthMessage);
            }
        }
    }
}
