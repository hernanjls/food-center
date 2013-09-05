package foodcenter.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbRestaurant;

public class AdminService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(AdminService.class);

    /******************** company apis ********************/

    public static Boolean deleteCompany(String id)
    {
        if (!userService.isUserAdmin())
        {
            return false;
        }

        DbCompany d = DbHandler.find(DbCompany.class, id);
        if (null == d)
        {
            logger.error("Can't find company, id=" + id);
            return false;
        }
        
        d.deleteImage();
        long deletedRows = DbHandler.delete(DbCompany.class, id);
        return (deletedRows > 0);
    }

    /******************** restaurant apis ********************/

    public static Boolean deleteRestaurant(String id)
    {
        if (!userService.isUserAdmin())
        {
            return false;
        }

        DbRestaurant r = DbHandler.find(DbRestaurant.class, id);
        if (null == r)
        {
            logger.error("Can't find restaurant, id=" + id);
            return false;
        }
        r.deleteImage();
        long deletedRows = DbHandler.delete(DbRestaurant.class, id);
        return (deletedRows > 0);
    }
 
}
