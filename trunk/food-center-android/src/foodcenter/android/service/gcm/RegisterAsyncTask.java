package foodcenter.android.service.gcm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.GCMIntentService;
import foodcenter.android.LoginActivity;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;

public class RegisterAsyncTask extends AsyncTask<String, String, Boolean>
{

	/** Cookie name for authorization. */
	public static final String AUTH_COOKIE_NAME = "SACSID";

	/** for logs */
	private static final String TAG = RegisterAsyncTask.class.getSimpleName();

	private final LoginActivity context;	
	
	public RegisterAsyncTask(LoginActivity context)
	{
		this.context = context;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		context.showSpinner("Registering ...");
	}

	/**
	 * Registers for GCM messaging with the given account name, <br>
	 * stores in {@link RequestUtils#getSharedPreferences(Context)}: <br>
	 * - {@link RequestUtils#ACCOUNT_NAME} <br>
	 * - {@link RequestUtils#AUTH_COOKIE}
	 * 
	 * @param accountName a String containing a Google account name
	 * 
	 * @return true if registration success, false othewise
	 */
	@Override
	protected Boolean doInBackground(String... params)
	{
		try
		{	
			String accountName = params[0];
			// Store the account name in shared preferences and clear auth cookie
			final SharedPreferences prefs = RequestUtils.getSharedPreferences(context);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(RequestUtils.ACCOUNT_NAME, accountName);
			editor.putString(RequestUtils.AUTH_COOKIE, null);
			editor.commit();

			// Obtain an auth token and save it as a cookie
			publishProgress("Getting authentication token...");
			String authToken = getAuthToken(accountName);
			
			publishProgress("Getting authentication cookie...");
			String authCookie = getAuthCookie(authToken);
			
			editor.putString(RequestUtils.AUTH_COOKIE, authCookie);
			editor.putString(RequestUtils.ACCOUNT_NAME, accountName);
			editor.commit();

			publishProgress("Registering on server...");
			// register to GCM (you are authenticated)
			if (!GCMRegistrar.isRegistered(context))
			{
				GCMRegistrar.register(context, GCMIntentService.GCM_SENDER_ID);
			}
			else
			{
				Log.i(TAG, "Already registered");
			}
			return true;
		}
		catch (Exception e)
		{
			// OperationCanceledException, AuthenticatorException, IOException,
			// URISyntaxException
			Log.e(TAG, e.getClass().getSimpleName(), e);
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		if (true == result)
		{
			Log.i(TAG, "[DONE] registering");
		}
		else
		{
			Log.e(TAG, "[EROR] registering fail");
		}
		context.finish();
	}

	/**
	 * updates the spinner of the current process
	 */
	@Override
	protected void onProgressUpdate(String... values)
	{
		String msg = values[0];
		context.showSpinner(msg);
	}
	/**
	 * this is blocking!!!
	 * 
	 * @param accountName is the account name to get auth token for.
	 * 
	 * @return auth token which can be used to create auth cookie. (will never return null).
	 * 
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	private String getAuthToken(String accountName) //
	    throws OperationCanceledException, AuthenticatorException, IOException
	{
		AccountManager mgr = AccountManager.get(context);
		Account acct = getAccountByName(mgr, "com.google", accountName);
		if (acct == null)
		{
			return null;
		}
		String authToken = mgr.blockingGetAuthToken(acct, "ah", false);
		if (null == authToken)
		{
			throw new AuthenticatorException("got null authToken...");
		}
		return authToken;
	}

	/**
	 * return the account which match type and has name
	 * 
	 * @param mgr
	 * @param type as passed to {@link AccountManager#getAccountsByType(String)}
	 * @param accountName
	 * @return
	 */
	private Account getAccountByName(AccountManager mgr, String type, String accountName)
	{
		Account[] accts = mgr.getAccountsByType(type);
		for (Account acct : accts)
		{
			if (acct.name.equals(accountName))
			{
				return acct;
			}
		}
		return null;
	}

	/**
	 * Retrieves the authorization cookie associated with the given token.
	 * This method should only be used when running against a production
	 * appengine backend (as opposed to a dev mode server).
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private String getAuthCookie(String authToken) //
	    throws URISyntaxException, ClientProtocolException, IOException
	{
		// Get SACSID / ACSID cookie
		DefaultHttpClient client = new DefaultHttpClient();
		String continueURL = Setup.PROD_URL;
		URI uri = new URI(Setup.PROD_URL + "/_ah/login?continue=" + URLEncoder.encode(continueURL, "UTF-8") + "&auth=" + authToken);
		HttpGet method = new HttpGet(uri);
		final HttpParams getParams = new BasicHttpParams();
		HttpClientParams.setRedirecting(getParams, false);
		method.setParams(getParams);

		HttpResponse res = client.execute(method);
		Header[] headers = res.getHeaders("Set-Cookie");
		if (res.getStatusLine().getStatusCode() != 302 || headers.length == 0)
		{
			return null;
		}

		for (Cookie cookie : client.getCookieStore().getCookies())
		{
			if (AUTH_COOKIE_NAME.equals(cookie.getName()))
			{
				return AUTH_COOKIE_NAME + "=" + cookie.getValue();
			}
		}
		return null;
	}

}
