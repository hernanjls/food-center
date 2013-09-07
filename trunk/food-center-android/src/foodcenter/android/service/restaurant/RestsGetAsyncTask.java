package foodcenter.android.service.restaurant;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.MainActivity;
import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.RestaurantProxy;

public class RestsGetAsyncTask extends AsyncTask<String, RestaurantProxy, Void>
{

    private final MainActivity owner;
    private final PullToRefreshAttacher pullToRefreshAttacher;
    public RestsGetAsyncTask(MainActivity owner, PullToRefreshAttacher pullToRefreshAttacher)
    {
        this.owner = owner;
        this.pullToRefreshAttacher = pullToRefreshAttacher;        
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        owner.showSpinner("Loading restaurants from server...");
    }

    @Override
    protected Void doInBackground(String... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner,
                                                                              FoodCenterRequestFactory.class);

            if (null == arg0 || arg0.length == 0 || null == arg0[0])
            {
                factory.getClientService().getDefaultRestaurants().fire(new RestsGetReciever());
            }
            else
            {
                String query = arg0[0];
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
                                                                  RequestUtils.getDefaultDisplayImageOptions(owner),
                                                                  rests);

        gridView.setAdapter(adapter);
        
        // Notify PullToRefreshAttacher that the refresh has finished
        if (null != pullToRefreshAttacher)
        {
            pullToRefreshAttacher.setRefreshComplete();
        }

    }

    @Override
    protected void onPostExecute(Void result)
    {
        owner.hideSpinner();
        super.onPostExecute(result);
    }

    private class RestsGetReciever extends Receiver<List<RestaurantProxy>>
    {
        @Override
        public void onSuccess(List<RestaurantProxy> response)
        {
            if (null != response)
            {
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
            owner.showSpinner(error.getMessage());
            
            // Notify PullToRefreshAttacher that the refresh has finished
            if (null != pullToRefreshAttacher)
            {
                pullToRefreshAttacher.setRefreshComplete();
            }
        }
    }
}
