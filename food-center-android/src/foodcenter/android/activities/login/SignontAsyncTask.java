package foodcenter.android.activities.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.activities.MsgBroadcastReceiver;

/**
 * Unregister from GCM and sign-out from server
 * 
 * @author stream2
 * 
 */
public class SignontAsyncTask extends AsyncTask<Void, String, Void>
{

    /** for logs */
    private static final String TAG = SignontAsyncTask.class.getSimpleName();

    private final Context context;

    public SignontAsyncTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        String msg = "Unregistering from GCM ..."; // TODO change to R.string
        MsgBroadcastReceiver.progress(context, msg);
        Log.i(TAG, msg);

        GCMRegistrar.unregister(context.getApplicationContext());
        return null;
    }
}
