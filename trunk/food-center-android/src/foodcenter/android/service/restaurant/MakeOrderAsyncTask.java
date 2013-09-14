package foodcenter.android.service.restaurant;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.activities.rest.OrderConfActivity;
import foodcenter.android.data.OrderConfData;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.ClientServiceRequest;

public class MakeOrderAsyncTask extends AsyncTask<OrderConfData, String, String>
{
    public static final int MAX_ATTEMPS = 10;

    private final static String TAG = MakeOrderAsyncTask.class.getSimpleName();

    private OrderConfActivity activity;

    private final int attempt;

    public MakeOrderAsyncTask(OrderConfActivity activity, int attempt)
    {
        super();

        this.activity = activity;
        this.attempt = attempt;
    }

    // return error msg or null on success
    @Override
    protected String doInBackground(OrderConfData... data)
    {
        try
        {
            if (0 != attempt)
            {
                Thread.sleep(100);
            }
            FoodCenterRequestFactory rf = AndroidRequestUtils.getFoodCenterRF(activity.getApplicationContext());
            ClientServiceRequest service = rf.getClientService();
            OrderProxy order = createOrder(service, data[0]);

            service.makeOrder(order).fire(new OrderGetReciever());
        }
        catch (Exception e)
        {
            Log.e(TAG, "if this is a class loader exeption: there is a retry mechanizm");
            Log.e(TAG, e.getMessage());
            return e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (null != result)
        {
            activity.orderFail(result, true, attempt);
        }
        super.onPostExecute(result);
    }
    
    
    private OrderProxy createOrder(ClientServiceRequest service, OrderConfData data)
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
            activity.orderSuccess();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e(TAG, error.getMessage());
            activity.orderFail(error.getMessage(), false, attempt);
        }
    }

}
