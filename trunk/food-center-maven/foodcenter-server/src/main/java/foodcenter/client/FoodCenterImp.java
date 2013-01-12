package foodcenter.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.handlers.AddMsgHandler;
import foodcenter.client.handlers.RemoveMsgHandler;

public class FoodCenterImp implements FoodCenter
{

	// private static final int REFRESH_INTERVAL = 5000; // ms

	private VerticalPanel verticalMainPanel = new VerticalPanel();
	private FlexTable msgFlexTable = new FlexTable();

	private HorizontalPanel horizonalAddPanel = new HorizontalPanel();
	private TextBox newMsgTextBox = new TextBox();
	private Button addMsgButton = new Button("Add");

	private ArrayList<String> msgs = new ArrayList<String>();

	private static MsgServiceAsync msgSvc = GWT.create(MsgService.class);

	/**
	 * Entry point method.
	 */
	@Override
	public void onModuleLoad()
	{

		// Assemble Add Stock panel.
		horizonalAddPanel.add(newMsgTextBox);
		horizonalAddPanel.add(addMsgButton);

		// Assemble Main panel.
		verticalMainPanel.add(msgFlexTable);
		verticalMainPanel.add(horizonalAddPanel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("msgList").add(verticalMainPanel);

		// Move cursor focus to the input box.
		newMsgTextBox.setFocus(true);

		// Listen for mouse events on the Add button.
		addMsgButton.addClickHandler(new AddMsgHandler(this));
		newMsgTextBox.addKeyPressHandler(new AddMsgHandler(this));

		// Create table for msgs in the db.
		updateTableFromDb();

	}

	/**
	 * Add msg to FlexTable. Executed when the user clicks the addMsgButton or
	 * presses enter in the newMsgTextBox.
	 */

	@Override
	public void updateTableFromDb()
	{

		msgFlexTable.removeAllRows();
		msgs.clear();
		msgFlexTable.setText(0, 0, "Msg");
		msgFlexTable.setText(0, 1, "Remove");

		// Initialize the service proxy.
		if (msgSvc == null)
		{
			msgSvc = GWT.create(MsgService.class);
		}

		// Make the call to get the msgs from the db.
		msgSvc.getMsgs(new GetMsgsAsynCallback());

	}

	/**
	 * adds the msg to the table, doesn't update/use any service.
	 * 
	 * @param msg is the msg to add.
	 */
	@Override
	public void addMsgToTable(String msg)
	{
		// Add the msg to the list, so user can't add it again.
		msgs.add(msg);

		int row = msgFlexTable.getRowCount();

		// Add a button to remove this stock from the table.
		Button removeMsgButton = new Button("remove msg");
		removeMsgButton.addClickHandler(new RemoveMsgHandler(this, msg));

		msgFlexTable.setText(row, 0, msg);
		msgFlexTable.setWidget(row, 1, removeMsgButton);
	}

	/**
	 * deletes the msg from the table, doesn't update/use any service.
	 * 
	 * @param msg is the msg to delete.
	 */
	@Override
	public void deleteMsgFromTable(String msg)
	{
		int removedIndex = msgs.indexOf(msg);
		msgs.remove(removedIndex);
		msgFlexTable.removeRow(removedIndex + 1);
	}

	@Override
	public final List<String> getMsgs()
	{
		return msgs;
	}
	
	@Override
    public TextBox getMsgTextBox()
    {
	    return newMsgTextBox;
    }

	class GetMsgsAsynCallback implements AsyncCallback<List<String>>
	{
		public void onFailure(Throwable caught)
		{
			Window.alert("[FAIL] service connection error: " + caught.getMessage());
		}

		public void onSuccess(List<String> result)
		{
			for (String s : result)
			{
				addMsgToTable(s);
			}
		}

	}
	
}