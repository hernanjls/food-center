package foodcenter.android.service.user;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.AndroidUtils;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.activities.main.MainActivity;
import foodcenter.android.activities.user.OrderHistoryActivity;
import foodcenter.android.adapters.RestaurantListAdapter;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantProxy;

public class OrderHistoryAsyncTask extends AsyncTask<Integer, OrderProxy, Void>
{

    private final static String TAG = OrderHistoryAsyncTask.class.getSimpleName();

    private final OrderHistoryActivity activity;

    private String query = null;

    public OrderHistoryAsyncTask(OrderHistoryActivity activity)
    {
        super();
        this.activity = activity;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        activity.showSpinner();
    }

    @Override
    protected Void doInBackground(Integer... toFrom)
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(activity);

            if (null == toFrom || toFrom.length < 1 || null == toFrom[0])
            {
                return null;
            }
            Integer to = toFrom[0];
            Integer from = toFrom[1];
            if (null == from || from > to)
            {
                from = 0;
            }

            factory.getClientService()
                .getOrders(from, to)
                .with(OrderProxy.ORDER_WITH)
                .fire(new OrdersGetReciever());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(OrderProxy... rests)
    {
//        // find the text view to add the text to.
//        ListView lv = (ListView) activity.findViewById(R.id.orders_list_view);
//
//        // update the view for all the restaurants
//        RestaurantListAdapter adapter = new HistoryListAdapter(activity,
//                                                               AndroidRequestUtils.getDefaultDisplayImageOptions(owner),
//                                                               rests);
//
//        lv.setAdapter(adapter);

        // Notify PullToRefreshAttacher that the refresh has finished
        activity.hideSpinner();
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
    }

    private class OrdersGetReciever extends Receiver<List<OrderProxy>>
    {
        @Override
        public void onSuccess(List<OrderProxy> response)
        {
            if (null != response)
            {
                // Save the response in cache!
                ObjectStore.put(List.class, query, response);
                publishProgress(response.toArray(new OrderProxy[0]));
            }
            else
            {
                publishProgress(new OrderProxy[] {});
            }
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e("req context", error.getMessage());
            AndroidUtils.displayMessage(activity, error.getMessage());

            // Notify PullToRefreshAttacher that the refresh has finished
            activity.hideSpinner();
        }
    }
}
