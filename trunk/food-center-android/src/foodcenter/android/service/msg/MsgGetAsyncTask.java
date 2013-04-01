package foodcenter.android.service.msg;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.MainActivity;
import foodcenter.android.Popup;
import foodcenter.android.R;
import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.MsgProxy;

public class MsgGetAsyncTask extends AsyncTask<Void, MsgProxy, Void>
{

	private final MainActivity owner;

	@Override
	protected void onPreExecute()
	{
	    super.onPreExecute();
	    owner.showSpinner("Loading msges from server...");
	}
	public MsgGetAsyncTask(MainActivity owner)
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
	protected void onProgressUpdate(MsgProxy... msgs)
	{
		// find the text view to add the text to.
		ListView listView = (ListView) owner.findViewById(R.id.select_message);

		// create a string of all the msgs
		ArrayList<MsgTextView> msgsArray = new ArrayList<MsgTextView>();
		for (MsgProxy m : msgs)
		{
			msgsArray.add(new MsgTextView(m));
		}

		ArrayAdapter<MsgTextView> adapter = new ArrayAdapter<MsgTextView>(owner, R.layout.msg, msgsArray);
		listView.setAdapter(adapter);

		// add all the msgs to the text view
		// t.addTouchables(views);
		listView.setOnItemClickListener(new MsgItemClickListener(msgsArray));
	}

	@Override
	protected void onPostExecute(Void result)
	{
	    owner.hideSpinner();
	    super.onPostExecute(result);
	}
	
	class MsgGetReciever extends Receiver<List<MsgProxy>>
	{
		@Override
		public void onSuccess(List<MsgProxy> response)
		{
			publishProgress(response.toArray(new MsgProxy[0]));
		}

		@Override
		public void onFailure(ServerFailure error)
		{
			Log.e("req context", error.getMessage());
			Popup.show(owner, error.getMessage());
		}
	}

	class MsgTextView
	{

		private final MsgProxy msg;

		public MsgTextView(MsgProxy msg)
		{
			this.msg = msg;
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("sender: ");
			builder.append(getMsg().getEmail());
			builder.append(", msg: ");
			builder.append(getMsg().getMsg());
			return builder.toString();
		}

		public MsgProxy getMsg()
        {
	        return msg;
        }
	}

	class MsgItemClickListener implements OnItemClickListener
	{
		private final ArrayList<MsgTextView> msgsArray;
		public MsgItemClickListener(ArrayList<MsgTextView> msgsArray)
		{
			this.msgsArray = msgsArray;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			owner.showSpinner("Pressed on item " + position);
			@SuppressWarnings("unchecked")
            ArrayAdapter<MsgTextView> adapter = (ArrayAdapter<MsgTextView>) parent.getAdapter();
			new MsgDeleteAsyncTask(owner, adapter, msgsArray).execute(position);
		}
	}
}
