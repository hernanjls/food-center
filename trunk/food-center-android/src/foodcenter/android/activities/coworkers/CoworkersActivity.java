package foodcenter.android.activities.coworkers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
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
import foodcenter.android.activities.branch.BranchActivity;
import foodcenter.android.activities.coworkers.CoworkersGetRunnable.CoworkersGetCallback;
import foodcenter.android.activities.coworkers.MakeTableReservationAsyncTask.MakeTableReservationCallback;
import foodcenter.android.activities.coworkers.RangeTimePickerFragment.RangeTimePickerListener;
import foodcenter.android.activities.rest.RestActivity;
import foodcenter.android.data.ReservationData;
import foodcenter.android.service.AndroidRequestUtils;

/**
 * Shows the co-worker list / allow to reserve a table
 */
public class CoworkersActivity extends FragmentActivity implements OnItemClickListener,
                                                       MakeTableReservationCallback,
                                                       CoworkersGetCallback,
                                                       RangeTimePickerListener
{

    private final static String RESERVE_FRAG_TAG = "foodcenter.android.RESERVE_FRAG_TAG";

    /** list view hold all the workers */
    private ListView lv;

    /** this is not pull-able, but help changing action bar :) */
    private PullToRefreshAttacher pullToRefreshAttacher;

    private String branchId = null;
    private String restId = null;

    /** Handles dialog calls and toasts */
    private ProgressDialog progress;
    private MsgBroadcastReceiver handleMsg;

    private CoworkersListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coworkers_view);

        progress = new ProgressDialog(this);
        progress.setCanceledOnTouchOutside(false);

        handleMsg = new MsgBroadcastReceiver(progress);
        handleMsg.registerMe(this);

        lv = (ListView) findViewById(R.id.coworkers_view_list);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new ModeCallback());
        lv.setOnItemClickListener(this);

        initActionBar();
        initPullToRefresh();

        Bundle extras = getIntent().getExtras();
        if (null != extras)
        {
            branchId = extras.getString(BranchActivity.EXTRA_BRANCH_ID);
            restId = extras.getString(RestActivity.EXTRA_REST_ID);
        }

        boolean isReservation = (null != branchId) && (null != restId);
        @SuppressWarnings("unchecked")
        List<String> savedState = (List<String>) getLastCustomNonConfigurationInstance();

        adapter = new CoworkersListAdapter(this, savedState, isReservation);
        lv.setAdapter(adapter);
        
        if (null == savedState)
        {
            pullToRefreshAttacher.setRefreshing(true);
            new Thread(new CoworkersGetRunnable(this, this)).start();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance()
    {
        final List<String> savedState = adapter.getSavedState();
        return savedState;
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(handleMsg);
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
    {
        // 1st click, after this moving to action mode
        lv.setItemChecked(position, true);
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

    @Override
    public void onReservationFail(String msg)
    {
        MsgBroadcastReceiver.progressDismissAndToastMsg(this, msg);
    }

    @Override
    public void onReservationSuccess()
    {
        String msg = getString(R.string.reservation_success);
        MsgBroadcastReceiver.progressDismissAndToastMsg(this, msg);

        // Navigate back to main view
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onSuccessGetCoworkers(final List<String> coworkers)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pullToRefreshAttacher.setRefreshComplete();
                adapter.addCoworkers(coworkers);
            };
        });

    }

    @Override
    public void onFailGetCoworkers(final String msg)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                pullToRefreshAttacher.setRefreshComplete();
                MsgBroadcastReceiver.progressDismissAndToastMsg(CoworkersActivity.this, msg);
            };
        });
    }

    @Override
    public void onReserveClick(RangeTimePickerFragment dialog)
    {
        String msg = getString(R.string.make_table_reservation);
        MsgBroadcastReceiver.progress(CoworkersActivity.this, msg);
        ReservationData data = getReservationData(dialog.getStartHr(),
                                                  dialog.getStartMin(),
                                                  dialog.getEndHr(),
                                                  dialog.getEndMin());
        new MakeTableReservationAsyncTask(CoworkersActivity.this, CoworkersActivity.this).execute(data);

    }

    @Override
    public void onCancelClick(RangeTimePickerFragment dialog)
    {
        // Do nothing here !!!!
    }

    private ReservationData getReservationData(int startHr, int startMin, int endHr, int endMin)
    {
        ReservationData res = new ReservationData();

        res.setRestBranchId(branchId);
        res.setRestId(restId);

        // Add users to the data
        int n = lv.getCount();
        for (int i = 0; i < n; ++i)
        {
            if (lv.isItemChecked(i))
            {
                res.getUsers().add((String) lv.getItemAtPosition(i));
            }
        }

        // Set acceptable start date
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(new Date());
        fromCalendar.set(Calendar.HOUR, startHr);
        fromCalendar.set(Calendar.MINUTE, startMin);
        fromCalendar.set(Calendar.SECOND, 0);
        Date fromDate = fromCalendar.getTime();
        res.setFromDate(fromDate);

        // Set acceptable end date
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(new Date());
        toCalendar.set(Calendar.HOUR, endHr);
        toCalendar.set(Calendar.MINUTE, endMin);
        toCalendar.set(Calendar.SECOND, 0);
        Date toDate = toCalendar.getTime();
        res.setToDate(toDate);

        return res;
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
                    new RangeTimePickerFragment().show(getSupportFragmentManager(),
                                                       RESERVE_FRAG_TAG);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            //
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position,
                                              long id,
                                              boolean checked)
        {
            showTotalSelected(mode);
        }

        private void showTotalSelected(ActionMode mode)
        {
            String s = getString(R.string.total_selected, lv.getCheckedItemCount()); 
            Log.d("showTotalSelected", s);
            mode.setSubtitle(s);
        }
    }

}
