package foodcenter.android.service.history;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.AndroidUtils;
import foodcenter.android.ObjectStore;
import foodcenter.android.activities.history.OrderHistoryActivity;
import foodcenter.android.activities.history.OrderHistoryListAdapter;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.OrderProxy;

public class OrderHistoryGetAsyncTask extends AsyncTask<Integer, OrderProxy, String>
{

    private final static String TAG = OrderHistoryGetAsyncTask.class.getSimpleName();

    private final OrderHistoryActivity activity;
    private final OrderHistoryListAdapter adapter;

    private String query = null;

    public OrderHistoryGetAsyncTask(OrderHistoryActivity activity, OrderHistoryListAdapter adapter)
    {
        super();
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        activity.showSpinner();
    }

    @Override
    protected String doInBackground(Integer... toFrom)
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
            return e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String msg)
    {
        if (null != msg)
        {
            AndroidUtils.displayMessage(activity, msg);
        }
        
        super.onPostExecute(msg);
    }

    @Override
    protected void onProgressUpdate(OrderProxy... rests)
    {
        adapter.addOrders(rests);

        // Notify PullToRefreshAttacher that the refresh has finished
        activity.hideSpinner();
    }

    private class OrdersGetReciever extends Receiver<List<OrderProxy>>
    {
        @Override
        public void onSuccess(List<OrderProxy> response)
        {
            OrderProxy[] orders = new OrderProxy[0];
            if (null != response)
            {
                // Save the response in cache!
                ObjectStore.put(List.class, query, response);
                orders = response.toArray(orders); 
            }
            
            publishProgress(orders);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e("req context", error.getMessage());
            AndroidUtils.displayMessage(activity, error.getMessage());

            publishProgress(new OrderProxy[0]);
        }
    }
}
