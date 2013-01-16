package foodcenter.android;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;

public class LoginActivity extends Activity
{

    /**
     * Tag for logging.
     */
    private static final String TAG = "AccountsActivity";

    /**
     * Cookie name for authorization.
     */
     private static final String AUTH_COOKIE_NAME = "SACSID";

    /**
     * The current context.
     */
    private Context mContext = this;

    /**
     * The selected position in the ListView of accounts.
     */
    private int mAccountSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = RequestUtils.getSharedPreferences(mContext);
        boolean isConnected = prefs.getBoolean(RequestUtils.IS_CONNECTED, false);
        int screenId = isConnected ? R.layout.disconnect : R.layout.connect;
        setScreenContent(screenId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        SharedPreferences prefs = RequestUtils.getSharedPreferences(mContext);
        boolean isConnected = prefs.getBoolean(RequestUtils.IS_CONNECTED, false);
        int screenId = isConnected ? R.layout.disconnect : R.layout.connect;
        setScreenContent(screenId);

        return true;
    }

    /**
     * Sets the screen content based on the screen id. <br>
     * {@link R.layout.disconnect} or {@link R.layout.connect}
     */
    private void setScreenContent(int screenId)
    {
        setContentView(screenId);
        switch (screenId)
        {
        case R.layout.disconnect:
            setDisconnectScreenContent();
            break;
        case R.layout.connect:
            setConnectScreenContent();
            break;
        }
    }

