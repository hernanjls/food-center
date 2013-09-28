package foodcenter.android.activities.login;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.R;
import foodcenter.android.service.AndroidRequestUtils;

public class LoginDialogFragment extends DialogFragment
{

    /*
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface LoginDialogListener
    {
        public void onSignInClick(DialogFragment dialog);

        public void onCancelClick(DialogFragment dialog);

        public void onSignOutClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private LoginDialogListener listener;
    private boolean isLogedIn;

    private ListView serversLv;
    private ListView accountsLv;

    private List<String> servers;
    private int selectedServer;

    private List<String> accounts;
    private int selectedAccount;

    // Override the Fragment.onAttach() method to instantiate the LoginDialogListener
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (LoginDialogListener) activity;
            isLogedIn = GCMRegistrar.isRegisteredOnServer(activity.getApplicationContext());
        }
        catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        accounts = getGoogleAccounts();
        if (accounts.isEmpty())
        {
            accounts.add("You can't signin without google account!");
        }
        servers = getAvailableServers();

        View view = inflater.inflate(R.layout.signin, null);
        serversLv = (ListView) view.findViewById(R.id.login_server_list);
        serversLv.setAdapter(new ArrayAdapter<String>(getActivity(),
                                                      R.layout.signin_lv_item,
                                                      servers)
        {
            @Override
            public boolean isEnabled(int position)
            {
                return !isLogedIn;
            }
        });
        serversLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        accountsLv = (ListView) view.findViewById(R.id.login_accounts_list);
        accountsLv.setAdapter(new ArrayAdapter<String>(getActivity(),
                                                       R.layout.signin_lv_item,
                                                       accounts)
        {
            @Override
            public boolean isEnabled(int position)
            {
                return !isLogedIn;
            }
        });
        accountsLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        builder.setView(view);
        
        if (isLogedIn)
        {
            isSignedInView(builder);
        }
        else
        {
            isSignedOutView(builder);
        }

        builder.setNegativeButton(R.string.signin_cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                listener.onCancelClick(LoginDialogFragment.this);
            }
        });

        // Create the AlertDialog object and return it
        Dialog res = builder.create();
        res.setCanceledOnTouchOutside(false);
        return res;
    }

    private void isSignedInView(AlertDialog.Builder builder)
    {
        int sPos = servers.indexOf(AndroidRequestUtils.getBaseUrl());
        serversLv.setItemChecked(sPos, true);

        SharedPreferences p = AndroidRequestUtils.getSharedPreferences(getActivity());
        String account = p.getString(AndroidRequestUtils.PREF_ACCOUNT_NAME, null);

        int aPos = accounts.indexOf(account);
        accountsLv.setItemChecked(aPos, true);

        builder.setTitle("Signed In!");

        builder.setPositiveButton(R.string.signin_signout, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                listener.onSignOutClick(LoginDialogFragment.this);
            }
        });

    }

    private void isSignedOutView(AlertDialog.Builder builder)
    {
        serversLv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                selectedServer = position;
            }
        });
        serversLv.setItemChecked(0, true);

        accountsLv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
            {
                selectedAccount = position;
            }
        });
        accountsLv.setItemChecked(0, true);
        
        builder.setTitle("Select Server and Account");

        builder.setPositiveButton(R.string.signin_signin, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                SharedPreferences p = AndroidRequestUtils.getSharedPreferences(getActivity());
                Editor editor = p.edit();
                editor.putString(AndroidRequestUtils.PREF_SERVER_URL, servers.get(selectedServer));
                editor.putString(AndroidRequestUtils.PREF_ACCOUNT_NAME,
                                 accounts.get(selectedAccount));
                editor.commit();

                AndroidRequestUtils.setUpUrl(LoginDialogFragment.this.getActivity());

                listener.onSignInClick(LoginDialogFragment.this);
            }
        });

    }

    private List<String> getAvailableServers()
    {
        ArrayList<String> result = new ArrayList<String>();
        result.add(AndroidRequestUtils.PROD_URL);
        result.add(AndroidRequestUtils.DEV_URL);
        return result;
    }

    private List<String> getGoogleAccounts()
    {
        ArrayList<String> result = new ArrayList<String>();
        Account[] accounts = AccountManager.get(getActivity().getApplicationContext())
            .getAccounts();
        for (Account account : accounts)
        {
            if (account.type.equals("com.google"))
            {
                result.add(account.name);
            }
        }

        return result;
    }
}

// class LoginActivity2 extends Activity implements SpinableActivity
// {
//
// /**
// * Tag for logging.
// */
// private static final String TAG = LoginActivity2.class.getSimpleName();
//
// /**
// * The selected position in the ListView of accounts.
// */
// private int mAccountSelectedPosition = 0;
//
// private ProgressDialog spin;
//
// public void closeLoginActivity(boolean isSuccess)
// {
// Intent intent = new Intent();
// int resCode = isSuccess ? RESULT_OK : RESULT_CANCELED;
// setResult(resCode, intent);
// finish();
// }
//
// @Override
// public void showSpinner()
// {
// if (null != spin)
// {
// spin.show();
// }
//
// }
//
// public void showSpinner(String msg)
// {
// spin.setMessage(msg);
// showSpinner();
// }
//
// @Override
// public void hideSpinner()
// {
// if (spin.isShowing())
// {
// spin.dismiss();
// }
// }
//
// @Override
// public Activity getActivity()
// {
// // TODO Auto-generated method stub
// return null;
// }
//
// @Override
// protected void onCreate(Bundle savedInstanceState)
// {
// super.onCreate(savedInstanceState);
//
// LoginActivity.loginActivity = this;
//
// spin = new ProgressDialog(LoginActivity.loginActivity);
// spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
// spin.setCancelable(false);
// setScreenContent();
// }
//
// @Override
// public boolean onOptionsItemSelected(MenuItem item)
// {
// setScreenContent();
// return true;
// }
//
// @Override
// protected void onDestroy()
// {
// Log.i(TAG, "dismissing spinner");
// spin.dismiss();
// // Log.i(TAG, "super.onDestroy");
// super.onDestroy();
// }
//
// /**
// * Sets the screen content based on the auth cookie. <br>
// * {@link R.layout.disconnect} or {@link R.layout.connect}
// */
// public void setScreenContent()
// {
//
// if (!GCMRegistrar.isRegisteredOnServer(getApplicationContext()))
// {
// setContentView(R.layout.connect);
// setConnectScreenContent();
// return;
// }
// setContentView(R.layout.disconnect);
// setDisconnectScreenContent();
//
// }
//
// /**
// * Sets up the 'connect' screen content.
// */
// private void setConnectScreenContent()
// {
// Log.i(TAG, "seting connect screen content");
// List<String> accounts = getGoogleAccounts();
// if (accounts.size() == 0)
// {
// // Show a dialog and invoke the "Add Account" activity if requested
// AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.loginActivity);
// builder.setMessage(R.string.needs_account);
// builder.setPositiveButton(R.string.add_account, new AddAcountClickListener());
// builder.setNegativeButton(R.string.skip, new NegativeButtonClickListener());
// builder.setIcon(android.R.drawable.stat_sys_warning);
// builder.setTitle(R.string.attention);
// builder.show();
// }

// else
// {
// final ListView listView = (ListView) findViewById(R.id.select_account);
// listView.setAdapter(new ArrayAdapter<String>(LoginActivity.loginActivity,
// R.layout.login_lv_item,
// accounts));
// listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
// listView.setItemChecked(mAccountSelectedPosition, true);
//
// final Button connectButton = (Button) findViewById(R.id.connect);
// connectButton.setOnClickListener(new ConnectButtonClickListener(listView));
//
// final Button exitButton = (Button) findViewById(R.id.exit);
// exitButton.setOnClickListener(new ExitButtonClickListener());
// }
// }
//
// /**
// * Sets up the 'disconnected' screen.
// */
// private void setDisconnectScreenContent()
// {
// final SharedPreferences prefs =
// AndroidRequestUtils.getSharedPreferences(getApplicationContext());
// String accountName = prefs.getString(AndroidRequestUtils.ACCOUNT_NAME, "error");
//
// // Format the disconnect message with the currently connected account
// // name
// TextView disconnectText = (TextView) findViewById(R.id.disconnect_text);
// String message = getResources().getString(R.string.disconnect_text);
// String formatted = String.format(message, accountName);
// disconnectText.setText(formatted);
//
// Button disconnectButton = (Button) findViewById(R.id.disconnect);
// disconnectButton.setOnClickListener(new DisconnectButtonClickListener());
//
// Button exitButton = (Button) findViewById(R.id.exit);
// exitButton.setOnClickListener(new ExitButtonClickListener());
// }
//
// // Utility Methods
//
// /**
// * Returns a list of registered Google account names. If no Google accounts
// * are registered on the device, a zero-length list is returned.
// */
// private List<String> getGoogleAccounts()
// {
// ArrayList<String> result = new ArrayList<String>();
// Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
// for (Account account : accounts)
// {
// if (account.type.equals("com.google"))
// {
// result.add(account.name);
// }
// }
//
// return result;
// }
//
// /**************************************************************************
// *
// * click listeners
// *
// *************************************************************************/
//
// class AddAcountClickListener implements DialogInterface.OnClickListener
// {
// @Override
// public void onClick(DialogInterface dialog, int which)
// {
// startActivity(new Intent(Settings.ACTION_ADD_ACCOUNT));
// }
//
// }
//
// class NegativeButtonClickListener implements DialogInterface.OnClickListener
// {
// @Override
// public void onClick(DialogInterface dialog, int which)
// {
// finish();
// }
//
// }
//
// class ConnectButtonClickListener implements OnClickListener
// {
// private final ListView listView;
//
// public ConnectButtonClickListener(ListView listView)
// {
// this.listView = listView;
// }
//
// @Override
// public void onClick(View v)
// {
// // Register in the background and terminate the activity
// mAccountSelectedPosition = listView.getCheckedItemPosition();
// TextView accountTextView = (TextView) listView.getChildAt(mAccountSelectedPosition);
// new AuthenticateAndLoginAsyncTask(LoginActivity.this).execute(accountTextView.getText()
// .toString());
// }
//
// }
//
// class DisconnectButtonClickListener implements OnClickListener
// {
// @Override
// public void onClick(View v)
// {
// new UnregisterAsyncTask(LoginActivity.loginActivity).execute();
// }
//
// }
//
// class ExitButtonClickListener implements OnClickListener
// {
// @Override
// public void onClick(View v)
// {
// finish();
// }
// }
// }
