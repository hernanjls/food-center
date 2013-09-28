package foodcenter.android.activities.coworkers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import foodcenter.android.R;
import foodcenter.android.activities.SpinableActivity;
import foodcenter.android.service.AndroidRequestUtils;

public class CoworkersActivity extends Activity implements SpinableActivity
{
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coworkers_view);

        lv = (ListView)findViewById(R.id.coworkers_view_list);
        
        initActionBar();
        
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

    @Override
    public void showSpinner()
    {
        // TODO Auto-generated method stub        
    }

    @Override
    public void hideSpinner()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public Activity getActivity()
    {
        return this;
    }

}
