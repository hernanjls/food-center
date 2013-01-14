package foodcenter.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("msgs")
public interface GWTMsgService extends RemoteService
{
	public List<String> getMsgs() throws IllegalArgumentException;
	
	public void addMsg(String msg) throws IllegalArgumentException;
	
	public void removeMsg(String msg) throws IllegalArgumentException;
}
