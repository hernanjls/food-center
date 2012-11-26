package foodcenter.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.TextBox;

public interface FoodCenter extends EntryPoint
{

	public void updateTableFromDb();

	public void addMsgToTable(String msg);

	public void deleteMsgFromTable(String msg);

	public TextBox getMsgTextBox();
	
	public List<String> getMsgs();

}
