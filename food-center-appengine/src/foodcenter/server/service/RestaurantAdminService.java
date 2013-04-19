package foodcenter.server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUser;

public class RestaurantAdminService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    private static DbHandler db = new DbHandlerImp();

    public static DbUser getDbUser(String email)
    {
        logger.info("getDbUser is called");
        return db.find(DbUser.class, "email = emailP", "String emailP", new Object[]{email});
    }

    public static List<DbRestaurant> getDefaultRestaurants()
    {
        logger.info("getDefaultRestaurants is called");
        return db.find(DbRestaurant.class, 10);
    }

    public static DbRestaurant getRestaurant(String id)
    {
        return db.find(DbRestaurant.class, id);
    }

    public static Boolean saveRestaurant(DbRestaurant rest)
    {
        return (null != db.save(rest));
    }

    public static Boolean deleteRestaurant(String id)
    {
        return 0 == Long.compare(0l, db.delete(DbRestaurant.class, id));
    }
}