    /**
     * Sets up the 'connect' screen content.
     */
    private void setConnectScreenContent()
    {

        List<String> accounts = getGoogleAccounts();
        if (accounts.size() == 0)
        {
            // Show a dialog and invoke the "Add Account" activity if requested
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.needs_account);
            builder.setPositiveButton(R.string.add_account, new AddAcountClickListener());
            builder.setNegativeButton(R.string.skip, new NegativeButtonClickListener());
            builder.setIcon(android.R.drawable.stat_sys_warning);
            builder.setTitle(R.string.attention);
            builder.show();
        }
        else
        {
            final ListView listView = (ListView) findViewById(R.id.select_account);
            listView.setAdapter(new ArrayAdapter<String>(mContext, R.layout.account, accounts));
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setItemChecked(mAccountSelectedPosition, true);

            final Button connectButton = (Button) findViewById(R.id.connect);
            connectButton.setOnClickListener(new ConnectButtonClickListener(listView));

            final Button exitButton = (Button) findViewById(R.id.exit);
            exitButton.setOnClickListener(new ExitButtonClickListener());
        }
    }

    /**
     * Sets up the 'disconnected' screen.
     */
    private void setDisconnectScreenContent()
    {
        final SharedPreferences prefs = RequestUtils.getSharedPreferences(mContext);
        String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME, "error");

        // Format the disconnect message with the currently connected account
        // name
        TextView disconnectText = (TextView) findViewById(R.id.disconnect_text);
        String message = getResources().getString(R.string.disconnect_text);
        String formatted = String.format(message, accountName);
        disconnectText.setText(formatted);

        Button disconnectButton = (Button) findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(new DisconnectButtonClickListener());

        Button exitButton = (Button) findViewById(R.id.exit);
        exitButton.setOnClickListener(new ExitButtonClickListener());
    }

    /**
     * Registers for GCM messaging with the given account name, <br>
     * stores in {@link RequestUtils#getSharedPreferences(Context)}: <br>
     * - {@link RequestUtils#ACCOUNT_NAME} <br>
     * - {@link RequestUtils#AUTH_COOKIE} 
     * 
     * @param accountName a String containing a Google account name
     */
    private void register(final String accountName)
    {
        // Store the account name in shared preferences and clear auth cookie
        final SharedPreferences prefs = RequestUtils.getSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RequestUtils.ACCOUNT_NAME, accountName);
        editor.putString(RequestUtils.AUTH_COOKIE, null);
        editor.commit();

        // Obtain an auth token and register (save the new cookie)
        AccountManager mgr = AccountManager.get(mContext);
        Account[] accts = mgr.getAccountsByType("com.google");
        for (Account acct : accts)
        {
            if (acct.name.equals(accountName))
            {
                mgr.getAuthToken(acct, "ah", null, this, new OnTokenAquieredCallback(), null);
            }
            break;
        }
    }

    // Utility Methods

    /**
     * Retrieves the authorization cookie associated with the given token. This
     * method should only be used when running against a production appengine
     * backend (as opposed to a dev mode server).
     */
    private String getAuthCookie(String authToken)
    {
        try
        {
            // Get SACSID / ACSID cookie 
            DefaultHttpClient client = new DefaultHttpClient();
            String continueURL = Setup.PROD_URL;
            URI uri = new URI(Setup.PROD_URL + "/_ah/login?continue=" + URLEncoder.encode(continueURL, "UTF-8") + "&auth=" + authToken);
//            String uriStr = "https://accounts.google.com/ServiceLogin?service=ah&passive=true&continue=https://appengine.google.com/_ah/conflogin%3Fcontinue%3Dhttp://food-center.appspot.com/&ltmpl=gm&shdf=" + authToken;
//            URI uri = new URI(uriStr);
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
        }
        catch (IOException e)
        {
            Log.w(TAG, "Got IOException " + e);
            Log.w(TAG, Log.getStackTraceString(e));
        }
        catch (URISyntaxException e)
        {
            Log.w(TAG, "Got URISyntaxException " + e);
            Log.w(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    /**
     * Returns a list of registered Google account names. If no Google accounts
     * are registered on the device, a zero-length list is returned.
     */
    private List<String> getGoogleAccounts()
    {
        ArrayList<String> result = new ArrayList<String>();
        Account[] accounts = AccountManager.get(mContext).getAccounts();
        for (Account account : accounts)
        {
            if (account.type.equals("com.google"))
            {
                result.add(account.name);
            }
        }

        return result;
    }

    class AddAcountClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            startActivity(new Intent(Settings.ACTION_ADD_ACCOUNT));
        }

    }

    class NegativeButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            finish();
        }

    }

    class ConnectButtonClickListener implements OnClickListener
    {
        private final ListView listView;

        public ConnectButtonClickListener(ListView listView)
        {
            this.listView = listView;
        }

        @Override
        public void onClick(View v)
        {
            // Register in the background and terminate the activity
            mAccountSelectedPosition = listView.getCheckedItemPosition();
            TextView account = (TextView) listView.getChildAt(mAccountSelectedPosition);
            register(account.getText().toString());
            finish();
        }

    }

    class DisconnectButtonClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            Editor edit = RequestUtils.getSharedPreferences(mContext).edit();
            edit.putBoolean(RequestUtils.IS_CONNECTED, false).commit();

            // Unregister in the background and terminate the activity
            GCMRegistrar.unregister(mContext);
            finish();
        }

    }

    class ExitButtonClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            finish();
        }
    }

    /**
     * on success saves the cookie on prefs {@link RequestUtils.AUTH_COOKIE}
     * and register to GCM service.
     */
    class OnTokenAquieredCallback implements AccountManagerCallback<Bundle>
    {
        @Override
        public void run(AccountManagerFuture<Bundle> future)
        {
            try
            {
                // save auth cookie for the AndroidRequestTransport
                Bundle authTokenBundle = future.getResult();
                String authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
                new GetAuthCookieAsyncTask().execute(new String[]{authToken});
                
            }
            catch (Exception e)
            {
                // AuthenticatorException, IOException, OperationCanceledException
                Log.w(TAG, "Got AuthenticatorException " + e);
                Log.w(TAG, Log.getStackTraceString(e));
            }
        }
    }
    
    class GetAuthCookieAsyncTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params)
        {
            String authToken = params[0];
            String authCookie = getAuthCookie(authToken);
            SharedPreferences prefs = RequestUtils.getSharedPreferences(mContext);
            prefs.edit().putString(RequestUtils.AUTH_COOKIE, authCookie).commit();
            
            // register to GCM (you are authenticated)
            GCMRegistrar.register(mContext, Setup.SENDER_ID);
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Void... values)
        {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }
        
        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
        
        
        
    }
}
