package foodcenter.server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbMsg;

public class MsgService
{

    private static Logger logger = LoggerFactory.getLogger(MsgService.class); 

    public static void createMsg(String msg)
    {
		UserService userService = UserServiceFactory.getUserService(); 
		String email = userService.getCurrentUser().getEmail(); 

		DbHandler.saveMsg(email, msg);
        
        
		List<String> dev = DbHandler.getGcmRegistered();
		if (!dev.isEmpty())
		{
			logger.info("gcm: " + dev.size());
			GCMSender.send(msg, dev, 5);
		}
		else
		{
		    logger.info("no devices to broadcast");
		}

    }

    public static void deleteMsg(String msg)
    {
    	DbHandler.deleteMsg(msg);
    }

    public static List<DbMsg> getMsgs()
    {
    	
    	return DbHandler.getMsgs();
//    	List<DbMsg> msgs = db.getMsgs();
//    	LinkedList<String> res = new LinkedList<String>();
//    	for (DbMsg m : msgs)
//    	{
//    		res.add(m.getMsg());
//    	}
//        return res;
    }

}
