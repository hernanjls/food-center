package foodcenter.server;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.shared.GWTMsgService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GWTMsgServiceImpl extends RemoteServiceServlet implements GWTMsgService
{

    DbHandler db = new DbHandlerImp();    

    public List<String> getMsgs() throws IllegalArgumentException
    {
    	LinkedList<String> res = new LinkedList<String>();
    	for (DbMsg m : db.getMsgs())
    	{
    		res.add(m.getMsg());
    	}
        return res;//db.getMsgs();

    }

    public void addMsg(String msg) throws IllegalArgumentException
    {        
    	
    	UserService userService = UserServiceFactory.getUserService();
		String email = userService.getCurrentUser().getEmail();

		db.saveMsg(email, msg);
		
        List<String> dev = db.getGcmRegistered();
        if (!dev.isEmpty())
        {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "gcm: " + dev.size());
                GCMSender.send(msg, dev, 5);
        }
        else
        {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "no devices to broadcast");
        }
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
