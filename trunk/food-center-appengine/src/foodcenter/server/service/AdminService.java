package foodcenter.server.service;

import java.util.List;

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

    public static DbCompany searchCompanyByName(String name)
    {
        // TODO searchCompanyByName
        throw new RuntimeException("Not Implemented");
    }

    public static List<DbCompany> getDefaultCompanies()
    {
        // TODO getDefaultCompanies
        throw new RuntimeException("Not Implemented");
    }

    public static Boolean deleteCompany(Long id)
    {
        // TODO deleteCompany
        throw new RuntimeException("Not Implemented");
    }

    public static DbCompany saveCompany(DbCompany comp)
    {
        if (!comp.isEditable())
        {
            return null;
        }
        
        return DbHandler.save(comp);
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

    public static Long createRestaurant(String name)
    {
        if (!userService.isUserAdmin())
        {
            return null;
        }

        return null;
        // return db.createRestaurant(name);
    }

 
 
}
