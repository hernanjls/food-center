package foodcenter.android.service.msg;

import java.util.List;


import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.Popup;
import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;

public class MsgGetAsyncTask extends AsyncTask<Void, String, Void>
{

    private final Activity owner;
    
    public MsgGetAsyncTask(Activity owner)
    {
        this.owner= owner;
    }
    
    /**
     *  publish the msg to the ui thread
     * @param msg is the msg to publish
     */
    public void publishProxyResult(String...  msgs)
    {
        this.publishProgress(msgs);
    }
    
    @Override
    protected Void doInBackground(Void... arg0)
    {
        try
        {
            FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner, FoodCenterRequestFactory.class);
            factory.msgService().getMsgs().fire(new MsgGetReciever(this));
        }
        catch (Exception e)
        {
            Log.e("unknown", e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    protected void onProgressUpdate(String... msgs)
    {
        Boolean b = Boolean.parseBoolean(msgs[0]);
        if (!b)
        {
            Popup.show(owner, msgs[1]);
            return;
        }
        
        //find the text view to add the text to.
        TextView t=(TextView)owner.findViewById(R.id.main_Text);
        
        //create a string of all the msgs
        StringBuilder builder = new StringBuilder();
        for (int i=1; i< msgs.length; ++i)
        {
            builder.append(msgs[i]);
            builder.append("\n");
        }
        
        //add all the msgs to the text view
        t.setText(builder.toString());
    }

    @Override
    protected void onPostExecute(Void result)
    {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }
    
}



class MsgGetReciever extends Receiver<List<String>>
{
    private final MsgGetAsyncTask owner;
    
    public MsgGetReciever(MsgGetAsyncTask owner)
    {
        this.owner = owner;
    }
    
    @Override
    public void onSuccess(List<String> response)
    {
        response.add(0, "true");
        
        owner.publishProxyResult(response.toArray(new String[0]));
    }

    @Override
    public void onFailure(ServerFailure error)
    {
        //don't call super and throw runtime exception
        Log.e("req context", error.getMessage());
        String[] res = {"false", error.getMessage()};
        owner.publishProxyResult(res);
    }
}


