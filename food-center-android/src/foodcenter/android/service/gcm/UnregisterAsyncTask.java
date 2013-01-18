package foodcenter.android.service.gcm;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.LoginActivity;
import foodcenter.android.service.RequestUtils;

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
		publishProgress("Removing know user...");
		Editor editor = RequestUtils.getSharedPreferences(context).edit();
		editor.putString(RequestUtils.ACCOUNT_NAME, null).commit();

		publishProgress("Unregistering from GCM service and Server...");
		Log.i(TAG, "[START] Unregistering from GCM...");
		GCMRegistrar.unregister(context);
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result)
	{
		Log.i(TAG, "[DONE] Unregistering from GCM");
	    context.finish();
	}

	@Override
	protected void onProgressUpdate(String... values)
	{
	    String msg = values[0];
	    context.showSpinner(msg);
	}
}
