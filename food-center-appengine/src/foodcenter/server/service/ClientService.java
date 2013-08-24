package foodcenter.server.service;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.PrivilegeManager;

public class ClientService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;

    public static String getLogoutUrl()
    {
        String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";
        return userService.createLogoutURL("/") + logoutRedirectionUrl;
    }
    
    public static User getCurrentUser()
    {
        return userService.getCurrentUser();
    }
    
    public static DbUser login(String gcmKey)
    {

        // find user by email

        // else save a new user to the db and return it
        DbUser user = PrivilegeManager.getCurrentUser();
        if (null == user)
        {
            String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";

            user = new DbUser();

            user.setEmail(userService.getCurrentUser().getEmail());
            user.setLogoutUrl(userService.createLogoutURL("/") + logoutRedirectionUrl);
            user.setUserId(userService.getCurrentUser().getUserId());

        }
        else
        {
            logger.info("User already exsists");

        }

        logger.info("Login info: " + user.getEmail());

        user.setNickName(userService.getCurrentUser().getNickname());
        user.setAdmin(userService.isUserAdmin());

        if (null != gcmKey)
        {
            user.setGcmKey(gcmKey);
        }

        return DbHandler.save(user);
    }

    public static void logout()
    {
        DbUser user = PrivilegeManager.getCurrentUser();
        ;
        if (null == user)
        {
            return;

        }
        user.setGcmKey("");
        DbHandler.save(user);
    }

    public static List<DbRestaurant> getDefaultRestaurants()
    {
        logger.info("getDefaultRestaurants is called");
        List<DbRestaurant> res = DbHandler.find(DbRestaurant.class, 10);
        return res;
    }

    public static DbRestaurant getRestaurantById(String id)
    {
        logger.info("getRestaurantById is called, id: " +id);
        return DbHandler.find(DbRestaurant.class, id);
    }

    public static List<DbRestaurant> findRestaurant(String pattern)
    {
        throw new NotImplementedException();
    }

    public static DbOrder makeOrder(DbOrder order)
    {
        logger.info("makeOrder is called");
        if (null == order)
        {
            return null;
        }

        // Set the user of the current order
        DbUser user = PrivilegeManager.getCurrentUser();
        if (null == user)
        {
            return null;
        }

        order.setUser(user);
        user.getOrders().add(order);

        // Save the order
        if (null == DbHandler.save(user))
        {
            return null;
        }
        return order;
    }

    public static List<DbOrder> getOrders()
    {
        DbUser user = PrivilegeManager.getCurrentUser();
        return user.getOrders();
    }

    public static List<DbCompany> getDefaultCompanies()
    {
        logger.info("getDefaultCompanies is called");
        return DbHandler.find(DbCompany.class, 10);
    }

    public static List<DbCompany> findCompany(String pattern)
    {
        throw new NotImplementedException();
    }

}
