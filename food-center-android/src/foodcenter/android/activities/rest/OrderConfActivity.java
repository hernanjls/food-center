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
import foodcenter.android.activities.helpers.OrderConfData;
import foodcenter.android.adapters.OrderConfAdapter;
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
                new MakeOrderAsyncTask(this).execute(data);
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

    public void showSpinner()
    {
        if (!spiner.isShowing())
        {
            String msg = getString(R.string.makeing_order);
            spiner.setMessage(msg);
            spiner.show();
        }
    }

    public void orderSuccess()
    {
        hideSpinner();

        String msg = getString(R.string.order_success);
        AndroidUtils.displayMessage(this, msg);
        
        // Navigate back to main view
        NavUtils.navigateUpFromSameTask(this);
    }

    public void hideSpinner()
    {
        if (spiner.isShowing())
        {
            spiner.dismiss();
        }
    }
}
