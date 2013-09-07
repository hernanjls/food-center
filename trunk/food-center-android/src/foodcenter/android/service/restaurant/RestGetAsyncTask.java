package foodcenter.android.service.restaurant;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.nostra13.universalimageloader.core.ImageLoader;

import foodcenter.android.CommonUtilities;
import foodcenter.android.R;
import foodcenter.android.activities.rest.RestaurantActivity;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.RestaurantBranchProxy;
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

        ListView branchesListView = (ListView) owner.findViewById(R.id.rest_branch_list);

        RestaurantProxy r = rest[0];
        // update the view for all the restaurant branches
        List<RestaurantBranchProxy> branches = r.getBranches();
        if (null != branches)
        {
            RestaurantBranchProxy[] branchesArray = new RestaurantBranchProxy[branches.size()];
            rest[0].getBranches().toArray(branchesArray);
            BranchListAdapter adapter = new BranchListAdapter(owner, branchesArray);

            branchesListView.setAdapter(adapter);
        }

        // Set action bar and activity title
        owner.setTitle(r.getName());

        // Load the image of this restaurant
        ImageView imageView = (ImageView) owner.findViewById(R.id.rest_info_img);
        String url = RequestUtils.getBaseUrl(owner) + r.getImageUrl();

        ImageLoader.getInstance().displayImage(url,
                                               imageView,
                                               RequestUtils.getDefaultDisplayImageOptions(owner));
        
        // TODO Load the info of this restaurant
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
