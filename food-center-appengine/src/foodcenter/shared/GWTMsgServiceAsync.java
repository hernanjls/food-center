package foodcenter.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import foodcenter.server.db.modules.DbMsg;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GWTMsgServiceAsync
{

	void getMsgs(AsyncCallback<List<String>> callback);

	public void addMsg(String msg, AsyncCallback<Void> callback);

	public void removeMsg(String msg, AsyncCallback<Void> callback);
	
}
