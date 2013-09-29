package foodcenter.android.activities.coworkers;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import foodcenter.android.R;
import foodcenter.android.activities.MsgBroadcastReceiver;
import foodcenter.android.service.AndroidRequestUtils;

public class CoworkersActivity extends Activity implements OnItemClickListener
{
    public static final String IS_TABLE_RESERVATION_VIEW = "foodcenter.android.IS_RESERVATION_VIEW";

    private ListView lv;

    // this is not pull-able, but help changing action bar :)
    private PullToRefreshAttacher pullToRefreshAttacher;

    private boolean isTableReservation;
    private int totalSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coworkers_view);

        lv = (ListView) findViewById(R.id.coworkers_view_list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new ModeCallback());
        lv.setOnItemClickListener(this);

        Bundle extras = getIntent().getExtras();
        isTableReservation = (null == extras) ? false
                                             : extras.getBoolean(IS_TABLE_RESERVATION_VIEW, false);

        totalSelected = 0;

        initActionBar();
        initPullToRefresh();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        new CoworkersGetAsyncTask(this, lv, isTableReservation).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
    {
        // 1st click, after this moving to action mode
        lv.setItemChecked(position, true);
        ++totalSelected;
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
                // NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
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

    private class ModeCallback implements ListView.MultiChoiceModeListener
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.coworkers_select_menu, menu);

            mode.setTitle("Select coworkers");
            showTotalSelected(mode);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.coworkers_select_reserve:
                    MsgBroadcastReceiver.toast(CoworkersActivity.this, "Not implemented yet!");
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            // reset the counter for next time
            totalSelected = 0;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position,
                                              long id,
                                              boolean checked)
        {

            totalSelected += checked ? 1 : -1;
            showTotalSelected(mode);
        }

        private void showTotalSelected(ActionMode mode)
        {
            String s = getString(R.string.total_selected, totalSelected);
            mode.setSubtitle(s);
        }
    }

}
