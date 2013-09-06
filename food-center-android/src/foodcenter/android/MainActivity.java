package foodcenter.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gcm.GCMRegistrar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import foodcenter.android.service.AuthCookieImageDownloader;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.Setup;
import foodcenter.android.service.restaurant.RestsGetAsyncTask;

public class MainActivity extends Activity
{
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int REQ_CODE_LOGIN = 0;

    private ProgressDialog spin;

    public MainActivity context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        spin = new ProgressDialog(context);
        spin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        spin.setCancelable(false);

        setContentView(R.layout.rests_gridview);

        // Create global configuration and initialize ImageLoader with this configuration
        Builder builder = new ImageLoaderConfiguration.Builder(getApplicationContext());
        // Use downloader with cookies from extra
        ImageDownloader downloader = new AuthCookieImageDownloader(this);
        ImageLoaderConfiguration config = builder.imageDownloader(downloader).build();
        ImageLoader.getInstance().init(config);

        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(context);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        // TODO GCMRegistrar.checkManifest(context);

        // register msg reciever handler (to show on ui thread)
        registerReceiver(handlePopupReceiver, new IntentFilter(Setup.DISPLAY_POPUP_ACTION));

        if (!gotoLoginActivity())        
        {
            SharedPreferences prefs = RequestUtils.getSharedPreferences(this);
            String accountName = prefs.getString(RequestUtils.ACCOUNT_NAME, null);
            showSpinner("logged in as: " + accountName);
            handleIntent(getIntent());
        }

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
//        gotoLoginActivity();
        
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
    public boolean onSearchRequested()
    {
        // TODO Auto-generated method stub
        return super.onSearchRequested();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_exit:
                finish();
                return true;
            case R.id.menu_login:
                // Invoke the Register activity
                startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQ_CODE_LOGIN);
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

    private final BroadcastReceiver handlePopupReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String newMessage = intent.getExtras().getString(Setup.EXTRA_MESSAGE);
            Popup.show(MainActivity.this, newMessage);
        }
    };

    private void handleIntent(Intent intent)
    {
        String query = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
             query = intent.getStringExtra(SearchManager.QUERY);
        }
        // use the query to search your data somehow
        new RestsGetAsyncTask(this).execute(query);
    }

}
