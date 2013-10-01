package foodcenter.android.activities.order;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.data.OrderData;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.ClientServiceRequest;

public class MakeOrderAsyncTask extends AsyncTask<OrderData, String, String>
{
    public static final int MAX_ATTEMPS = 10;

    private final static String TAG = MakeOrderAsyncTask.class.getSimpleName();

    private final Context context;
    private final MakeOrderCallback callback;
    private final int attempt;

    public interface MakeOrderCallback
    {
        /** callback for {@link MakeOrderAsyncTask} when order fails */
        public void onOrderFail(String msg, boolean retry, int attempt);

        /** callback for {@link MakeOrderAsyncTask} when order success */
        public void onOrderSuccess();
    }
    
    public MakeOrderAsyncTask(Context context, MakeOrderCallback callback, int attempt)
    {
        super();

        this.context = context;
        this.callback = callback;
        this.attempt = attempt;
    }

    // return error msg or null on success
    @Override
    protected String doInBackground(OrderData... data)
    {
        try
        {
            if (0 != attempt)
            {
                Thread.sleep(100);
            }
            FoodCenterRequestFactory rf = AndroidRequestUtils.getFoodCenterRF(context.getApplicationContext());
            ClientServiceRequest service = rf.getClientService();
            OrderProxy order = createOrder(service, data[0]);

            service.makeOrder(order).fire(new OrderGetReciever());
        }
        catch (Exception e)
        {
            Log.e(TAG, "if this is a class loader exeption: there is a retry mechanizm");
            Log.e(TAG, e.getMessage(), e);
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (null != result)
        {
            callback.onOrderFail(result, true, attempt);
        }
        super.onPostExecute(result);
    }
    
    
    private OrderProxy createOrder(ClientServiceRequest service, OrderData data)
    {

        OrderProxy res = service.create(OrderProxy.class);
        res.setCourses(new ArrayList<CourseOrderProxy>());

        int n = data.getCourses().size();
        for (int i = 0; i < n; ++i)
        {
            CourseProxy c = data.getCourses().get(i);
            CourseOrderProxy co = service.create(CourseOrderProxy.class);
            co.setCourseId(c.getId());
            co.setCnt(data.getCounters().get(i));
            co.setInfo(c.getInfo());
            co.setPrice(c.getPrice());
            co.setName(c.getName());
            res.getCourses().add(co);
        }

        res.setRestBranchId(data.getRestBranchId());
        res.setRestId(data.getRestId());
        res.setService(data.getService());
        return res;
    }

    private class OrderGetReciever extends Receiver<OrderProxy>
    {
        @Override
        public void onSuccess(OrderProxy response)
        {
            callback.onOrderSuccess();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e(TAG, error.getMessage());
            callback.onOrderFail(error.getMessage(), false, attempt);
        }
    }

}
