package foodcenter.android.activities.coworkers;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.ObjectStore;
import foodcenter.android.activities.MsgBroadcastReceiver;
import foodcenter.android.activities.main.RestsGetAsyncTask;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;

public class CoworkersGetRunnable extends Receiver<List<String>> implements Runnable
{

    public interface CoworkersGetCallback
    {
        /** it will be called from non-UI thread */
        public void onSuccessGetCoworkers(final List<String> coworkers);

        /** it will be called from non-UI thread */
        public void onFailGetCoworkers(final String msg);
    }

    private final static String TAG = RestsGetAsyncTask.class.getSimpleName();

    private final Context context;
    private final CoworkersGetCallback callback;

    private final static String usersKeyStore = CoworkersGetRunnable.class.getName() + "_USERS";

    public CoworkersGetRunnable(Context context, CoworkersGetCallback callback)
    {
        super();
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void run()
    {
        try
        {
            FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(context);

            @SuppressWarnings("unchecked")
            List<String> coworkers = ObjectStore.get(List.class, usersKeyStore);
            if (null != coworkers)
            {
                callback.onSuccessGetCoworkers(coworkers);
                return;
            }

            factory.getClientService().getCoworkers().fire(this);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
            callback.onFailGetCoworkers(e.getMessage());
        }
    }

    /** Doesn't run on UI thread */
    @Override
    public void onSuccess(List<String> response)
    {
        if (null != response)
        {
            // Save the response in cache!
            ObjectStore.put(List.class, usersKeyStore, response);
            callback.onSuccessGetCoworkers(response);
            return;
        }
        callback.onFailGetCoworkers("null response");
    }

    /** Doesn't run on UI thread */
    @Override
    public void onFailure(ServerFailure error)
    {
        String msg = error.getMessage();
        Log.e(TAG, msg);
        MsgBroadcastReceiver.toast(context, msg);

        callback.onFailGetCoworkers(msg);
    }

}
