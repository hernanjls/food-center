package foodcenter.android.activities.rest;

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
import foodcenter.android.AndroidUtils;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.adapters.MenuListAdapter;
import foodcenter.android.adapters.OrderConfAdapter;
import foodcenter.android.data.OrderConfData;
import foodcenter.android.service.restaurant.MakeOrderAsyncTask;

public class OrderConfActivity extends ListActivity
{
    private OrderConfData data;
    private final DecimalFormat df = new DecimalFormat("#.0");
    private ProgressDialog spiner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_NONE);

        initSpiner();
        initActionBar();
        handleIntent(getIntent());
    }

    private void initSpiner()
    {
        spiner = new ProgressDialog(this);
        spiner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        spiner.setCancelable(false);

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
        data = ObjectStore.get(OrderConfData.class, OrderConfData.CACHE_KEY);
        if (null == data)
        {
            return;
        }
        OrderConfAdapter adapter = new OrderConfAdapter(this, data);

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
                showSpinner(getString(R.string.makeing_order));
                new MakeOrderAsyncTask(this, 1).execute(data);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        hideSpinner();

        super.onDestroy();
    }

    private void showSpinner(String msg)
    {
        spiner.setMessage(msg);
        if (!spiner.isShowing())
        {
            spiner.show();
        }
    }

    public void orderFail(String msg, boolean retry, int attempt)
    {   
        if (retry && ( attempt < MakeOrderAsyncTask.MAX_ATTEMPS))
        {
            
            showSpinner(getString(R.string.makeing_order_retry, attempt) + ": " + msg);
            new MakeOrderAsyncTask(this, attempt + 1 ).execute(data);
            return;
        }
        hideSpinner();
        AndroidUtils.displayMessage(this, msg);
    }
    
    public void orderSuccess()
    {
        hideSpinner();

        String msg = getString(R.string.order_success);
        AndroidUtils.displayMessage(this, msg);
        
        // remove the Order and the Adapter from cache
        ObjectStore.put(OrderConfData.class, OrderConfData.CACHE_KEY, null);
        ObjectStore.put(MenuListAdapter.class, data.getRestBranchId(), null);
        
        // Navigate back to main view
        NavUtils.navigateUpFromSameTask(this);
    }

    private void hideSpinner()
    {
        if (spiner.isShowing())
        {
            spiner.dismiss();
        }
    }
}
