package foodcenter.android.service.restaurant;

import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.CommonUtilities;
import foodcenter.android.ObjectCashe;
import foodcenter.android.activities.rest.RestaurantActivity;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.RestaurantProxy;

public class RestGetAsyncTask extends AsyncTask<String, RestaurantProxy, Void>
{

    private final RestaurantActivity owner;

    public RestGetAsyncTask(RestaurantActivity owner)
    {
        this.owner = owner;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        owner.showSpinner();
    }

    @Override
    protected Void doInBackground(String... restId)
    {
        if (null == restId || restId.length == 0 || null == restId[0])
        {
            return null;
        }
        try
        {
            FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner,
                                                                              FoodCenterRequestFactory.class);

            factory.getClientService()
                .getRestaurantById(restId[0])
                .with(RestaurantProxy.REST_WITH)
                .fire(new RestGetReciever());
        }
        catch (Exception e)
        {
            Log.e("unknown", e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(RestaurantProxy... rest)
    {
        // // find the text view to add the text to.
        owner.hideSpinner();
        
        if (null == rest || rest.length < 1)
        {
            return;
        }

        owner.showRestaurant(rest[0]);

    }

    private class RestGetReciever extends Receiver<RestaurantProxy>
    {
        @Override
        public void onSuccess(RestaurantProxy response)
        {
            publishProgress(response);
            ObjectCashe.put(RestaurantProxy.class, response.getId(), response);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e("req context", error.getMessage());
            CommonUtilities.displayMessage(owner, error.getMessage());

            // Notify PullToRefreshAttacher that the refresh has finished
            owner.hideSpinner();
        }
    }
}