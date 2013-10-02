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

import foodcenter.android.activities.MsgBroadcastReceiver;
import foodcenter.android.activities.main.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class AndroidUtils
{
    /** Intent used notify sign-in. */
    public static final String ACTION_SIGNED_IN = "foodcenter.android.SIGNED_IN";

    /** Intent used notify sign-out. */
    public static final String ACTION_SIGNED_OUT = "foodcenter.android.SIGNED_OUT";

    public static final String EXTRA_SIGN_MESSAGE = "extraMessage";

    public static void notifySignOut(Context context, String msg)
    {
        MsgBroadcastReceiver.progressDismissAndToastMsg(context, msg);

        Intent intent = new Intent(ACTION_SIGNED_OUT);
        intent.putExtra(EXTRA_SIGN_MESSAGE, msg);
        context.sendBroadcast(intent);
    }

    public static void notifySignIn(Context context, String msg)
    {
        MsgBroadcastReceiver.progressDismissAndToastMsg(context, msg);

        Intent intent = new Intent(ACTION_SIGNED_IN);
        context.sendBroadcast(intent);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    public static void generateNotification(Context context, String message)
    {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_food_center)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message);

        // Big notification
        String[] events = message.split("\n");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        
        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(events[0]);

        // Moves events into the big view
        for (int i = 1; i < events.length; i++)
        {
            inboxStyle.addLine(events[i]);
        }
        // Moves the big view style object into the notification object.
        builder.setStyle(inboxStyle);

        
        
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
