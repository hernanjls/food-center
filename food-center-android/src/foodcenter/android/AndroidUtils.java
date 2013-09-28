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

import android.content.Context;
import android.content.Intent;
/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class AndroidUtils
{

    /** Intent used to display a message in the screen. */
    public static final String ACTION_SHOW_TOAST = "foodcenter.android.SHOW_TOAST";

    /** Intent used to display a message in the screen. */
    public static final String ACTION_SHOW_PROGRESS = "foodcenter.android.SHOW_PROGRESS";

    /** Intent used notify sign-in. */
    public static final String ACTION_SIGNED_IN = "foodcenter.android.SIGNED_IN";
    
    /** Intent used notify sign-out. */
    public static final String ACTION_SIGNED_OUT = "foodcenter.android.SIGNED_OUT";
    
    public static final String EXTRA_MESSAGE = "extraMessage";

    
    public static void notifySignOut(Context context, String msg)
    {
        Intent intent = new Intent(ACTION_SIGNED_OUT);
        intent.putExtra(EXTRA_MESSAGE, msg);
        context.sendBroadcast(intent);
    }
    
    public static void notifySignIn(Context context, String msg)
    {
        Intent intent = new Intent(ACTION_SIGNED_IN);
        intent.putExtra(EXTRA_MESSAGE, msg);
        context.sendBroadcast(intent);
    }
        
    /**
     * Notifies UI to display / hide a progress message. <br>
     * It's used both by the UI and the background services and tasks.     
     *  
     * @param context application's context.
     * @param message message to be displayed. null message to hide
     */
    public static void progress(Context context, String msg)
    {
        Intent intent = new Intent(ACTION_SHOW_PROGRESS);
        intent.putExtra(EXTRA_MESSAGE, msg);
        context.sendBroadcast(intent);
    }

    /**
     * Notifies UI to hide progress and show toast. <br>
     * It's used both by the UI and the background services and tasks.
     *      
     * @param context
     * @param msg
     */
    public static void progressDismissAndToastMsg(Context context, String msg)
    {
        progress(context, null);
        toast(context, msg);
    }

    /**
     * Notifies UI to display a toast message.<br>
     * It's used both by the UI and the background services and tasks.
     * 
     * @param context application's context.
     * @param msg message to be displayed.
     */
    public static void toast(Context context, String msg)
    {
        if (null == msg)
        {
            return;
        }
        Intent intent = new Intent(ACTION_SHOW_TOAST);
        intent.putExtra(EXTRA_MESSAGE, msg);
        context.sendBroadcast(intent);
    }
    
}
