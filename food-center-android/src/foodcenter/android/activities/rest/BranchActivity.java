package foodcenter.android.activities.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import foodcenter.android.ObjectCashe;
import foodcenter.android.R;
import foodcenter.android.service.restaurant.MenuListAdapter;
import foodcenter.android.service.restaurant.SwipeListViewTouchListener;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

public class BranchActivity extends ListActivity implements
                                                SwipeListViewTouchListener.OnSwipeCallback
{

    public static final String EXTRA_BRANCH_ID = "Extra Branch ID";

    private final static String TAG = BranchActivity.class.getSimpleName();

    // this is not pull-able, but helps animating action bar :)
    private PullToRefreshAttacher mPullToRefreshAttacher;

    private RestaurantBranchProxy branch = null;
    private List<ServiceType> services = null; // TODO resolve branch service workaround :)

    private MenuListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new ModeCallback());

        SwipeListViewTouchListener touchListener = new SwipeListViewTouchListener(lv,
                                                                                  this,
                                                                                  false,
                                                                                  false);
        lv.setOnTouchListener(touchListener);

        initActionBar();
        initPullToRefresh();
        handleIntent(getIntent());
    }

    private void initActionBar()
    {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getActionBar().setSubtitle(getString(R.string.press_to_select));
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
    public void onSwipeLeft(ListView lv, int[] reverseSortedPositions)
    {
        int pos = reverseSortedPositions[0];

        if (null != adapter.getItem(pos) && !adapter.decreaseCounter(pos))
        {
            lv.setItemChecked(pos, false);
        }
    }

    @Override
    public void onSwipeRight(ListView lv, int[] reverseSortedPositions)
    {
        int pos = reverseSortedPositions[0];
        if (null != adapter.getItem(pos))
        {
            adapter.increaseCounter(pos);
            lv.setItemChecked(pos, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (null == branch)
        {
            return true;
        }

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.branch_menu, menu);

        boolean isTable = services.contains(ServiceType.TABLE);
        menu.findItem(R.id.branch_menu_table).setVisible(isTable);

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
                onBackPressed();
                return true;
            case R.id.branch_menu_table:
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        onSwipeRight(l, new int[] { position });
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
        // Get the id from the intent
        String branchId = intent.getExtras().getString(EXTRA_BRANCH_ID);
        ;
        String restId = intent.getExtras().getString(RestaurantActivity.EXTRA_REST_ID);
        if (null == branchId || null == restId)
        {
            setTitle("Can't find branch id");
            Log.e(TAG, "Can't find branch id - null");
            return;
        }

        // Get
        branch = ObjectCashe.get(RestaurantBranchProxy.class, branchId);
        if (null == branch)
        {
            setTitle("Can't find branch");
            Log.e(TAG, "Can't find branch id in ObjectStore: " + branchId);
            return;
        }

        RestaurantProxy rest = ObjectCashe.get(RestaurantProxy.class, restId);
        services = (null != rest) ? rest.getServices() : new ArrayList<ServiceType>();

        ListView branchView = getListView();
        adapter = ObjectCashe.get(MenuListAdapter.class, branch.getId());
        if (null == adapter)
        {
            adapter = new MenuListAdapter(this, branch.getMenu());
            ObjectCashe.put(MenuListAdapter.class, branch.getId(), adapter);
        }
        branchView.setAdapter(adapter);
    }

    private class ModeCallback implements ListView.MultiChoiceModeListener
    {
        private final DecimalFormat df = new DecimalFormat("#.0");

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.branch_course_list_select_menu, menu);

            boolean isTakeAway = services.contains(ServiceType.TAKE_AWAY);
            menu.findItem(R.id.branch_menu_takeaway).setVisible(isTakeAway);

            boolean isDelivery = services.contains(ServiceType.DELIVERY);
            menu.findItem(R.id.branch_menu_delivery).setVisible(isDelivery);

            mode.setTitle("Select Items");
            showTotalCost(mode);
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
                case R.id.branch_view_list_item:
                    mode.finish();
                    break;
                case R.id.branch_menu_takeaway:
                case R.id.branch_menu_delivery:
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            adapter.clearCounters();
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position,
                                              long id,
                                              boolean checked)
        {

            // Select & add 1 to counter
            if (checked && 0 == adapter.getCounter(position))
            {
                onSwipeRight(getListView(), new int[] { position });
            }
            else if (!checked)
            {
                adapter.clearCounter(position);
            }
            
            showTotalCost(mode);
        }

        public void showTotalCost(ActionMode mode)
        {
            // Calculate the total cost to show on action bar
            Double totalCost = 0.0;
            final int checkedCount = getListView().getCheckedItemCount();
            if (0 != checkedCount)
            {
                SparseBooleanArray arr = getListView().getCheckedItemPositions();
                int n = adapter.getCount();
                for (int i = 0; i < n; ++i)
                {
                    if (arr.get(i))
                    {
                        totalCost += adapter.getPrice(i);
                    }
                }
            }

            // Show total cost on action bar
            mode.setSubtitle("Total price: " + df.format(totalCost));

        }
    }

}
