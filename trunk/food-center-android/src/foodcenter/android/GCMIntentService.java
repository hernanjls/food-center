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
import android.os.AsyncTask;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.activities.main.LoginActivity;
import foodcenter.android.activities.main.MainActivity;
import foodcenter.android.service.AndroidRequestUtils;
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
        AndroidUtils.displayMessage(context, getString(R.string.gcm_registered));

        Log.i(TAG, "Server: registering gcm device (regId = " + regId + ")");
        new ServerLoginAsyncTask(context, regId, 5).execute();
    }

    @Override
    protected void onUnregistered(Context context, String regId)
    {
        Log.i(TAG, "Device unregistered");
        AndroidUtils.displayMessage(context, getString(R.string.gcm_unregistered));

        if (GCMRegistrar.isRegisteredOnServer(context))
        {
            Log.i(TAG, "unregistering device (regId = " + regId + ")");
            new GCMUnRegisterAsyncTask(context).execute();
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
        AndroidUtils.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        AndroidUtils.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId)
    {
        Log.e(TAG, "Received error: " + errorId);
        AndroidUtils.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        AndroidUtils.displayMessage(context, getString(R.string.gcm_recoverable_error, errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message)
    {

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_food_center)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                                                                          PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, builder.getNotification());
    }
}

class GCMUnRegisterAsyncTask extends AsyncTask<Void, String, Void>
{

    private final Context context;

    public GCMUnRegisterAsyncTask(final Context context)
    {
        this.context = context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(context);
            factory.getClientService().logout().fire(new GCMUnregisterReciever());
        }
        catch (Exception e)
        {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
            publishProgress(e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values)
    {
        if (null != values && values.length > 0)
        {
            LoginActivity.showSpinner(values[0]);
        }
        LoginActivity.closeLoginActivity(false);

    }

    class GCMUnregisterReciever extends Receiver<Void>
    {
        @Override
        public void onSuccess(Void arg0)
        {
            GCMRegistrar.setRegisteredOnServer(context, false);

            // Delete the current auth cookie from shared preferences
            Editor editor = AndroidRequestUtils.getSharedPreferences(context).edit();
            editor.putString(AndroidRequestUtils.ACCOUNT_NAME, null).commit();
            editor.putString(AndroidRequestUtils.AUTH_COOKIE, null);
            editor.commit();
            publishProgress();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            GCMRegistrar.setRegisteredOnServer(context, false);

            // Delete the current auth cookie from shared preferences
            Editor editor = AndroidRequestUtils.getSharedPreferences(context).edit();
            editor.putString(AndroidRequestUtils.ACCOUNT_NAME, null).commit();
            editor.putString(AndroidRequestUtils.AUTH_COOKIE, null);
            editor.commit();

            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String msg = context.getString(R.string.server_unregister_error, error.getMessage());
            publishProgress(msg);
        }
    }
}
