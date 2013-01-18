package foodcenter.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;
import foodcenter.android.service.msg.MsgAddDialog;
import foodcenter.android.service.msg.MsgGetAsyncTask;

public class MainActivity extends Activity
{
    private final static String TAG = MainActivity.class.getSimpleName();

	private ProgressDialog spin;

	private MainActivity context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		spin = new ProgressDialog(context);
		spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		spin.setCancelable(false);
        
        setContentView(R.layout.activity_main);

        // register msg reciever handler (to show on ui thread)
        registerReceiver(mHandleMessageReceiver, new IntentFilter(Setup.DISPLAY_MESSAGE_ACTION));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        SharedPreferences prefs = RequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME, null);
        if (null == accountName)
        {
        	startActivity(new Intent(this, LoginActivity.class));
        }
        else
        {
        	Popup.show(MainActivity.this, "logged in as: " + accountName);
            new MsgGetAsyncTask(this).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        // Invoke the Register activity
        menu.getItem(0).setIntent(new Intent(this, LoginActivity.class));        

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.menu_add_msg:
            new MsgAddDialog(MainActivity.this);
            return true;
        case R.id.menu_update_msgs:
            new MsgGetAsyncTask(MainActivity.this).execute();
            return true;
        case R.id.menu_exit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
        
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(mHandleMessageReceiver);
//        GCMRegistrar.onDestroy(MainActivity.this);
        Log.i(TAG, "dismissing spinner");
		spin.dismiss();
        Log.i(TAG, "super.onDestroy");
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
	
	public void hideSpinner()
	{
		if (spin.isShowing())
		{
			spin.hide();
		}
	}
	
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String newMessage = intent.getExtras().getString(Setup.EXTRA_MESSAGE);
            Popup.show(MainActivity.this, newMessage);
        }
    };
}
