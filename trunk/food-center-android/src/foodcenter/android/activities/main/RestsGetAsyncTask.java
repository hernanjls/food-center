package foodcenter.android.activities.main;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.AndroidUtils;
import foodcenter.android.ObjectStore;
import foodcenter.android.R;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.RestaurantProxy;

public class RestsGetAsyncTask extends AsyncTask<String, RestaurantProxy, Exception>
{

    private final static String TAG = RestsGetAsyncTask.class.getSimpleName();

    private final MainActivity owner;

    private String query = null;

    public RestsGetAsyncTask(MainActivity owner)
    {
        super();
        this.owner = owner;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        owner.showSpinner();
    }

    @Override
    protected Exception doInBackground(String... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(owner);

            query = (null == arg0 || arg0.length == 0 || null == arg0[0]) ? "" : arg0[0];

            @SuppressWarnings("unchecked")
            List<RestaurantProxy> rests = ObjectStore.get(List.class, query);
            if (null != rests)
            {
                publishProgress(rests.toArray(new RestaurantProxy[0]));
                return null;
            }

            factory.getClientService().findRestaurant(query, null).fire(new RestsGetReciever());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return e;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(RestaurantProxy... rests)
    {
        // Notify PullToRefreshAttacher that the refresh has finished
        owner.hideSpinner();
        
        if (null == rests || rests.length <= 0)
        {
            return;
        }
        
        // find the text view to add the text to.
        GridView gridView = (GridView) owner.findViewById(R.id.rest_grid_view);

        // update the view for all the restaurants
        MainRestListAdapter adapter = new MainRestListAdapter(owner,
                                                              AndroidRequestUtils.getDefaultDisplayImageOptions(owner),
                                                              rests);

        gridView.setAdapter(adapter);
    }

    @Override
    protected void onPostExecute(Exception result)
    {
        if (null != result)
        {
            AndroidUtils.toast(owner, result.getMessage());
            publishProgress(new RestaurantProxy[0]);
        }

        super.onPostExecute(result);
    }

    private class RestsGetReciever extends Receiver<List<RestaurantProxy>>
    {
        /** Doesn't run on UI thread */
        @Override
        public void onSuccess(List<RestaurantProxy> response)
        {
            RestaurantProxy[] rests = new RestaurantProxy[0];
            if (null != response)
            {
                // Save the response in cache!
                ObjectStore.put(List.class, query, response);
                rests = response.toArray(new RestaurantProxy[0]);
            }
            publishProgress(rests);
        }

        /** Doesn't run on UI thread */
        @Override
        public void onFailure(ServerFailure error)
        {
            String msg = "type: " + error.getExceptionType() + " , msg: " + error.getMessage();
            if (error.getMessage().equals(AndroidRequestUtils.SERVER_ERROR_COOKIE_AUTH))
            {
                msg = "Authentication error, please logout and re-login...";
                //TODO re-auth here ?
            }
            
            Log.e(RestsGetReciever.class.getSimpleName(), msg);
            AndroidUtils.toast(owner, msg);
            publishProgress();
        }
    }
}
