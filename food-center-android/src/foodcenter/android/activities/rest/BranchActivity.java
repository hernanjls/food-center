package foodcenter.android.activities.rest;

import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.android.service.restaurant.MenuListAdapter;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantBranchProxy;

public class BranchActivity extends ListActivity
{

    public static final String EXTRA_BRANCH_ID = "Extra Branch ID";

    private final static String TAG = BranchActivity.class.getSimpleName();

    // this is not pull-able, but helps animating action bar :)
    private PullToRefreshAttacher mPullToRefreshAttacher;

    private static RestaurantBranchProxy branch = null;

    private MenuListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new ModeCallback());

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
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.branch_menu, menu);

        boolean isTable = branch.getServices().contains(ServiceType.TABLE);
        // TODO menu.findItem(R.id.branch_menu_table).setVisible(isTable);

        return true;
    }

    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        getActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed()
    {
        branch = null;
        super.onBackPressed();
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
        String branchId = intent.getExtras().getString(EXTRA_BRANCH_ID);;

        if (null == branchId)
        {
            setTitle("Can't find branch id");
            Log.e(TAG, "Can't find branch id - null");
            return;
        }

        if (null != branch && branch.getId().equals(branchId))
        {
            ObjectStore.getOnce(branchId);
        }
        else
        {
            branch = (RestaurantBranchProxy) ObjectStore.getOnce(branchId);
            if (null == branch)
            {
                setTitle("Can't find branch");
                Log.e(TAG, "Can't find branch id in ObjectStore: " + branchId);
                return;
            }
        }
        
        ListView branchView = getListView();

        adapter = new MenuListAdapter(this, branch.getMenu());

        branchView.setAdapter(adapter);

    }

    private class ModeCallback implements ListView.MultiChoiceModeListener
    {
        private double totalCost = 0;
        
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.branch_course_list_select_menu, menu);

            boolean isTakeAway = branch.getServices().contains(ServiceType.TAKE_AWAY);
            // TODO menu.findItem(R.id.branch_menu_takeaway).setVisible(isTakeAway);

            boolean isDelivery = branch.getServices().contains(ServiceType.DELIVERY);
            // TODO menu.findItem(R.id.branch_menu_delivery).setVisible(isDelivery);

            mode.setTitle("Select Items");
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
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode,
                                              int position,
                                              long id,
                                              boolean checked)
        {

            Double price = adapter.getItem(position).getPrice();
            
            totalCost += (checked) ? price : (-1 * price);
            mode.setSubtitle("Total price: " + totalCost);

        }

    }
}
