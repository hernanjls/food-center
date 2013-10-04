package foodcenter.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;
import foodcenter.android.AndroidUtils;

/**
 * {@link BroadcastReceiver} for handleing messages 
 */
public class MsgBroadcastReceiver extends BroadcastReceiver
{

    /** Intent used to display a message in the screen. */
    public static final String ACTION_SHOW_TOAST = "foodcenter.android.SHOW_TOAST";

    /** Intent used to display a message in the screen. */
    public static final String ACTION_SHOW_PROGRESS = "foodcenter.android.SHOW_PROGRESS";
    
    /** tag for the message to put/ display */
    private static final String EXTRA_MESSAGE = "extraMessage";

    /** progress dialog to show progress on */
    private ProgressDialog progress;
    
    /**
     * Creates a new MsgReceiver.
     * @param progress - progress dialog to show progress.
     */
    public MsgBroadcastReceiver(ProgressDialog progress)
    {
        super();
        this.progress = progress;
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
     * It's used both by the UI and the background services and tasks.<br>
     * Notifies all registered MsgBroadcastHandlers
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

    /**
     * Register this receiver to the activity.
     * @param activity
     */
    public void registerMe(Activity activity)
    {
        activity.registerReceiver(this, new IntentFilter(ACTION_SHOW_PROGRESS));
        activity.registerReceiver(this, new IntentFilter(ACTION_SHOW_TOAST));

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (ACTION_SHOW_PROGRESS.equals(action))
        {
            String msg = intent.getStringExtra(AndroidUtils.EXTRA_SIGN_MESSAGE);
            
            if (null != msg)
            {
                progress.setMessage(msg);
                if (!progress.isShowing())
                {
                    progress.show();
                }
            }
            else
            {
                if (progress.isShowing())
                {
                    progress.dismiss();
                }
            }
        }
        
        else if (ACTION_SHOW_TOAST.equals(action))
        {
            String msg = intent.getStringExtra(AndroidUtils.EXTRA_SIGN_MESSAGE);
            if (null != msg)
            {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
            return;
        }
        
    }


}
