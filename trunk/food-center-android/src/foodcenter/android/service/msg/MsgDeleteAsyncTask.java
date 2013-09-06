//package foodcenter.android.service.msg;
//
//import java.util.ArrayList;
//
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//
//import com.google.web.bindery.requestfactory.shared.Receiver;
//import com.google.web.bindery.requestfactory.shared.ServerFailure;
//
//import foodcenter.android.MainActivity;
//import foodcenter.android.service.RequestUtils;
//import foodcenter.android.service.restaurant.RestBranchesGetAsyncTask.MsgTextView;
//import foodcenter.service.FoodCenterRequestFactory;
//import foodcenter.service.msg.MsgProxy;
//
//public class MsgDeleteAsyncTask extends AsyncTask<Integer, String, Void>
//{
//
//	private final static String TAG = MsgDeleteAsyncTask.class.getSimpleName();
//
//	private final static String HIDE_SPINNER = "hide spinner";
//	private final static String SUCCESS = "success";
//	private final MainActivity owner;
//	private final ArrayAdapter<MsgTextView> adapter;
//	private final ArrayList<MsgTextView> msgsArray;
//
//	public MsgDeleteAsyncTask(MainActivity owner, ArrayAdapter<MsgTextView> adapter, ArrayList<MsgTextView> msgsArray)
//	{
//		this.owner = owner;
//		this.adapter = adapter;
//		this.msgsArray = msgsArray;
//	}
//
//	@Override
//	protected void onPreExecute()
//	{
//		owner.showSpinner("Deleting msg");
//	}
//
//	@Override
//	protected void onProgressUpdate(String... values)
//	{
//		String msg = values[0];
//		if (HIDE_SPINNER.equals(msg))
//		{
//			owner.hideSpinner();
//		}
//		else if (SUCCESS.equals(msg))
//		{
//			adapter.notifyDataSetChanged();
//			owner.hideSpinner();
//		}
//		else
//		{
//			owner.showSpinner(msg);
//		}
//	}
//
//	@Override
//	protected Void doInBackground(Integer... params)
//	{
//		int position = params[0];
//		
//		FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner, FoodCenterRequestFactory.class);
//		MsgProxy msg = msgsArray.get(position).getMsg();
//		factory.msgService().deleteMsg(msg.getMsg()).fire(new DeleteMsgReciever(position));
//		return null;
//	}
//	
//	
//	class DeleteMsgReciever extends Receiver<Void>
//	{
//		private final int position;
//		public DeleteMsgReciever(int position)
//		{
//			this.position = position;
//		}
//		
//		@Override
//		public void onSuccess(Void arg0)
//		{
//			msgsArray.remove(position);
//			publishProgress(SUCCESS);
//		}
//
//		@Override
//		public void onFailure(ServerFailure error)
//		{
//			publishProgress(error.getMessage());
//			try
//			{
//				Thread.sleep(1000);
//			}
//			catch (InterruptedException e)
//			{
//				Log.e(TAG, e.getClass().getSimpleName(), e);
//			}
//			publishProgress(HIDE_SPINNER);
//		}
//	}
//}
