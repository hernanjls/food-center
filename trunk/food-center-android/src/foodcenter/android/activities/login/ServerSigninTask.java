package foodcenter.android.activities.login;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.AndroidUtils;
import foodcenter.android.GCMIntentService;
import foodcenter.android.R;
import foodcenter.android.activities.MsgBroadcastReceiver;
import foodcenter.android.service.AndroidRequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.requset.ClientServiceRequest;

/**
 * 
 * Registering GCM service with GCM regId on food-center-server <br>
 * Don't forget to run signIn(); <br>
 * 
 * must run on non-UI thread.
 * 
 */
public class ServerSigninTask extends Receiver<UserProxy>
{
    // private static final String TAG = GCMRegistrar.class.getSimpleName();
    
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private final Context context;
    private final String regId; // GCM reg id
    private int attempt;
    
    
    private long backoff;

    public ServerSigninTask(final Context context, String regId, int attempt)
    {
        super();
        this.context = context;
        this.regId = regId;
        this.attempt = attempt;
        
        backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
    }

    /** it actually retrieves the RF on the UI thread, but fire is on other thread */
    public void signIn()
    {
        FoodCenterRequestFactory factory = AndroidRequestUtils.getFoodCenterRF(context);
        ClientServiceRequest service = factory.getClientService();

        service.login(regId).fire(this);
    }

    @Override
    public void onSuccess(UserProxy user)
    {
        GCMRegistrar.setRegisteredOnServer(context, true);
        String msg = context.getString(R.string.server_registered);
        AndroidUtils.notifySignIn(context, msg);
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
                signIn();
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

            MsgBroadcastReceiver.progress(context, msg);

            GCMRegistrar.unregister(context);
        }

        catch (InterruptedException e)
        {
            String msg = "Thread interrupted: abort remaining retries!"; //TODO change to R.string
            // Activity finished before we complete - exit.
            Log.d(GCMIntentService.TAG, msg);
            
            MsgBroadcastReceiver.progressDismissAndToastMsg(context, msg);
        }
    }
}
