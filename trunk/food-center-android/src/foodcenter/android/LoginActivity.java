package foodcenter.android;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import foodcenter.android.service.gcm.RegisterAsyncTask;
import foodcenter.android.service.gcm.UnregisterAsyncTask;

public class LoginActivity extends Activity
{

	/**
	 * Tag for logging.
	 */
	private static final String TAG = LoginActivity.class.getSimpleName();

	/**
	 * The current context.
	 */
	private LoginActivity mContext = this;

	/**
	 * The selected position in the ListView of accounts.
	 */
	private int mAccountSelectedPosition = 0;

	private ProgressDialog spin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		spin = new ProgressDialog(mContext);
		spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		spin.setCancelable(false);
		setScreenContent();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		setScreenContent();
		return true;
	}

	@Override
	protected void onDestroy()
	{
		GCMRegistrar.onDestroy(mContext);
		Log.i(TAG, "dismissing spinner");
		spin.dismiss();
		// Log.i(TAG, "super.onDestroy");
		super.onDestroy();
	}

	public void showSpinner(String msg)
	{
		spin.setMessage(msg);
		if (!spin.isShowing())
		{
			spin.show();
		}
	}
	
	/**
	 * Sets the screen content based on the auth cookie. <br>
	 * {@link R.layout.disconnect} or {@link R.layout.connect}
	 */
	private void setScreenContent()
	{
		SharedPreferences prefs = RequestUtils.getSharedPreferences(mContext);
		String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME, null);
		if (accountName == null)
		{
			setContentView(R.layout.connect);
			setConnectScreenContent();
			return;
		}
		setContentView(R.layout.disconnect);
		setDisconnectScreenContent();

	}

	/**
	 * Sets up the 'connect' screen content.
	 */
	private void setConnectScreenContent()
	{
		Log.i(TAG, "seting connect screen content");
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

	// Utility Methods

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

	/**************************************************************************
	 * 
	 * click listeners
	 * 
	 *************************************************************************/

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
			TextView accountTextView = (TextView) listView.getChildAt(mAccountSelectedPosition);
			new RegisterAsyncTask(mContext).execute(accountTextView.getText().toString());
		}

	}

	class DisconnectButtonClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			new UnregisterAsyncTask(mContext).execute();
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
}
