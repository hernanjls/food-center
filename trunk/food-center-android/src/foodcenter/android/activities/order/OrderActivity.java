package foodcenter.android.activities.order;

import java.text.DecimalFormat;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.activities.MsgBroadcastReceiver;
import foodcenter.android.activities.branch.BranchMenuListAdapter;
import foodcenter.android.data.OrderData;

public class OrderActivity extends ListActivity
{
    

    private OrderData data;
    private final DecimalFormat df = new DecimalFormat("#.0");
    private ProgressDialog progress;
    private MsgBroadcastReceiver handleMsg;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_NONE);

        initProgressDialog();
        
        handleMsg = new MsgBroadcastReceiver(progress);
        handleMsg.registerMe(this);
    
        initActionBar();
        handleIntent(getIntent());
    }

    private void initProgressDialog()
    {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);

    }

    private void initActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.order_title);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        TextView view = (TextView) findViewById(R.id.order_title);
        view.setText(title);
        super.setTitle(title);
    }

    public void setSubtitle(CharSequence title)
    {
        TextView view = (TextView) findViewById(R.id.order_subtitle);
        view.setText(title);
    }

    private void handleIntent(Intent intent)
    {
        // Get the adapter from the cache (with all the info)
        data = ObjectStore.get(OrderData.class, OrderData.CACHE_KEY);
        if (null == data)
        {
            return;
        }
        OrderCourseListAdapter adapter = new OrderCourseListAdapter(this, data);

        String title = getString(R.string.title_order_confirmation) + " " + df.format(adapter.getTotalPrice());
        setTitle(title);
        
        String subtitle = getString(R.string.subtitle_order_confirmation) + " " + data.getService().getName();
        setSubtitle(subtitle);
        

        // Set the adapter
        ListView branchView = getListView();
        branchView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (null == data)
        {
            return true;
        }

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_confirm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.order_confirm_menu_ok:
                MsgBroadcastReceiver.progress(this, getString(R.string.makeing_order));
                new MakeOrderAsyncTask(this, 1).execute(data);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        
        progress.dismiss();
        
        unregisterReceiver(handleMsg);
        
        super.onDestroy();
    }


    public void orderFail(String msg, boolean retry, int attempt)
    {   
        if (retry && ( attempt < MakeOrderAsyncTask.MAX_ATTEMPS))
        {
            
            MsgBroadcastReceiver.progress(this, getString(R.string.makeing_order_retry, attempt) + ": " + msg);
            new MakeOrderAsyncTask(this, attempt + 1 ).execute(data);
            return;
        }
        MsgBroadcastReceiver.progressDismissAndToastMsg(this, msg);
    }
    
    public void orderSuccess()
    {
        MsgBroadcastReceiver.progress(this, null);

        String msg = getString(R.string.order_success);
        MsgBroadcastReceiver.toast(this, msg);
        
        // remove the Order and the Adapter from cache
        ObjectStore.put(OrderData.class, OrderData.CACHE_KEY, null);
        ObjectStore.put(BranchMenuListAdapter.class, data.getRestBranchId(), null);
        
        // Navigate back to main view
        NavUtils.navigateUpFromSameTask(this);
    }    
}
