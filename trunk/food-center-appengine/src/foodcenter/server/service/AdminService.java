package foodcenter.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.security.UsersManager;

public class AdminService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(AdminService.class);

    /******************** company apis ********************/

    public static Boolean deleteCompany(String id)
    {        
        if (!userService.isUserAdmin())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        
        DbCompany d = DbHandler.find(DbCompany.class, id);
        if (null == d)
        {
            logger.error(ServiceError.INVALID_COMP_ID + id);
            throw new ServiceError(ServiceError.INVALID_COMP_ID + id);
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
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }

        DbRestaurant r = DbHandler.find(DbRestaurant.class, id);
        if (null == r)
        {
            logger.error(ServiceError.INVALID_REST_ID + id);
            throw new ServiceError(ServiceError.INVALID_REST_ID + id);
        }
        r.deleteImage();
        long deletedRows = DbHandler.delete(DbRestaurant.class, id);
        return (deletedRows > 0);
    }
 
}
