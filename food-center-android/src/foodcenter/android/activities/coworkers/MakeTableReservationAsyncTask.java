package foodcenter.android.activities.coworkers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.data.ReservationData;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.TableReservationProxy;
import foodcenter.service.requset.ClientServiceRequest;

public class MakeTableReservationAsyncTask extends AsyncTask<ReservationData, Void, Void>
{
    private final static String TAG = MakeTableReservationAsyncTask.class.getSimpleName();

    private final Context context;
    private final MakeTableReservationCallback callback;

    public interface MakeTableReservationCallback
    {
        /** callback for {@link MakeTableReservationAsyncTask} when order fails */
        public void onReservationFail(String msg);

        /** callback for {@link MakeTableReservationAsyncTask} when order success */
        public void onReservationSuccess();
    }
    
    public MakeTableReservationAsyncTask(Context context, MakeTableReservationCallback callback)
    {
        super();

        this.context = context;
        this.callback = callback;
    }

    // return error msg or null on success
    @Override
    protected Void doInBackground(ReservationData... data)
    {
        if (null == data || 0 == data.length)
        {
            Log.e(TAG, "null input");
            callback.onReservationFail("Error: null input");
            return null;
        }
        try
        {
            FoodCenterRequestFactory rf = AndroidRequestUtils.getFoodCenterRF(context.getApplicationContext());
            ClientServiceRequest service = rf.getClientService();
            TableReservationProxy reservation = createReservation(service, data[0]);

            service.reserveTable(reservation).fire(new ReservationGetReciever());
        }
        catch (Exception e)
        {
            Log.e(TAG, "if this is a class loader exeption, add retry mechanzim");
            Log.e(TAG, e.getMessage(), e);
            callback.onReservationFail("Error: " + e.getMessage());
        }

        return null;
    }
    
    
    private TableReservationProxy createReservation(ClientServiceRequest service, ReservationData data)
    {
        TableReservationProxy res = service.create(TableReservationProxy.class);
        res.setFromDate(data.getFromDate());
        res.setToDate(data.getToDate());
        res.setUsers(data.getUsers());
        res.setRestBranchId(data.getRestBranchId());
        res.setRestId(data.getRestId());
        return res;
    }

    private class ReservationGetReciever extends Receiver<TableReservationProxy>
    {
        @Override
        public void onSuccess(TableReservationProxy response)
        {
            if (null != response)
            {
                callback.onReservationSuccess();
                return;
            }
            onFailure(new ServerFailure("response is null"));
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            Log.e(TAG, error.getMessage());
            callback.onReservationFail(error.getMessage());
        }
    }

}
