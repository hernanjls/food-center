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
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.activities.login.ServerSigninTask;
import foodcenter.android.activities.main.MainActivity;
import foodcenter.android.service.AndroidRequestUtils;
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
        AndroidUtils.progress(context, getString(R.string.gcm_registered));

        new ServerSigninTask(context, regId, 5).signIn();
    }

    @Override
    protected void onUnregistered(Context context, String regId)
    {
        Log.i(TAG, "Device unregistered");
        AndroidUtils.toast(context, getString(R.string.gcm_unregistered));

        if (GCMRegistrar.isRegisteredOnServer(context))
        {
            Log.i(TAG, "unregistering device (regId = " + regId + ")");
            signOut(context);
        }
        else
        {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.

            Log.i(TAG, "Ignoring unregister callback");
            AndroidUtils.notifySignOut(context, null);
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("msg");
        AndroidUtils.toast(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        AndroidUtils.toast(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId)
    {
        Log.e(TAG, "Received error: " + errorId);
        AndroidUtils.toast(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        AndroidUtils.toast(context, getString(R.string.gcm_recoverable_error, errorId));
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

    private void signOut(final Context context)
    {
        String msg = "Signing out of server ..."; // TODO change to R.strings
        AndroidUtils.progress(context, msg);

        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(context);
            factory.getClientService().logout().fire(new SignOutReciever(context));
        }
        catch (Exception e)
        {
            msg = e.getMessage();
            Log.e(getClass().getSimpleName(), msg, e);
            AndroidUtils.progressDismissAndToastMsg(context, msg);
            return;
        }
    }

    class SignOutReciever extends Receiver<Void>
    {
        private final Context context;

        public SignOutReciever(Context context)
        {
            this.context = context;
        }

        /** Common for both success or failure */
        private void onCommon()
        {
            GCMRegistrar.setRegisteredOnServer(context, false);
            
            //Delete the current auth cookie from shared preferences 
            Editor editor = AndroidRequestUtils.getSharedPreferences(context).edit();
            editor.putString(AndroidRequestUtils.PREF_ACCOUNT_NAME, null);
            editor.putString(AndroidRequestUtils.AUTH_COOKIE, null);
            editor.commit();

        }

        @Override
        public void onSuccess(Void arg0)
        {
            onCommon();
            AndroidUtils.notifySignOut(context, null);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            onCommon();

            // At this point the device is unregistered from GCM, but still registered in the
            // server. We could try to unregister again, but it is not necessary. server deals
            // with "NotRegistered" error
            String msg = context.getString(R.string.server_unregister_error, error.getMessage());
            AndroidUtils.notifySignOut(context, msg);
        }
    }

}
