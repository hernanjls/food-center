package foodcenter.android.service.restaurant;

import java.util.List;

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

public class RestsGetAsyncTask extends AsyncTask<Void, RestaurantProxy, Void>
{

    private final MainActivity owner;

    public RestsGetAsyncTask(MainActivity owner)
    {
        this.owner = owner;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        owner.showSpinner("Loading restaurants from server...");
    }

    @Override
    protected Void doInBackground(Void... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner,
                                                                              FoodCenterRequestFactory.class);
            factory.getClientService().getDefaultRestaurants().fire(new RestsGetReciever());
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
        GridView gridView = (GridView) owner.findViewById(R.id.rests_gridview);

        // update the view for all the restaurants
        RestaurantListAdapter adapter = new RestaurantListAdapter(owner,
                                                                  RequestUtils.getDefaultDisplayImageOptions(owner),
                                                                  rests);
        gridView.setAdapter(adapter);
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
            publishProgress(response.toArray(new RestaurantProxy[0]));
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e("req context", error.getMessage());
            owner.showSpinner(error.getMessage());
        }
    }
}
