package foodcenter.server.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.DbRestaurant;

public class AdminService
{

    private static UserService userService = UserServiceFactory.getUserService();
    
    /********************   company apis    ********************/
    
    public static DbCompany searchCompanyByName(String name)
    {
        //TODO 
        throw new RuntimeException("Not Implemented");
    }

    public static List<DbCompany> getDefaultCompanies()
    {
        //TODO
        throw new RuntimeException("Not Implemented");
    }
    
    
    public static Boolean deleteCompany(Long id)
    {
        //TODO
        throw new RuntimeException("Not Implemented");
    }

	public static DbCompany saveCompany(DbCompany comp)
	{
		return DbHandler.save(comp);
	}

    /********************   restaurant apis    ********************/
    
    

    
    
    public static Boolean deleteRestaurant(String id)
	{
		return 0 == Long.compare(0l, DbHandler.delete(DbRestaurant.class, id));
	}    
    
    
    public static Long createRestaurant(String name)
    {
        if (!userService.isUserAdmin())
        {
            return null;
        }
        
        return null;
//        return db.createRestaurant(name);
    }
    
    
    @Deprecated
    public static void createMsg(String msg)
    {
        String email = userService.getCurrentUser().getEmail(); 

        DbHandler.saveMsg(email, msg);
        
        
        List<String> dev = DbHandler.getGcmRegistered();
        if (!dev.isEmpty())
        {
            Logger.getLogger(AdminService.class.getName()).log(Level.INFO, "gcm: " + dev.size());
            GCMSender.send(msg, dev, 5);
        }
        else
        {
            Logger.getLogger(AdminService.class.getName()).log(Level.INFO, "no devices to broadcast");
        }

    }

    @Deprecated
    public static void deleteMsg(String msg)
    {
    	DbHandler.deleteMsg(msg);
    }

    @Deprecated
    public static List<DbMsg> getMsgs()
    {
        
        return DbHandler.getMsgs();
//      List<DbMsg> msgs = db.getMsgs();
//      LinkedList<String> res = new LinkedList<String>();
//      for (DbMsg m : msgs)
//      {
//          res.add(m.getMsg());
//      }
//        return res;
    }

}
