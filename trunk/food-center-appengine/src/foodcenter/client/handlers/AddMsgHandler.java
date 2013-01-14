package foodcenter.client.handlers;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import foodcenter.client.FoodCenter;
import foodcenter.shared.GWTMsgService;
import foodcenter.shared.GWTMsgServiceAsync;

public class AddMsgHandler implements KeyPressHandler, ClickHandler
{
	private final FoodCenter foodCenter;
	private static GWTMsgServiceAsync msgSvc = GWT.create(GWTMsgService.class);

	public AddMsgHandler(FoodCenter foodCenter)
	{
		this.foodCenter = foodCenter;
	}

	@Override
	public void onKeyPress(KeyPressEvent event)
	{
		if (event.getCharCode() == KeyCodes.KEY_ENTER)
		{
			addMsg();
		}
	}

	@Override
	public void onClick(ClickEvent event)
	{
		addMsg();
	}

	private void addMsg()
	{

		final String msg = foodCenter.getMsgTextBox().getText();

		foodCenter.getMsgTextBox().setFocus(true);

		// Don't add the msg if it's already in the table or invalid.
		List<String> msgs = foodCenter.getMsgs();
		if (msgs.contains(msg) || !isValidMsg(msg))
		{
			return;
		}

		foodCenter.getMsgTextBox().setText("");

		// add the msg to the db, and to the table on service success.
		if (msgSvc == null)
		{
			msgSvc = GWT.create(GWTMsgService.class);
		}
		msgSvc.addMsg(msg, new AddMsgAsyncCallback(msg));

	}

	private boolean isValidMsg(String msg)
	{
		// msg must not start with a digit.
		if (msg.matches("^[0-9].*"))
		{
			Window.alert("'" + msg + "' is not a valid msg.");
			foodCenter.getMsgTextBox().selectAll();
			return false;
		}
		return true;
	}
	
	class AddMsgAsyncCallback implements AsyncCallback<Void>
	{

		private final String msg;

		public AddMsgAsyncCallback(String msg)
		{
			this.msg = msg;
		}

		@Override
		public void onFailure(Throwable caught)
		{
			Window.alert("[FAIL] add msg: " + caught.getMessage());
			foodCenter.updateTableFromDb();
		}

		@Override
		public void onSuccess(Void result)
		{
			foodCenter.addMsgToTable(msg);
		}

	}


}
