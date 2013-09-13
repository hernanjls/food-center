package foodcenter.android.activities.rest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.activities.helpers.OrderConfData;
import foodcenter.android.adapters.MenuListAdapter;
import foodcenter.android.adapters.SwipeListViewTouchListener;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

public class BranchActivity extends ListActivity implements
                                                SwipeListViewTouchListener.OnSwipeCallback
{

    public final static int REQ_CODE_ORDER = 1;
    public final static String EXTRA_BRANCH_ID = "Extra Branch ID";

    private final static String TAG = BranchActivity.class.getSimpleName();

    private RestaurantBranchProxy branch = null;
    private String restId = null;
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
        handleIntent(getIntent());
    }

    private void initActionBar()
    {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getActionBar().setSubtitle(getString(R.string.press_to_select));
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        onSwipeRight(l, new int[] { position });
    }

    private void OpenOrderVerification(ServiceType service)
    {
        Intent intent = new Intent(this, OrderConfActivity.class);

        // restId received from intent
        OrderConfData data = adapter.getOrderConfData(service);
        data.setRestBranchId(branch.getId());
        data.setRestId(restId);
        ObjectStore.put(OrderConfData.class, OrderConfData.CACHE_KEY, data);

        startActivity(intent);
    }

    private void handleIntent(Intent intent)
    {
        // Get the ids from the intent
        String branchId = intent.getExtras().getString(EXTRA_BRANCH_ID);
        restId = intent.getExtras().getString(RestaurantActivity.EXTRA_REST_ID);

        if (null == branchId || null == restId)
        {
            setTitle("Can't find branch id");
            Log.e(TAG, "Can't find branch id - null");
            return;
        }

        // Get
        branch = ObjectStore.get(RestaurantBranchProxy.class, branchId);
        if (null == branch)
        {
            setTitle("Can't find branch");
            Log.e(TAG, "Can't find branch id in ObjectStore: " + branchId);
            return;
        }

        RestaurantProxy rest = ObjectStore.get(RestaurantProxy.class, restId);
        services = (null != rest) ? rest.getServices() : new ArrayList<ServiceType>();

        ListView branchView = getListView();
        adapter = ObjectStore.get(MenuListAdapter.class, branch.getId());
        if (null == adapter)
        {
            adapter = new MenuListAdapter(this, branch.getMenu());
            ObjectStore.put(MenuListAdapter.class, branch.getId(), adapter);
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
            showTotalPrice(mode);
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
                    // mode.finish();
                    break;
                case R.id.branch_menu_delivery:
                    // mode.finish();
                    OpenOrderVerification(ServiceType.DELIVERY);
                    break;
                case R.id.branch_menu_takeaway:
                    // mode.finish();
                    OpenOrderVerification(ServiceType.TAKE_AWAY);
                    break;
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

            showTotalPrice(mode);
        }

        private void showTotalPrice(ActionMode mode)
        {
            String s = getString(R.string.total_price) + " " + df.format(adapter.getTotalPrice());

            // Show total price on action bar
            mode.setSubtitle(s);
        }
    }

}
