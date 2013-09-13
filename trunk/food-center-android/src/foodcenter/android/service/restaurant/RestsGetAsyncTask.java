package foodcenter.android.service.restaurant;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.AndroidUtils;
import foodcenter.android.ObjectCashe;
import foodcenter.android.R;
import foodcenter.android.activities.main.MainActivity;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.RestaurantProxy;

public class RestsGetAsyncTask extends AsyncTask<String, RestaurantProxy, Void>
{

    private final MainActivity owner;

    private String query = null;
    
    public RestsGetAsyncTask(MainActivity owner)
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
    protected Void doInBackground(String... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(owner);

            if (null == arg0 || arg0.length == 0 || null == arg0[0])
            {
                query = "";
                @SuppressWarnings("unchecked")
                List<RestaurantProxy> rests =  ObjectCashe.get(List.class, query);
                if (null != rests)
                {
                    publishProgress(rests.toArray(new RestaurantProxy[0]));
                    return null;
                }
                factory.getClientService().getDefaultRestaurants().fire(new RestsGetReciever());
            }
            else
            {
                query = arg0[0];
                @SuppressWarnings("unchecked")
                List<RestaurantProxy> rests =  ObjectCashe.get(List.class, query);
                if (null != rests)
                {
                    publishProgress(rests.toArray(new RestaurantProxy[0]));
                    return null;
                }
                factory.getClientService().findRestaurant(query, null).fire(new RestsGetReciever());
            }
        }
        catch (Exception e)
        {
            Log.e("unknown", e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(RestaurantProxy... rests)
    {
        // find the text view to add the text to.
        GridView gridView = (GridView) owner.findViewById(R.id.rest_grid_view);

        // update the view for all the restaurants
        RestaurantListAdapter adapter = new RestaurantListAdapter(owner,
                                                                  AndroidRequestUtils.getDefaultDisplayImageOptions(owner),
                                                                  rests);

        gridView.setAdapter(adapter);
        
        // Notify PullToRefreshAttacher that the refresh has finished
        owner.hideSpinner();
    }

    @Override
    protected void onPostExecute(Void result)
    {
        super.onPostExecute(result);
    }

    private class RestsGetReciever extends Receiver<List<RestaurantProxy>>
    {
        @Override
        public void onSuccess(List<RestaurantProxy> response)
        {
            if (null != response)
            {
                // Save the response in cache!
                ObjectCashe.put(List.class, query, response);
                publishProgress(response.toArray(new RestaurantProxy[0]));
            }
            else
            {
                publishProgress(new RestaurantProxy[] {});
            }
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e("req context", error.getMessage());
            AndroidUtils.displayMessage(owner, error.getMessage());
            
            // Notify PullToRefreshAttacher that the refresh has finished
            owner.hideSpinner();
        }
    }
}
