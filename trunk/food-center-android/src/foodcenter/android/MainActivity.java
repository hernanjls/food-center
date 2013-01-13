package foodcenter.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import foodcenter.android.service.msg.MsgAddDialog;
import foodcenter.android.service.msg.MsgGetAsyncTask;

public class MainActivity extends Activity
{
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
	            break;
	        case R.id.menu_update_msgs:
	            new MsgGetAsyncTask(this).execute();
	            break;
            default:
                break;
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
	
}
