package foodcenter.android.service.msg;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.Popup;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;

public class MsgAddAsyncTask extends AsyncTask<Void, String, Void>
{

	private final Activity owner;
	private final String msg;
	    
	public MsgAddAsyncTask(Activity owner, String msg)
	{
		this.owner= owner;
		this.msg = msg;
	}
	
	/**
	 *  publish the msg to the ui thread
	 * @param msg is the msg to publish
	 */
	public void publishProxyResult(String... msgs)
	{
	    this.publishProgress(msgs);
	}
	
	@Override
	protected Void doInBackground(Void... arg0)
	{
		try
		{
			FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner, FoodCenterRequestFactory.class);
			factory.msgService().createMsg(msg).fire(new MsgAddReciever(this));
		}
		catch (Exception e)
		{
			Log.e("unknown", e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... values)
	{
	    String msg = values[0];
	    Popup.show(owner, msg);
	    new MsgGetAsyncTask(owner).execute();
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    // TODO Auto-generated method stub
	    super.onPostExecute(result);
	}
	
}



class MsgAddReciever extends Receiver<Void>
{
	private final MsgAddAsyncTask owner;
	
	public MsgAddReciever(MsgAddAsyncTask owner)
	{
		this.owner = owner;
	}
	
	@Override
	public void onSuccess(Void response)
	{
		owner.publishProxyResult("success");
	}

	@Override
	public void onFailure(ServerFailure error)
	{
		//don't call super and throw runtime exception
	    Log.e("req context", error.getMessage());
		owner.publishProxyResult(error.getMessage());
	}
}


