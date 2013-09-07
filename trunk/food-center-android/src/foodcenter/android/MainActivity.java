package foodcenter.android;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gcm.GCMRegistrar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import foodcenter.android.actionbar.ActionBarDrawer;
import foodcenter.android.service.AuthCookieImageDownloader;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;
import foodcenter.android.service.restaurant.RestsGetAsyncTask;

public class MainActivity extends Activity implements PullToRefreshAttacher.OnRefreshListener, ListView.OnItemClickListener

{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int REQ_CODE_LOGIN = 0;

    private ProgressDialog spin;

    public MainActivity context = this;

    private ActionBarDrawer actionBarDrawer;

    private PullToRefreshAttacher mPullToRefreshAttacher;

    private final BroadcastReceiver handlePopupReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String newMessage = intent.getExtras().getString(Setup.EXTRA_MESSAGE);
            Popup.show(MainActivity.this, newMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        spin = new ProgressDialog(context);
        spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        spin.setCancelable(false);

        setContentView(R.layout.main_view);

        initGCMService();
        initImageLoader();
        initActionBar();
        actionBarDrawer = new ActionBarDrawer(this, this);
        
        setTitle(actionBarDrawer.getTitle());
        
        initPullToRefresh();

        if (!gotoLoginActivity())
        {
            handleIntent(getIntent());
        }

    }

    private void initGCMService()
    {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(context);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        // TODO GCMRegistrar.checkManifest(context);

        // register msg reciever handler (to show on ui thread)
        registerReceiver(handlePopupReceiver, new IntentFilter(Setup.DISPLAY_POPUP_ACTION));

    }

    private void initImageLoader()
    {

        // Create global configuration and initialize ImageLoader with this configuration
        Builder builder = new ImageLoaderConfiguration.Builder(getApplicationContext());

        // Use downloader with cookies from extra
        ImageDownloader downloader = new AuthCookieImageDownloader(this);
        ImageLoaderConfiguration config = builder.imageDownloader(downloader).build();

        ImageLoader.getInstance().init(config);
    }

    private void initActionBar()
    {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_TITLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    private void initPullToRefresh()
    {
        // Create new PullToRefreshAttacher
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);

        // Retrieve the PullToRefreshLayout from the content view
        PullToRefreshLayout ptrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

        // Give the PullToRefreshAttacher to the PullToRefreshLayout, along with the refresh
        // listener (this).
        ptrLayout.setPullToRefreshAttacher(mPullToRefreshAttacher, this);

        // As we haven't set an explicit HeaderTransformer, we can safely cast the result of
        // getHeaderTransformer() to DefaultHeaderTransformer
        DefaultHeaderTransformer ht = (DefaultHeaderTransformer) mPullToRefreshAttacher.getHeaderTransformer();

        // As we're using a DefaultHeaderTransformer we can change the text which is displayed.
        // You should load these values from localised resources, but we'll just use static strings.
        ht.setPullText("Swipe down to refresh");
        ht.setRefreshingText("Refreshing ...");

        // DefaultHeaderTransformer allows you to change the color of the progress bar. Here
        // we set it to a dark holo green, loaded from our resources
        ht.setProgressBarColor(getResources().getColor(R.color.holo_dark_green));
    }

    private boolean gotoLoginActivity()
    {
        if (!GCMRegistrar.isRegisteredOnServer(getApplicationContext()))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQ_CODE_LOGIN);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (REQ_CODE_LOGIN == requestCode)
        {
            if (RESULT_OK == resultCode)
            {
                handleIntent(data);
            }
            else
            {
                gotoLoginActivity();
            }
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // gotoLoginActivity();

    }

    @Override
    public void onRefreshStarted(View view)
    {
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawer.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        actionBarDrawer.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        actionBarDrawer.setTitle(title);
        getActionBar().setTitle(title);
    }

    /* The click listner for ListView in the navigation drawer */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        actionBarDrawer.getItemAtPosition(position);
        actionBarDrawer.closeDrawer();
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (actionBarDrawer.onOptionsItemSelected(item))
        {
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.menu_refresh:
                handleIntent(getIntent());
                return true;
            case R.id.menu_setting:
                CommonUtilities.displayMessage(this, "Currently not supported");
                return true;
            case R.id.menu_help:
                CommonUtilities.displayMessage(this, "Currently not supported");
                return true;
            case R.id.menu_login:
                // Invoke the Register activity
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class),
                                       REQ_CODE_LOGIN);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(handlePopupReceiver);
        GCMRegistrar.onDestroy(getApplicationContext());

        // GCMRegistrar.onDestroy(MainActivity.this);
        Log.i(TAG, "dismissing spinner");
        spin.dismiss();
        Log.i(TAG, "super.onDestroy");
        super.onDestroy();
    }

    public void showSpinner(String msg)
    {
        spin.setMessage(msg);
        if (!spin.isShowing())
        {
            spin.show();
        }
    }

    public void hideSpinner()
    {
        if (spin.isShowing())
        {
            spin.dismiss();
        }
    }

    private void handleIntent(Intent intent)
    {
        String query = null;

        SharedPreferences prefs = RequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME, null);
        getActionBar().setSubtitle(accountName);

        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            query = intent.getStringExtra(SearchManager.QUERY);
        }
        // use the query to search your data somehow
        new RestsGetAsyncTask(this, mPullToRefreshAttacher).execute(query);
    }


    
}
