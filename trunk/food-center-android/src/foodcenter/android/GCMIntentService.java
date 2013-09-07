/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package foodcenter.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.activities.main.LoginActivity;
import foodcenter.android.activities.main.MainActivity;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.login.ServerLoginAsyncTask;
import foodcenter.service.FoodCenterRequestFactory;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService
{

    public static final String TAG = "GCMIntentService";
    public static final int MAX_ATTEMPTS = 5;

    /** Google API project id registered to use GCM. */
    public static final String GCM_SENDER_ID = "621422938667";

    public GCMIntentService()
    {
        super(GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String regId)
    {
        Log.i(TAG, "GCM: Device registered: regId = " + regId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));

        Log.i(TAG, "Server: registering gcm device (regId = " + regId + ")");
        new ServerLoginAsyncTask(context, regId, 5).execute();
    }

    @Override
    protected void onUnregistered(Context context, String regId)
    {
        Log.i(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));

        if (GCMRegistrar.isRegisteredOnServer(context))
        {
            Log.i(TAG, "unregistering device (regId = " + regId + ")");
            FoodCenterRequestFactory factory = RequestUtils
                .getRequestFactory(context, FoodCenterRequestFactory.class);
            factory.getClientService().logout().fire(new GCMUnRegisterReciever(context));
        }
        else
        {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
            LoginActivity.closeLoginActivity(false);
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("msg");
        CommonUtilities.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        CommonUtilities.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId)
    {
        Log.e(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message)
    {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}

class GCMUnRegisterReciever extends Receiver<Void>
{

    private final Context context;

    public GCMUnRegisterReciever(final Context context)
    {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onSuccess(Void arg0)
    {
        GCMRegistrar.setRegisteredOnServer(context, false);

        // Delete the current auth cookie from shared preferences
        Editor editor = RequestUtils.getSharedPreferences(context).edit();
        editor.putString(RequestUtils.ACCOUNT_NAME, null).commit();
        editor.putString(RequestUtils.AUTH_COOKIE, null);
        editor.commit();
        LoginActivity.closeLoginActivity(false);
    }

    @Override
    public void onFailure(ServerFailure error)
    {
        GCMRegistrar.setRegisteredOnServer(context, false);

        // Delete the current auth cookie from shared preferences
        Editor editor = RequestUtils.getSharedPreferences(context).edit();
        editor.putString(RequestUtils.ACCOUNT_NAME, null).commit();
        editor.putString(RequestUtils.AUTH_COOKIE, null);
        editor.commit();
        
        // At this point the device is unregistered from GCM, but still
        // registered in the server.
        // We could try to unregister again, but it is not necessary:
        // if the server tries to send a message to the device, it will get
        // a "NotRegistered" error message and should unregister the device.
        String msg = context.getString(R.string.server_unregister_error, error.getMessage());
        LoginActivity.showSpinner(msg);
        LoginActivity.closeLoginActivity(false);
    }
}
