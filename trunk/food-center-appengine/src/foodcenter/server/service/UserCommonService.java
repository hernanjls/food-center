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

public class UserCommonService
{

    private static UserService userService = UserServiceFactory.getUserService();
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    private static DbHandler db = new DbHandlerImp();

    public static DbUser getDbUser(String email)
    {
        logger.info("getDbUser is called");
        return db.find(DbUser.class, "email == emailP", "String emailP", new Object[]{email});
    }
    
    public static DbUser getLoginInfo()
    {
    	
    	    	// find user by email
    	        // if user exists update and return user
    	        // else save a new user to the db and return it
        DbUser res = new DbUser();
        if(isNewUser(res))
        {
        	return res;
        }
        else 
        {
        	logger.info("User already exsists");
        	return res;
        }
        
//
//        res.setAdmin(userService.isUserAdmin());
//        res.setEmail(userService.getCurrentUser().getEmail());
//
//        String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";
//        res.setLogoutUrl(userService.createLogoutURL("/") + logoutRedirectionUrl);
//        res.setNickName(userService.getCurrentUser().getNickname());
//        res.setUserId(userService.getCurrentUser().getUserId());
//        logger.info("Login info: " + res.getEmail());
//        return res;
    }

    public static boolean isNewEmail(String email)
    {
    	return  (null == db.find(DbUser.class, email));
    	
    }
    public static boolean isNewUser(DbUser user)
    {
    	
    	if(isNewEmail(user.getEmail()))
    	{
    		 
    	        user.setAdmin(userService.isUserAdmin());
    	        user.setEmail(userService.getCurrentUser().getEmail());

    	        String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";
    	        user.setLogoutUrl(userService.createLogoutURL("/") + logoutRedirectionUrl);
    	        user.setNickName(userService.getCurrentUser().getNickname());
    	        user.setUserId(userService.getCurrentUser().getUserId());
    	        logger.info("Login info: " + user.getEmail());
    	        db.save(user);
    	        return true;

    	}
    	else
    	{
    		return false;
    	}
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
