package foodcenter.android.activities.main;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import foodcenter.android.AndroidUtils;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.activities.SpinableActivity;
import foodcenter.android.activities.coworkers.CoworkersActivity;
import foodcenter.android.activities.history.OrderHistoryActivity;
import foodcenter.android.activities.login.AuthenticateAndSigninAsyncTask;
import foodcenter.android.activities.login.LoginDialogFragment;
import foodcenter.android.activities.login.LoginDialogFragment.LoginDialogListener;
import foodcenter.android.activities.login.SignontAsyncTask;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.android.service.AuthCookieImageDownloader;

public class MainActivity extends FragmentActivity implements
                                                  PullToRefreshAttacher.OnRefreshListener,
                                                  ListView.OnItemClickListener, SpinableActivity,
                                                  LoginDialogListener

{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String LOGIN_FRAG_TAG = "Login TAG";
    
    public MainActivity context = this;

    private ActionBarDrawer actionBarDrawer;

//    private ServerSignCallback serverCallback;
    
    private PullToRefreshAttacher mPullToRefreshAttacher;
    
    private ProgressDialog progress;

    
    /** uses {@link AndroidUtils#toast(Context, String)} */
    private final BroadcastReceiver handleMessages = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            handleIntent(intent);
        }
    };

    @Override
    public void onSignInClick(DialogFragment dialog)
    {
        if (!GCMRegistrar.isRegisteredOnServer(getApplicationContext()))
        {
            new AuthenticateAndSigninAsyncTask(this).execute();
        }
    }

    @Override
    public void onCancelClick(DialogFragment dialog)
    {
        if (!GCMRegistrar.isRegisteredOnServer(getApplicationContext()))
        {
            showSignInDialog();
        }
    }
    
    @Override
    public void onSignOutClick(DialogFragment dialog)
    {
        if (GCMRegistrar.isRegisteredOnServer(getApplicationContext()))
        {
            new SignontAsyncTask(this).execute();    
        }
        else
        {
            onCancelClick(dialog);
        }
        
    }


    @Override
    public Activity getActivity()
    {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        // Setup the server URL on start-up
        AndroidRequestUtils.setUpUrl(this);
        progress = new ProgressDialog(MainActivity.this);
        progress.setCanceledOnTouchOutside(false);

        // register msg reciever handler (to show on ui thread)
        registerReceiver(handleMessages, new IntentFilter(AndroidUtils.ACTION_SHOW_PROGRESS));
        registerReceiver(handleMessages, new IntentFilter(AndroidUtils.ACTION_SHOW_TOAST));
        registerReceiver(handleMessages, new IntentFilter(AndroidUtils.ACTION_SIGNED_IN));
        registerReceiver(handleMessages, new IntentFilter(AndroidUtils.ACTION_SIGNED_OUT));

        initGCMService();
        initImageLoader();
        initActionBar();

        actionBarDrawer = new ActionBarDrawer(this, this);
        setTitle(actionBarDrawer.getTitle());

        initPullToRefresh();

        if (!GCMRegistrar.isRegisteredOnServer(getApplicationContext()))
        {
            showSignInDialog();
        }
        else
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
    }

    private void initImageLoader()
    {

        if (!ImageLoader.getInstance().isInited())
        {
            // Create global configuration and initialize ImageLoader with this configuration
            Builder builder = new ImageLoaderConfiguration.Builder(getApplicationContext());

            // Use downloader with cookies from extra
            ImageDownloader downloader = new AuthCookieImageDownloader(this);
            ImageLoaderConfiguration config = builder.imageDownloader(downloader).build();

            ImageLoader.getInstance().init(config);
        }
    }

    private void initActionBar()
    {
        getActionBar().setDisplayShowTitleEnabled(true);
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
        ht.setPullText(getString(R.string.swipe_down_to_refresh));
        ht.setRefreshingText(getString(R.string.load_restaurants));

        // DefaultHeaderTransformer allows you to change the color of the progress bar. Here
        // we set it to a dark holo green, loaded from our resources
        ht.setProgressBarColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    private void showSignInDialog()
    {
        new LoginDialogFragment().show(getSupportFragmentManager(), LOGIN_FRAG_TAG);
    }

    @Override
    public void onRefreshStarted(View view)
    {
        ObjectStore.clear();
        Intent i = new Intent(Intent.ACTION_MAIN);
        handleIntent(i);
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

    /** The click listner for ListView in the navigation drawer */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = null;
        switch (position)
        {
            case ActionBarDrawerAdapter.PROFILE_POSITION:
                showSignInDialog();
                break;
            case ActionBarDrawerAdapter.HISTORY_POSITION:
                i = new Intent(this, OrderHistoryActivity.class);
                startActivity(i);
                break;
            case ActionBarDrawerAdapter.COWORKERS_POSITION:
                i = new Intent(this, CoworkersActivity.class);
                startActivity(i);
                break;
            default:
                String s = (String) actionBarDrawer.getItemAtPosition(position);
                AndroidUtils.toast(this, s + " not supported yet...");
                break;
        }

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
                onRefreshStarted(null);
                return true;
            case R.id.menu_setting:
                AndroidUtils.toast(this, "Currently not supported");
                return true;
            case R.id.menu_help:
                AndroidUtils.toast(this, "Currently not supported");
                return true;
            case R.id.menu_signout:
                new SignontAsyncTask(this).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(handleMessages);
        GCMRegistrar.onDestroy(getApplicationContext());
        Log.i(TAG, "super.onDestroy");
        super.onDestroy();
    }

    @Override
    public void showSpinner()
    {
        mPullToRefreshAttacher.setRefreshing(true);
    }

    @Override
    public void hideSpinner()
    {
        mPullToRefreshAttacher.setRefreshComplete();
    }

    private void handleIntent(Intent intent)
    {
            
        String query = null;
        SharedPreferences prefs = AndroidRequestUtils.getSharedPreferences(this);
        String accountName = prefs.getString(AndroidRequestUtils.PREF_ACCOUNT_NAME, null);
        getActionBar().setSubtitle(accountName);

        
        final String action = intent.getAction();
        Log.d(TAG, "handle intent: " + action);
        
        if (Intent.ACTION_SEARCH.equals(action))
        {
            query = intent.getStringExtra(SearchManager.QUERY);            
            // continue to default behavior
        }
        else if (AndroidUtils.ACTION_SHOW_PROGRESS.equals(action))
        {
            String msg = intent.getStringExtra(AndroidUtils.EXTRA_MESSAGE);
            
            if (null != msg)
            {
                progress.setMessage(msg);
                progress.show();    
            }
            else
            {
                progress.dismiss();
            }
            return;
        }
        
        else if (AndroidUtils.ACTION_SHOW_TOAST.equals(action))
        {
            String msg = intent.getStringExtra(AndroidUtils.EXTRA_MESSAGE);
            if (null != msg)
            {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
            return;
        }
        else if (AndroidUtils.ACTION_SIGNED_IN.equals(action))
        {
            progress.dismiss();
            
            String msg = intent.getStringExtra(AndroidUtils.EXTRA_MESSAGE);
            if (null != msg)
            {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
            
            actionBarDrawer.notifyDataSetChanged();
            // continue to default behavior
        }
        else if (AndroidUtils.ACTION_SIGNED_OUT.equals(action))
        {
            progress.dismiss();
            
            actionBarDrawer.notifyDataSetChanged();
            
            String msg = intent.getStringExtra(AndroidUtils.EXTRA_MESSAGE);
            if (null != msg)
            {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
            
            ObjectStore.clear();
            
            showSignInDialog();
            
            return;
        }

        // by default show restaurants on screen
        new RestsGetAsyncTask(this).execute(query);

    }
}
