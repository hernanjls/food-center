package foodcenter.client.handlers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.FoodCenter;
import foodcenter.client.service.RequestUtils;
import foodcenter.shared.GWTMsgService;
import foodcenter.shared.GWTMsgServiceAsync;

public class RemoveMsgHandler implements ClickHandler
{
	
	private final FoodCenter foodCenter;
	private final String msg;
	private RequestUtils reqUtils = null;

	public RemoveMsgHandler(FoodCenter foodCenter, String msg)
	{
		this.foodCenter = foodCenter;
		this.msg = msg;
		this.reqUtils =  new RequestUtils();
	}

	@Override
	public void onClick(ClickEvent event)
	{
		// Removes the msg to the db, and from the table on service success.
		
	    reqUtils.getRequestFactory().msgService().deleteMsg(msg).fire(new RemoveMsgReceiver(msg));
	}
	
	class RemoveMsgReceiver extends Receiver<Void>
	{
		private final String msg;

		public RemoveMsgReceiver(String msg)
		{
			this.msg = msg;
		}
		
		@Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("[FAIL] remove msg : " + error.getMessage());
            foodCenter.updateTableFromDb();
        }
        
		@Override
		public void onSuccess(Void result)
		{
			foodCenter.deleteMsgFromTable(msg);
		}

	}

}
