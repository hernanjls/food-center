package foodcenter.server.service.msg;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbMsg;

public class MsgService
{

    static DbHandler db = new DbHandlerImp();

    public static void createMsg(String msg)
    {
		UserService userService = UserServiceFactory.getUserService(); 
		String email = userService.getCurrentUser().getEmail(); 

        db.saveMsg(email, msg);
        
        
		List<String> dev = db.getGcmRegistered();
		if (!dev.isEmpty())
		{
			Logger.getLogger(MsgService.class.getName()).log(Level.INFO, "gcm: " + dev.size());
			GCMSender.send(msg, dev, 5);
		}
		else
		{
			Logger.getLogger(MsgService.class.getName()).log(Level.INFO, "no devices to broadcast");
		}

    }

    public static void deleteMsg(String msg)
    {
        db.deleteMsg(msg);
    }

    public static List<String> getMsgs()
    {
    	
//    	return db.getMsgs();
    	List<DbMsg> msgs = db.getMsgs();
    	LinkedList<String> res = new LinkedList<String>();
    	for (DbMsg m : msgs)
    	{
    		res.add(m.getMsg());
    	}
        return res;
    }

}
