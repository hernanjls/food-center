package foodcenter.android.service.login;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.LoginActivity;

public class UnregisterAsyncTask extends AsyncTask<Void, String, Boolean>
{

	/** for logs */
	private static final String TAG = UnregisterAsyncTask.class.getSimpleName();

	private final LoginActivity context;	
	
	public UnregisterAsyncTask(LoginActivity context)
	{
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		publishProgress("Unregistering from GCM service and Server...");
		Log.i(TAG, "[START] Unregistering from GCM...");
		GCMRegistrar.unregister(context.getApplicationContext());
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result)
	{
		Log.i(TAG, "[DONE] Unregistering from GCM");
		// GCMIntentService onUnregister deals with activity
	}

	@Override
	protected void onProgressUpdate(String... values)
	{
	    String msg = values[0];
	    LoginActivity.showSpinner(msg);
	}
}
