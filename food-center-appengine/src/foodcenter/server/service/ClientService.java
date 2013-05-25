package foodcenter.server.service;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCart;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUser;

public class ClientService
{

	private static UserService userService = UserServiceFactory.getUserService();
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;

	public static DbUser getDbUser(String email)
	{
		logger.info("getDbUser is called");
		return DbHandler.find(DbUser.class, "email == emailP", "String emailP", new Object[] { email });
	}

	public static DbUser login(String gcmKey)
	{

		// find user by email

		// else save a new user to the db and return it
		DbUser user = getDbUser(userService.getCurrentUser().getEmail());
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

		DbHandler.save(user);

		return user;
	}

	public static void logout()
	{
		DbUser user = getDbUser(userService.getCurrentUser().getEmail());
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

	public static List<DbRestaurant> findRestaurant(String pattern)
	{
		throw new NotImplementedException();
	}

	public static DbCart makeOrder(DbCart order)
	{
		throw new NotImplementedException();
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
