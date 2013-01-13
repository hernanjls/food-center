package foodcenter.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import foodcenter.android.service.msg.MsgAddAsyncTask;
import foodcenter.android.service.msg.MsgGetAsyncTask;
import foodcenter.android.service.msg.OnClickAddMsg;

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
	            showAddMsgDialog();
	            break;
            default:
                break;
	    }
	    
	    return super.onOptionsItemSelected(item);
	}
	
	private void showAddMsgDialog()
	{
	    EditText msgText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("add new msg:");
        builder.setCancelable(false);
        builder.setView(msgText);
        String msg = msgText.getText().toString();
        Log.i("msg", msg);
        builder.setPositiveButton("Add Msg", new OnClickAddMsg(this, msgText));  
        builder.create().show();
	}
	
	
}
