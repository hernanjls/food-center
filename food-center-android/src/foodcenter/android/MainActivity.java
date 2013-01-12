package foodcenter.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.android.service.RequestUtils;
import foodcenter.service.FoodCenterRequestFactory;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	protected void onStart()
	{
		super.onStart();

		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... arg0)
			{
				try
				{
					FoodCenterRequestFactory factory = RequestUtils.getRequestFactory(MainActivity.this, FoodCenterRequestFactory.class);

					String msg = "android works" + Math.random();
					factory.msgRequest().createMsg(msg).fire(new MsgAddReciever());
				}
				catch (Exception e)
				{
					Log.e("tag", e.getMessage(), e);
				}
				return null;
			}

		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class MsgAddReciever extends Receiver<Void>
	{

		@Override
		public void onSuccess(Void response)
		{
			Popup.show(MainActivity.this, "success");
		}

		@Override
		public void onFailure(ServerFailure error)
		{
			Popup.show(MainActivity.this, error.getMessage());
			Log.e("req context", error.getMessage());
		}

	}
}
