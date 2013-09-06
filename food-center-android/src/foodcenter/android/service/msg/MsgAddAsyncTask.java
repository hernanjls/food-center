//package foodcenter.android.service.msg;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.web.bindery.requestfactory.shared.Receiver;
//import com.google.web.bindery.requestfactory.shared.ServerFailure;
//
//import foodcenter.android.MainActivity;
//import foodcenter.android.service.RequestUtils;
//import foodcenter.service.FoodCenterRequestFactory;
//
//public class MsgAddAsyncTask extends AsyncTask<Void, String, Void>
//{
//
//	private final MainActivity owner;
//	private final String msg;
//	
//	public MsgAddAsyncTask(MainActivity owner, String msg)
//	{
//		this.owner = owner;
//		this.msg = msg;
//	}
//	@Override
//	protected void onPreExecute()
//	{
//	    super.onPreExecute();
//	    owner.showSpinner("Adding msg....");
//	}
//
//	@Override
//	protected Void doInBackground(Void... arg0)
//	{
//		FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(owner, FoodCenterRequestFactory.class);
//		factory.msgService().createMsg(msg).fire(new MsgAddReciever());
//		return null;
//	}
//
//	@Override
//	protected void onProgressUpdate(String... values)
//	{
//		owner.hideSpinner();
////		new MsgGetAsyncTask(owner).execute();
//	}
//
//	
//	class MsgAddReciever extends Receiver<Void>
//	{
//
//		@Override
//		public void onSuccess(Void response)
//		{
//			publishProgress("success");
//		}
//
//		@Override
//		public void onFailure(ServerFailure error)
//		{
//			// don't call super and throw runtime exception
//			Log.e("req context", error.getMessage());
//			publishProgress(error.getMessage());
//		}
//	}
//
//}
