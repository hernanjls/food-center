package foodcenter.android.activities.coworkers;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import foodcenter.android.R;
import foodcenter.android.service.AndroidRequestUtils;

public class CoworkersActivity extends Activity
{
    private ListView lv;

    // this is not pull-able, but help changing action bar :)
    private PullToRefreshAttacher pullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coworkers_view);

        lv = (ListView) findViewById(R.id.coworkers_view_list);

        initActionBar();
        initPullToRefresh();
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
        
        new CoworkersGetAsyncTask(this, lv).execute();
    }

    private void initActionBar()
    {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        SharedPreferences prefs = AndroidRequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(AndroidRequestUtils.PREF_ACCOUNT_NAME,
                                             getString(R.string.unknown_user));
        getActionBar().setSubtitle(accountName);
    }

    private void initPullToRefresh()
    {
        // Create new PullToRefreshAttacher
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshMinimizeDelay *= 3;
        pullToRefreshAttacher = PullToRefreshAttacher.get(this, options);

        /*
         * //Don't Attach pull to refresh
         * // Retrieve the PullToRefreshLayout from the content view
         * PullToRefreshLayout ptrLayout = (PullToRefreshLayout)findViewById(R.id.history_order);
         * 
         * // Give the PullToRefreshAttacher to the PullToRefreshLayout, along with the refresh
         * // listener (this).
         * ptrLayout.setPullToRefreshAttacher(pullToRefreshAttacher, this);
         */

        // As we haven't set an explicit HeaderTransformer, we can safely cast the result of
        // getHeaderTransformer() to DefaultHeaderTransformer
        DefaultHeaderTransformer ht = (DefaultHeaderTransformer) pullToRefreshAttacher.getHeaderTransformer();

        // As we're using a DefaultHeaderTransformer we can change the text which is displayed.
        // You should load these values from localised resources, but we'll just use static strings.

        // ht.setPullText(getString(R.string.swipe_down_to_refresh));
        ht.setRefreshingText(getString(R.string.load_coworkers));

        // DefaultHeaderTransformer allows you to change the color of the progress bar. Here
        // we set it to a dark holo green, loaded from our resources
        ht.setProgressBarColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                // onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
