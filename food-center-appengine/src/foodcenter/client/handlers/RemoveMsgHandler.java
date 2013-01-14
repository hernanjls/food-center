package foodcenter.client.handlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import foodcenter.client.FoodCenter;
import foodcenter.shared.GWTMsgService;
import foodcenter.shared.GWTMsgServiceAsync;

public class RemoveMsgHandler implements ClickHandler
{
	
	private final FoodCenter foodCenter;
	private final String msg;
	
	
	private static GWTMsgServiceAsync msgSvc = GWT.create(GWTMsgService.class); 

	public RemoveMsgHandler(FoodCenter foodCenter, String msg)
	{
		this.foodCenter = foodCenter;
		this.msg = msg;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		// Removes the msg to the db, and from the table on service success.
		if (msgSvc == null)
		{
			msgSvc = GWT.create(GWTMsgService.class);
		}
		msgSvc.removeMsg(msg, new RemoveMsgsAsynCallback(msg));
	}
	
	
	
	class RemoveMsgsAsynCallback implements AsyncCallback<Void>
	{
		private final String msg;

		public RemoveMsgsAsynCallback(String msg)
		{
			this.msg = msg;
		}

		public void onFailure(Throwable caught)
		{
			Window.alert("[FAIL] remove msg: " + caught.getMessage());
			foodCenter.updateTableFromDb();
		}

		public void onSuccess(Void result)
		{
			foodCenter.deleteMsgFromTable(msg);
		}

	}

}
