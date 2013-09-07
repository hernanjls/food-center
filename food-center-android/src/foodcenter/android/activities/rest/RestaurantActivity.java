package foodcenter.android.activities.rest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import foodcenter.android.CommonUtilities;
import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;

public class RestaurantActivity extends Activity
{
    private final static String TAG = RestaurantActivity.class.getSimpleName();
    
    public final static String EXTRA_REST_ID = "Extra Restaurant ID";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_view);

        initActionBar();
        
        handleIntent(getIntent());
    }



    private void initActionBar()
    {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        SharedPreferences prefs = RequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME, getString(R.string.unknown_user));
        getActionBar().setSubtitle(accountName);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.rest_menu, menu);

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
            case R.id.menu_help:
                CommonUtilities.displayMessage(this, "Currently not supported");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showSpinner()
    {
        // TODO show spinner on actionBar
    }

    public void hideSpinner()
    {
        //TODO hide spinner on actionBar
    }

    private void handleIntent(Intent intent)
    {
        //TODO handleIntent
//        String query = null;
//        
//        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
//        {
//            query = intent.getStringExtra(SearchManager.QUERY);
//        }
//        // use the query to search your data somehow
//        new RestGetAsyncTask(this).execute(query);
    }
}
