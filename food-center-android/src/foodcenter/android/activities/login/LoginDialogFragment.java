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
        serversLv.setAdapter(new StringArrayAdapter(R.layout.signin_lv_item, servers));
        serversLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        accountsLv = (ListView) view.findViewById(R.id.login_accounts_list);
        accountsLv.setAdapter(new StringArrayAdapter(R.layout.signin_lv_item, accounts));
        accountsLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        builder.setView(view);

        if (isLogedIn)
        {
            showSignedInView(builder);
        }
        else
        {
            showSignedOutView(builder);
        }

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
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

    private void showSignedInView(AlertDialog.Builder builder)
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

    private void showSignedOutView(AlertDialog.Builder builder)
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

    private class StringArrayAdapter extends ArrayAdapter<String>
    {
        public StringArrayAdapter(int layout, List<String> objects)
        {
            super(getActivity(), layout, objects);
        }

        @Override
        public boolean isEnabled(int position)
        {
            return !isLogedIn;
        }
    }
}
