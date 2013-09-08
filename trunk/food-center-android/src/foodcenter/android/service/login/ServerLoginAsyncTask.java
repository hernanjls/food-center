package foodcenter.android.service.login;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.CommonUtilities;
import foodcenter.android.GCMIntentService;
import foodcenter.android.R;
import foodcenter.android.activities.main.LoginActivity;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.requset.ClientServiceRequest;

/**
 * 
 * registering gcm service with gcm regId on food-center-server <br>
 * dont forget to run execute(); <br>
 * 
 */
public class ServerLoginAsyncTask extends Receiver<UserProxy>
{
    // private static final String TAG = GCMRegistrar.class.getSimpleName();

    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private final String regId;     // GCM reg id
    private int attempt;
    private long backoff;

    private final Context context;

    public ServerLoginAsyncTask(final Context context, String regId, int attempt)
    {
        this.context = context;
        this.regId = regId;
        this.attempt = attempt;
        this.backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
    }

    public void execute()
    {
        FoodCenterRequestFactory factory = RequestUtils
            .getRequestFactory(context, FoodCenterRequestFactory.class);
        ClientServiceRequest service = factory.getClientService();

        service.login(regId).fire(this);
    }

    @Override
    public void onSuccess(UserProxy user)
    {
        GCMRegistrar.setRegisteredOnServer(context, true);
        String message = context.getString(R.string.server_registered);
        CommonUtilities.displayMessage(context, message);

        LoginActivity.closeLoginActivity(true);
    }

    @Override
    public void onFailure(ServerFailure error)
    {
        try
        {
            if (attempt > 0)
            {
                Log.d(GCMIntentService.TAG, "Sleeping for " + backoff + " ms before retry");
                Thread.sleep(backoff);
                this.attempt--;
                this.backoff *= 2;
                execute();
                return;
            }

            // At this point all attempts to register with the app
            // server failed, so we need to unregister the device
            // from GCM - the app will try to register again when
            // it is restarted. Note that GCM will send an
            // unregistered callback upon completion, but
            // GCMIntentService.onUnregistered() will ignore it.
            String msg = context.getString(R.string.server_register_error,
                                           GCMIntentService.MAX_ATTEMPTS);

            CommonUtilities.displayMessage(context, msg);

            GCMRegistrar.unregister(context);
        }

        catch (InterruptedException e)
        {
            // Activity finished before we complete - exit.
            Log.d(GCMIntentService.TAG, "Thread interrupted: abort remaining retries!");
            Thread.currentThread().interrupt();
            return;
        }
    }
}
