package foodcenter.android.service.login;

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
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.GCMIntentService;
import foodcenter.android.activities.main.LoginActivity;
import foodcenter.android.service.RequestUtils;

public class AuthenticateAndLoginAsyncTask extends AsyncTask<String, String, Boolean>
{

    /** Cookie name for authorization. */
    public static final String AUTH_COOKIE_NAME = "SACSID";

    /** for logs */
    private static final String TAG = AuthenticateAndLoginAsyncTask.class.getSimpleName();

    private final Context appContext;
    private final LoginActivity loginActivity;

    public AuthenticateAndLoginAsyncTask(final LoginActivity loginActivity)
    {
        this.loginActivity = loginActivity;
        this.appContext = loginActivity.getApplicationContext();
    }

    @Override
    protected void onPreExecute()
    {
        // This task is created by login activity, so we can call this function
        LoginActivity.showSpinner("Registering ...");
        // super.onPreExecute();
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
            final SharedPreferences prefs = RequestUtils.getSharedPreferences(appContext);
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
            final String regId = GCMRegistrar.getRegistrationId(appContext);
            if (regId.equals(""))
            {
                // Device is not registered
                GCMRegistrar.register(appContext, GCMIntentService.GCM_SENDER_ID);
            }
            else if (!GCMRegistrar.isRegisteredOnServer(appContext))
            {
                // Device is already registered on GCM but not logged in on server
                new ServerLoginAsyncTask(appContext, regId, 5).execute();
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
            Log.i(TAG, "OK registering, waiting for server login to finish");
            // context.finish(); is called from ServerLoginAsyncTask
        }
        else
        {
            Log.e(TAG, "[EROR] exception was caught");
        }
    }

    /**
     * updates the spinner of the current process
     */
    @Override
    protected void onProgressUpdate(String... values)
    {
        String msg = values[0];
        LoginActivity.showSpinner(msg);
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
    private String getAuthToken(String accountName) throws OperationCanceledException,
                                                   AuthenticatorException,
                                                   IOException
    {

        AccountManager mgr = AccountManager.get(appContext);
        Account acct = getAccountByName(mgr, "com.google", accountName);
        // String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/analytics.readonly";
        String AUTH_TOKEN_TYPE = "ah";
        if (acct == null)
        {
            throw new AuthenticatorException("account == null");
        }
        AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct,
                                                                             AUTH_TOKEN_TYPE,
                                                                             null,
                                                                             loginActivity,
                                                                             null,
                                                                             null);
        Bundle authTokenBundle = accountManagerFuture.getResult();
        String authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
        // String authToken = mgr.blockingGetAuthToken(acct, AUTH_TOKEN_TYPE, false);
        // mgr.invalidateAuthToken("com.google", authToken);
        if (null == authToken)
        {
            throw new AuthenticatorException("authToken == null");
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
    private String getAuthCookie(String authToken) throws URISyntaxException,
                                                  ClientProtocolException,
                                                  IOException
    {
        // Get SACSID / ACSID cookie
        DefaultHttpClient client = new DefaultHttpClient();
        String continueURL = RequestUtils.getBaseUrl(appContext);
        URI uri = new URI(continueURL + "/_ah/login?continue="
                          + URLEncoder.encode(continueURL, "UTF-8")
                          + "&auth="
                          + authToken);
        HttpGet method = new HttpGet(uri);
        final HttpParams getParams = new BasicHttpParams();
        HttpClientParams.setRedirecting(getParams, false);
        method.setParams(getParams);

        HttpResponse res = client.execute(method);
        Header[] headers = res.getHeaders("Set-Cookie");
        if (res.getStatusLine().getStatusCode() != 302 || headers.length == 0)
        {
            throw new IOException("status= " + res.getStatusLine().getStatusCode()
                                  + " headers length = "
                                  + headers.length);
        }

        for (Cookie cookie : client.getCookieStore().getCookies())
        {
            if (AUTH_COOKIE_NAME.equals(cookie.getName()))
            {
                return AUTH_COOKIE_NAME + "=" + cookie.getValue();
            }
        }
        throw new IOException("AUTH_COOKIE_NAME is missing from the response");
    }

}
