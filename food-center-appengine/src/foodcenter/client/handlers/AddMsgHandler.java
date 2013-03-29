package foodcenter.client.handlers;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.FoodCenter;
import foodcenter.client.service.RequestUtils;

public class AddMsgHandler implements KeyPressHandler, ClickHandler
{
	private final FoodCenter foodCenter;
    private RequestUtils reqUtils = null;

	public AddMsgHandler(FoodCenter foodCenter)
	{
		this.foodCenter = foodCenter;
		this.reqUtils = new RequestUtils();
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
	    // Get the new msg from the UI
		final String msg = foodCenter.getMsgTextBox().getText();

		// Set the focus in case the msg isn't valid, for the user to continue typing
		foodCenter.getMsgTextBox().setFocus(true);

		// Don't add the msg if it's already in the table or invalid.
		// Get the table messages
		List<String> msgs = foodCenter.getMsgs();
		if (msgs.contains(msg) || !isValidMsg(msg))
		{
			return;
		}

		// Clear the message for the input because it will be added to the DB.
		foodCenter.getMsgTextBox().setText("");

		// add the msg to the db, and to the table on service success.
		reqUtils.getRequestFactory().msgService().createMsg(msg).fire(new AddMsgRecieverk(msg));

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
	
	class AddMsgRecieverk extends Receiver<Void>
	{

		private final String msg;

		public AddMsgRecieverk(String msg)
		{
			this.msg = msg;
		}

		@Override
        public void onSuccess(Void result)
        {
            foodCenter.addMsgToTable(msg);
        }
		
        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert("[FAIL] add msg : " + error.getMessage());
        }
        


	}


}
