package foodcenter.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gcm.GCMRegistrar;

import foodcenter.android.service.Setup;
import foodcenter.android.service.msg.MsgAddDialog;
import foodcenter.android.service.msg.MsgGetAsyncTask;

public class MainActivity extends Activity
{
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // display msgs
        registerReceiver(mHandleMessageReceiver, new IntentFilter(Setup.DISPLAY_MESSAGE_ACTION));

        // gcm registration
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);
        if (regId.equals(""))
        {
            GCMRegistrar.register(this, Setup.SENDER_ID);    
        }
        else
        {
            Log.i(TAG,"Already registered");
        }
        
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        new MsgGetAsyncTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.menu_add_msg:
            new MsgAddDialog(this);
            return true;
        case R.id.menu_update_msgs:
            new MsgGetAsyncTask(this).execute();
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
        GCMRegistrar.onDestroy(MainActivity.this);
        super.onDestroy();
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String newMessage = intent.getExtras().getString(Setup.EXTRA_MESSAGE);
            Popup.show(MainActivity.this, newMessage);
            new MsgGetAsyncTask(MainActivity.this).execute();
        }
    };
}
