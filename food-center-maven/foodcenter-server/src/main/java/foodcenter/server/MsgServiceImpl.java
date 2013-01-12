package foodcenter.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import foodcenter.client.MsgService;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MsgServiceImpl extends RemoteServiceServlet implements MsgService
{

	DbHandler db = new DbHandlerImp();
	
	public List<String> getMsgs() throws IllegalArgumentException
	{
		return db.getMsgs();
	}


	
    public void addMsg(String msg) throws IllegalArgumentException
    {
	    db.saveMsg(msg);
    }


	
    public void removeMsg(String msg) throws IllegalArgumentException
    {
		long res = db.deleteMsg(msg);
		if (0 == res)
		{
			throw new IllegalArgumentException("msg doesnt exists: " + msg);
		}
		
    }
}
