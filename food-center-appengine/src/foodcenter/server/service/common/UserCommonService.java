package foodcenter.server.service.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbRestaurant;

public class UserCommonService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    private static DbHandler db = new DbHandlerImp();

    public static LoginInfo getLoginInfo()
    {
        LoginInfo res = new LoginInfo();

        res.setAdmin(userService.isUserAdmin());
        res.setEmail(userService.getCurrentUser().getEmail());

        String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";
        res.setLogoutUrl(userService.createLogoutURL("/") + logoutRedirectionUrl);
        res.setNickName(userService.getCurrentUser().getNickname());
        res.setUserId(userService.getCurrentUser().getUserId());
        logger.info("Login info: " + res.getEmail());
        return res;
    }

    public static List<DbRestaurant> getDefaultRestaurants()
    {
        logger.info("getDefaultRestaurants is called");
        return db.findN(DbRestaurant.class, 10);
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
