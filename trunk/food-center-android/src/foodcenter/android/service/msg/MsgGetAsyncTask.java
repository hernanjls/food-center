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
		this.owner = owner;
	}

	@Override
	protected Void doInBackground(Void... arg0)
	{
		try
		{
			FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner, FoodCenterRequestFactory.class);
			factory.msgService().getMsgs().fire(new MsgGetReciever());
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
		// find the text view to add the text to.
		TextView t = (TextView) owner.findViewById(R.id.main_Text);

		// create a string of all the msgs
		StringBuilder builder = new StringBuilder();
		for (String m : msgs)
		{
			builder.append(m);
			builder.append("\n");
		}

		// add all the msgs to the text view
		t.setText(builder.toString());
	}

	class MsgGetReciever extends Receiver<List<String>>
	{
		@Override
		public void onSuccess(List<String> response)
		{
			publishProgress(response.toArray(new String[0]));
		}

		@Override
		public void onFailure(ServerFailure error)
		{
			Log.e("req context", error.getMessage());
			Popup.show(owner, error.getMessage());
		}
	}

}
