package foodcenter.android.service.restaurant;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.AndroidUtils;
import foodcenter.android.activities.helpers.OrderConfData;
import foodcenter.android.activities.rest.OrderConfActivity;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.CourseOrderProxy;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.requset.ClientServiceRequest;

public class MakeOrderAsyncTask extends AsyncTask<OrderConfData, OrderProxy, Void>
{

    private final static String TAG = MakeOrderAsyncTask.class.getSimpleName();
    
    private OrderConfActivity activity;
    
    public MakeOrderAsyncTask(OrderConfActivity activity)
    {
        this.activity = activity;
    }
    
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        activity.showSpinner();
    }

    @Override
    protected Void doInBackground(OrderConfData... data)
    {
        FoodCenterRequestFactory rf = AndroidRequestUtils.getFoodCenterRF(activity);
        ClientServiceRequest service = rf.getClientService();
        OrderProxy order = createOrder(service, data[0]);
        
        service.makeOrder(order).fire(new OrderGetReciever());
        
        return null;
    }
    
    private OrderProxy createOrder(ClientServiceRequest service, OrderConfData data)
    {
        
        OrderProxy res = service.create(OrderProxy.class);
        res.setCourses(new ArrayList<CourseOrderProxy>());
        
        int n = data.getCourses().size();
        for (int i=0; i< n; ++i)
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
            activity.hideSpinner();
            AndroidUtils.displayMessage(activity, error.getMessage());
        }
    }


}
