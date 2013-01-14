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

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;
import foodcenter.android.service.gcm.CommonUtilities;
import foodcenter.service.FoodCenterRequestFactory;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService
{

    public static final String TAG = "GCMIntentService";
    public static final int MAX_ATTEMPTS = 5;
    
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    public GCMIntentService()
    {
        super(Setup.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        register(context, registrationId, MAX_ATTEMPTS, backoff);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
        Log.i(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
        
        if (GCMRegistrar.isRegisteredOnServer(context))
        {
            unregister(context, registrationId);
        }
        else
        {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
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
        Log.i(TAG, "Received error: " + errorId);
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
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    /**
     * Register this account/device pair within the server.
     * 
     * @return whether the registration succeeded or not.
     */
    protected void register(final Context context, final String regId, int attempt, long backoff)
    {
        Log.i(TAG, "registering device (regId = " + regId + ")");

        FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(context, FoodCenterRequestFactory.class);
        factory.gcmService().register(regId).fire(new GCMRegisterReciever(context, this, regId, attempt, backoff));

    }
    
    private void unregister(final Context context, final String regId)
    {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(context, FoodCenterRequestFactory.class);
        factory.gcmService().unregister(regId).fire(new GCMUnRegisterReciever(context));
    }
}

class GCMRegisterReciever extends Receiver<Void>
{
    
    private final Context context;
    private final GCMIntentService service;
    private final String regId;
    private final int attempt;
    private final long backoff;

    public GCMRegisterReciever(final Context context, GCMIntentService service, String regId, int attempt, long backoff)
    {
        this.context = context;
        this.service = service;
        this.regId = regId;
        this.attempt = attempt;
        this.backoff = backoff;
    }

    @Override
    public void onSuccess(Void arg0)
    {
        GCMRegistrar.setRegisteredOnServer(context, true);
        String message = context.getString(R.string.server_registered);
        CommonUtilities.displayMessage(context, message);
    }

    @Override
    public void onFailure(ServerFailure error)
    {
        try
        {
            if (attempt > 0)
            {
                Log.d(GCMIntentService.TAG, "Sleeping for " + backoff + " ms before retry");
                Thread.sleep(backoff);
                service.register(context, regId, attempt - 1, backoff * 2);
                return;
            }
            String message = context.getString(R.string.server_register_error, GCMIntentService.MAX_ATTEMPTS);
            CommonUtilities.displayMessage(context, message);
        }
        catch (InterruptedException e)
        {
            // Activity finished before we complete - exit.
            Log.d(GCMIntentService.TAG, "Thread interrupted: abort remaining retries!");
            Thread.currentThread().interrupt();
            return;
        }
    }
}

class GCMUnRegisterReciever extends Receiver<Void>
{
    
    private final Context context;

    public GCMUnRegisterReciever(final Context context)
    {
        this.context = context;
    }

    @Override
    public void onSuccess(Void arg0)
    {
        GCMRegistrar.setRegisteredOnServer(context, false);
        String message = context.getString(R.string.server_unregistered);
        CommonUtilities.displayMessage(context, message);
    }

    @Override
    public void onFailure(ServerFailure error)
    {
        // At this point the device is unregistered from GCM, but still
        // registered in the server.
        // We could try to unregister again, but it is not necessary:
        // if the server tries to send a message to the device, it will get
        // a "NotRegistered" error message and should unregister the device.
        String message = context.getString(R.string.server_unregister_error, error.getMessage());
        CommonUtilities.displayMessage(context, message);
    }
}
