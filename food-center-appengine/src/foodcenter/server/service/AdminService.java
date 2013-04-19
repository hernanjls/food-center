package foodcenter.server.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.GCMSender;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbMsg;
import foodcenter.server.db.modules.DbRestaurant;

public class AdminService
{

    private static DbHandler db = new DbHandlerImp();
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
    
    public static Boolean createCompany(String name)
    {
        //TODO
        throw new RuntimeException("Not Implemented");
    }
    
    public static Boolean deleteCompany(Long id)
    {
        //TODO
        throw new RuntimeException("Not Implemented");
    }


    /********************   restaurant apis    ********************/
    
    public static DbRestaurant searchRestaurantByName(String name)
    {
        if (!userService.isUserAdmin())
        {
            // TODO deal with it maybe...
        }
        
        return db.searchRestaurantByName(name);
    }

    public static List<DbRestaurant> getDefaultRestaurants()
    {
        if (!userService.isUserAdmin())
        {
            // TODO deal with it maybe...
        }
        //TODO
        throw new RuntimeException("Not Implemented");
    }
    
    public static Boolean deleteRestaurant(Long id)
    {
        if (!userService.isUserAdmin())
        {
            // TODO deal with it maybe...
        }
        //TODO
        throw new RuntimeException("Not Implemented");
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
    
    
    public static void createMsg(String msg)
    {
        String email = userService.getCurrentUser().getEmail(); 

        db.saveMsg(email, msg);
        
        
        List<String> dev = db.getGcmRegistered();
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

    public static void deleteMsg(String msg)
    {
        db.deleteMsg(msg);
    }

    public static List<DbMsg> getMsgs()
    {
        
        return db.getMsgs();
//      List<DbMsg> msgs = db.getMsgs();
//      LinkedList<String> res = new LinkedList<String>();
//      for (DbMsg m : msgs)
//      {
//          res.add(m.getMsg());
//      }
//        return res;
    }

}
