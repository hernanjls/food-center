package foodcenter.android.activities.coworkers;

import java.util.List;
import java.util.Map;

import android.app.Activity;
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

    public interface CoworkersGetCallback
    {
        public void showSpinner();
        
        public void hideSpinner();
    }

    private final static String TAG = RestsGetAsyncTask.class.getSimpleName();

    private final Activity activity;
    private final CoworkersGetCallback callback;
    private final ListView lv;

    private final static String usersKeyStore = CoworkersGetAsyncTask.class.getName() + "_USERS";

    private final boolean isEnabled; 
    
    
    
    public CoworkersGetAsyncTask(Activity activity, CoworkersGetCallback callback, ListView lv, boolean isEnabled)
    {
        super();
        this.activity = activity;
        this.callback = callback;
        this.lv = lv;
        this.isEnabled = isEnabled;

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        callback.showSpinner();
    }

    @Override
    protected Exception doInBackground(Void... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(activity);

            @SuppressWarnings("unchecked")
            List<String> rests = ObjectStore.get(List.class, usersKeyStore);
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
        
        if (isEnabled)
        {
            @SuppressWarnings("unchecked")
            Map<Integer, Boolean> selected = ObjectStore.get(Map.class, CoworkersActivity.SELECTED_KEY);
            for (int pos : selected.keySet())
            {
                lv.setItemChecked(pos, selected.get(pos));
            }
        }
        // Notify PullToRefreshAttacher that the refresh has finished
        callback.hideSpinner();
    }

    @Override
    protected void onPostExecute(Exception result)
    {
        if (null != result)
        {
            MsgBroadcastReceiver.toast(activity, result.getMessage());
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
                ObjectStore.put(List.class, usersKeyStore, response);
                coworkers = response.toArray(coworkers);
            }
            publishProgress(coworkers);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            String msg = error.getMessage();
            Log.e(TAG, msg);
            MsgBroadcastReceiver.toast(activity, msg);
            
            publishProgress(new String[0]);
        }
    }
}
