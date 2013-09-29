package foodcenter.android.activities.coworkers;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.ObjectStore;
import foodcenter.android.activities.MsgBroadcastReceiver;
import foodcenter.android.activities.main.RestsGetAsyncTask;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;

public class CoworkersGetAsyncTask extends AsyncTask<Void, String, Exception>
{

    private final static String TAG = RestsGetAsyncTask.class.getSimpleName();

    private final CoworkersActivity activity;
    private final Context context;
    private final ListView lv;

    private final String query = CoworkersGetAsyncTask.class.getName();

    private final boolean isEnabled; 
    
    public CoworkersGetAsyncTask(CoworkersActivity activity, ListView lv, boolean isEnabled)
    {
        super();
        this.activity = activity;
        this.lv = lv;
        this.isEnabled = isEnabled;

        context = activity.getApplicationContext();
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        activity.showSpinner();
    }

    @Override
    protected Exception doInBackground(Void... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(context);

            @SuppressWarnings("unchecked")
            List<String> rests = ObjectStore.get(List.class, query);
            if (null != rests)
            {
                publishProgress(rests.toArray(new String[0]));
                return null;
            }

            factory.getClientService().getCoworkers().fire(new CoworkersGetReciever());
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            return e;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... coworkers)
    {
        // update the view for all the restaurants
        CoworkersListAdapter adapter = new CoworkersListAdapter(activity, coworkers, isEnabled);

        lv.setAdapter(adapter);

        // Notify PullToRefreshAttacher that the refresh has finished
        activity.hideSpinner();
    }

    @Override
    protected void onPostExecute(Exception result)
    {
        if (null != result)
        {
            MsgBroadcastReceiver.toast(context, result.getMessage());
            publishProgress(new String[0]);
        }

        super.onPostExecute(result);
    }

    private class CoworkersGetReciever extends Receiver<List<String>>
    {

        /** Doesn't run on UI thread */
        @Override
        public void onSuccess(List<String> response)
        {
            String[] coworkers = new String[0];
            if (null != response)
            {
                // Save the response in cache!
                ObjectStore.put(List.class, query, response);
                coworkers = response.toArray(coworkers);
            }
            publishProgress(coworkers);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            String msg = error.getMessage();
            Log.e(TAG, msg);
            MsgBroadcastReceiver.toast(context, msg);
            
            publishProgress(new String[0]);
        }
    }
}
