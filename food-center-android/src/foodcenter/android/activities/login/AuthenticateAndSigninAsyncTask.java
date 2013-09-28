package foodcenter.android.activities.login;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.AndroidUtils;
import foodcenter.android.GCMIntentService;
import foodcenter.android.service.AndroidRequestUtils;

public class AuthenticateAndSigninAsyncTask extends AsyncTask<Void, String, Void>
{

    /** for logs */
    private static final String TAG = AuthenticateAndSigninAsyncTask.class.getSimpleName();

    private final Activity activity;

    private final Context appContext;

    public AuthenticateAndSigninAsyncTask(Activity activity)
    {
        super();
        this.activity = activity;

        appContext = activity.getApplicationContext();
    }

    /**
     * Registers for GCM messaging with the given account name, <br>
     * stores in {@link AndroidRequestUtils#getSharedPreferences(Context)}: <br>
     * - {@link AndroidRequestUtils#PREF_ACCOUNT_NAME} <br>
     * - {@link AndroidRequestUtils#AUTH_COOKIE}
     * 
     * @param accountName a String containing a Google account name
     *            on null input, will use already existing account name
     * 
     * @return true if registration success, false othewise
     */
    @Override
    protected Void doInBackground(Void... params)
    {
        AndroidUtils.progress(activity, "Starting Register process..."); // change to R.strings
        try
        {
            final SharedPreferences prefs = AndroidRequestUtils.getSharedPreferences(appContext);
            SharedPreferences.Editor editor = prefs.edit();

            // in-case we want to re-authenticate
            String accountName = prefs.getString(AndroidRequestUtils.PREF_ACCOUNT_NAME, null);
            if (null == accountName)
            {
                String msg = "Can't get account name"; // TODO change to R.string
                Log.e(TAG, msg);
                AndroidUtils.progressDismissAndToastMsg(activity, msg);
            }

            editor.putString(AndroidRequestUtils.AUTH_COOKIE, null);
            editor.commit();

            // Obtain an auth token and save it as a cookie
            // TODO change to R.string
            AndroidUtils.progress(activity, "Generating authentication TOKEN...");
            String authToken = getAuthToken(accountName);

            // TODO change to R.string
            AndroidUtils.progress(activity, "Getting authentication COOKIE...");
            String authCookie = getAuthCookie(authToken);
            editor.putString(AndroidRequestUtils.AUTH_COOKIE, authCookie);
            editor.putString(AndroidRequestUtils.PREF_ACCOUNT_NAME, accountName);
            editor.commit();

            // register to GCM (you are authenticated)
            final String regId = GCMRegistrar.getRegistrationId(appContext);
            if (regId.equals(""))
            {
                // Device is not registered on GCM (sign-in is called onRegister callback)
                GCMRegistrar.register(appContext, GCMIntentService.GCM_SENDER_ID);
            }
            else if (!GCMRegistrar.isRegisteredOnServer(appContext))
            {
                // Device is already registered on GCM but not signed-in on server
                new ServerSigninTask(appContext, regId, 5).signIn();
            }
        }
        catch (Exception e)
        {
            // OperationCanceledException, AuthenticatorException, IOException,
            // URISyntaxException
            String msg = "[ERROR] " + e.getMessage();
            Log.e(TAG, msg, e);
            AndroidUtils.progressDismissAndToastMsg(activity, msg);
        }
        return null;
    }

    /**
     * this is blocking!!!
     * 
     * @param accountName is the account name to get auth token for.
     * 
     * @return auth token which can be used to create auth cookie. (will never return null).
     * 
     * @throws IOException when auth cookie is not found
     * @throws AccountsException when account is not found
     */
    private String getAuthToken(String accountName) throws IOException, AccountsException
    {

        AccountManager mgr = AccountManager.get(appContext);
        Account acct = getAccountByName(mgr, "com.google", accountName);
        String AUTH_TOKEN_TYPE = "ah";
        if (acct == null)
        {
            throw new AuthenticatorException("account == null");
        }
        AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct,
                                                                             AUTH_TOKEN_TYPE,
                                                                             null,
                                                                             activity,
                                                                             null,
                                                                             null);
        Bundle authTokenBundle = accountManagerFuture.getResult();
        String authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
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
    private Account
        getAccountByName(AccountManager mgr, String type, String accountName) throws AccountsException
    {
        Account[] accts = mgr.getAccountsByType(type);
        for (Account acct : accts)
        {
            if (acct.name.equals(accountName))
            {
                return acct;
            }
        }

        throw new AccountsException("Can't find account");
    }

    /**
     * Retrieves the authorization cookie associated with the given token.
     * This method should only be used when running against a production
     * appengine backend (as opposed to a dev mode server).
     * 
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     * 
     */
    private String getAuthCookie(String authToken) throws URISyntaxException,
                                                  ClientProtocolException,
                                                  IOException
    {
        // Get SACSID / ACSID cookie
        DefaultHttpClient client = new DefaultHttpClient();
        String continueURL = AndroidRequestUtils.getBaseUrl();
        URI uri = new URI(continueURL + "/_ah/login?continue="
                          + URLEncoder.encode(continueURL, "UTF-8")
                          + "&auth="
                          + authToken);

        HttpPost httpPost = new HttpPost(uri);
        if (AndroidRequestUtils.isDev())
        {
            Log.d(TAG, "Auth cookie for dev server ...");
            final SharedPreferences prefs = AndroidRequestUtils.getSharedPreferences(appContext);
            String accName = prefs.getString(AndroidRequestUtils.PREF_ACCOUNT_NAME,
                                             "myemail@gmail.com");

            // attach
            List<BasicNameValuePair> devParams = new ArrayList<BasicNameValuePair>();
            devParams.add(new BasicNameValuePair("email", accName));
            devParams.add(new BasicNameValuePair("admin", "False"));
            devParams.add(new BasicNameValuePair("action", "Login"));
            HttpEntity entity = new UrlEncodedFormEntity(devParams);
            httpPost.setEntity(entity);
        }

        final HttpParams params = new BasicHttpParams();
        HttpClientParams.setRedirecting(params, false);
        httpPost.setParams(params);
        
        HttpResponse res = client.execute(httpPost);
        Header[] headers = res.getHeaders("Set-Cookie");
        if (res.getStatusLine().getStatusCode() != 302 || headers.length == 0)
        {
            throw new IOException("status= " + res.getStatusLine().getStatusCode()
                                  + " headers length = "
                                  + headers.length);
        }

        final String authCookieName = AndroidRequestUtils.getAuthCookieName();
        for (Cookie cookie : client.getCookieStore().getCookies())
        {
            if (authCookieName.equals(cookie.getName()))
            {
                return authCookieName + "=" + cookie.getValue();
            }
        }
        throw new IOException("AUTH_COOKIE_NAME is missing from the response");
    }
}
