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
import android.content.Context;
import android.content.Intent;
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
            
}
