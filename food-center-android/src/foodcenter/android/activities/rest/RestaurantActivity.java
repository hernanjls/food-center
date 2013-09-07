package foodcenter.android.activities.rest;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import foodcenter.android.CommonUtilities;
import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.restaurant.RestGetAsyncTask;

public class RestaurantActivity extends Activity
{
    //private final static String TAG = RestaurantActivity.class.getSimpleName();

    public final static String EXTRA_REST_ID = "Extra Restaurant ID";

    // this is not pullable, but help changing action bar :)
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_view);

        initActionBar();

        initPullToRefresh();

        handleIntent(getIntent());
    }

    private void initActionBar()
    {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        SharedPreferences prefs = RequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME,
                                             getString(R.string.unknown_user));
        getActionBar().setSubtitle(accountName);
    }

    private void initPullToRefresh()
    {
        // Create new PullToRefreshAttacher
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshMinimizeDelay *= 3;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);

        // As we haven't set an explicit HeaderTransformer, we can safely cast the result of
        // getHeaderTransformer() to DefaultHeaderTransformer
        DefaultHeaderTransformer ht = (DefaultHeaderTransformer) mPullToRefreshAttacher.getHeaderTransformer();

        // As we're using a DefaultHeaderTransformer we can change the text which is displayed.
        // You should load these values from localised resources, but we'll just use static strings.
        
        ht.setPullText(getString(R.string.swipe_down_to_refresh));
        ht.setRefreshingText(getString(R.string.load_restaurant));

        // DefaultHeaderTransformer allows you to change the color of the progress bar. Here
        // we set it to a dark holo green, loaded from our resources
        ht.setProgressBarColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.rest_menu, menu);

        return true;
    }

    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        getActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.menu_help:
                CommonUtilities.displayMessage(this, "Currently not supported");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showSpinner()
    {
        mPullToRefreshAttacher.setRefreshing(true);
    }

    public void hideSpinner()
    {
        mPullToRefreshAttacher.setRefreshComplete();
    }

    private void handleIntent(Intent intent)
    {
        String restId = intent.getExtras().getString(EXTRA_REST_ID);
        if (null != restId)
        {
            new RestGetAsyncTask(this).execute(restId);
        }
        else
        {
            setTitle("Can't find restaurant id");
        }
    }
}
