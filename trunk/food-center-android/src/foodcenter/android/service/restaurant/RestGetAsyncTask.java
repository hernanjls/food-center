package foodcenter.android.service.restaurant;

import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.CommonUtilities;
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

                factory.getClientService().getRestaurantById(restId[0]).with(RestaurantProxy.REST_WITH).fire(new RestGetReciever());
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
        //TODO onProgressUpdate
//        // find the text view to add the text to.
//        GridView gridView = (GridView) owner.findViewById(R.id.rest_grid_view);
//
//        // update the view for all the restaurants
//        RestaurantListAdapter adapter = new RestaurantAdapter(owner,
//                                                             RequestUtils.getDefaultDisplayImageOptions(owner),
//                                                             rest[0]);
//
//        gridView.setAdapter(adapter);
        
        // Notify PullToRefreshAttacher that the refresh has finished
        owner.hideSpinner();
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
    }

    private class RestGetReciever extends Receiver<RestaurantProxy>
    {
        @Override
        public void onSuccess(RestaurantProxy response)
        {
            publishProgress(response);
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
