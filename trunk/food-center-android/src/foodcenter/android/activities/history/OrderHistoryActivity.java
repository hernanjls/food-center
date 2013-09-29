package foodcenter.android.activities.history;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import foodcenter.android.R;
import foodcenter.android.service.AndroidRequestUtils;

public class OrderHistoryActivity extends Activity implements OnRefreshListener
{

    private OrderHistoryListAdapter adapter;
    private ExpandableListView elv;
    private PullToRefreshAttacher pullToRefreshAttacher;
    
    private Integer[] toFrom = { 5 , 0 };
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_layot);
        
        adapter = new OrderHistoryListAdapter(this);
        
        elv = (ExpandableListView) findViewById(R.id.history_order_list);
        elv.setAdapter(adapter);
        
        initActionBar();
        initPullToRefresh();

        new OrderHistoryGetAsyncTask(this, adapter).execute(toFrom);
    }
    
    @Override
    public void onRefreshStarted(View view)
    {
        int to = toFrom[0];
        
        if (adapter.getGroupCount() < to)
        {
            hideSpinner();
            return;
        }
        
        int cnt = toFrom[0] - toFrom[1];
        toFrom[1]  = toFrom[0];
        toFrom[0] = toFrom[1] + cnt;
        new OrderHistoryGetAsyncTask(this, adapter).execute(toFrom);
    }

    private void initActionBar()
    {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        SharedPreferences prefs = AndroidRequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(AndroidRequestUtils.PREF_ACCOUNT_NAME, null);
        getActionBar().setSubtitle(accountName);

    }
    
    private void initPullToRefresh()
    {

        // Create new PullToRefreshAttacher
        pullToRefreshAttacher = PullToRefreshAttacher.get(this);

        // Retrieve the PullToRefreshLayout from the content view
        PullToRefreshLayout ptrLayout = (PullToRefreshLayout) findViewById(R.id.history_order);

        // Give the PullToRefreshAttacher to the PullToRefreshLayout, along with the refresh
        // listener (this).
        ptrLayout.setPullToRefreshAttacher(pullToRefreshAttacher, this);

        // As we haven't set an explicit HeaderTransformer, we can safely cast the result of
        // getHeaderTransformer() to DefaultHeaderTransformer
        DefaultHeaderTransformer ht = (DefaultHeaderTransformer) pullToRefreshAttacher.getHeaderTransformer();

        // As we're using a DefaultHeaderTransformer we can change the text which is displayed.
        // You should load these values from localised resources, but we'll just use static strings.
        ht.setPullText(getString(R.string.swipe_down_to_load_more));
        ht.setRefreshingText(getString(R.string.load_order_history)); //TODO

        // DefaultHeaderTransformer allows you to change the color of the progress bar. Here
        // we set it to a dark holo green, loaded from our resources
        ht.setProgressBarColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();        
        return super.onOptionsItemSelected(item);
    }


    public void showSpinner()
    {
        pullToRefreshAttacher.setRefreshing(true);
    }

    public void hideSpinner()
    {
        pullToRefreshAttacher.setRefreshComplete();
    }
}
