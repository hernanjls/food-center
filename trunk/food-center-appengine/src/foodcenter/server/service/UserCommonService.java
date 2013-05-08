package foodcenter.server.service;

import java.util.List;

import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

import foodcenter.server.ThreadLocalPM;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCourse;
import foodcenter.server.db.modules.DbMenu;
import foodcenter.server.db.modules.DbMenuCategory;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;
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
		return db.find(DbUser.class, "email == emailP", "String emailP", new Object[] { email });
	}

	public static DbUser login(String gcmKey)
	{

		// find user by email
		// if user exists update and return user
		// else save a new user to the db and return it
		DbUser user = getDbUser(userService.getCurrentUser().getEmail());
		if (null == user)
		{
			String logoutRedirectionUrl = isDev ? "food_center.jsp?gwt.codesvr=127.0.0.1:9997" : "";

			user = new DbUser();

			user.setEmail(userService.getCurrentUser().getEmail());
			user.setAdmin(userService.isUserAdmin());
			user.setLogoutUrl(userService.createLogoutURL("/") + logoutRedirectionUrl);
			user.setNickName(userService.getCurrentUser().getNickname());
			user.setUserId(userService.getCurrentUser().getUserId());
			logger.info("Login info: " + user.getEmail());
		}
		else
		{
			logger.info("User already exsists");
			user.setAdmin(userService.isUserAdmin());
		}

		if (null != gcmKey)
		{
			user.setGcmKey(gcmKey);
		}

		db.save(user);
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
		db.save(user);
	}

	private static void loadRestaurantBranches(List<DbRestaurantBranch> branches)
	{
		for (DbRestaurantBranch b : branches)
		{
			DbMenu menu = b.getMenu();
			if (null != menu)
			{
				loadMenu(menu);
			}
			// TODO load other things.....
		}
	}

	private static void loadMenuCategoryCourses(List<DbCourse> courses)
	{
		for (DbCourse c : courses)
		{
			c.getId();
		}
	}

	private static void loadMenuCategory(List<DbMenuCategory> cats)
	{
		for (DbMenuCategory c : cats)
		{
			List<DbCourse> courses = c.getCourses();
			if (null != courses)
			{
				loadMenuCategoryCourses(courses);
			}
		}
	}

	private static void loadMenu(DbMenu menu)
	{
		List<DbMenuCategory> cats = menu.getCategories();
		if (null != cats)
		{
			loadMenuCategory(cats);
		}
	}

	private static void loadRestaurats(List<DbRestaurant> rests)
	{

		for (DbRestaurant rest : rests)
		{
			DbMenu menu = rest.getMenu();
			if (null != menu)
			{
				loadMenu(menu);
			}

			List<DbRestaurantBranch> branches = rest.getBranches();
			if (null != branches)
			{
				loadRestaurantBranches(branches);
			}
		}

	}

	public static List<DbRestaurant> getDefaultRestaurants()
	{
		logger.info("getDefaultRestaurants is called");
		List<DbRestaurant> res = db.find(DbRestaurant.class, 10);
		if (null != res)
		{
			loadRestaurats(res);
		}
		return res;
		// Transaction tx = null;
		// try
		// {
		// tx = ThreadLocalPM.get().currentTransaction();
		// if (tx.isActive())
		// {
		// tx.rollback();
		// }
		// return res;
		// }
		// catch (Exception e)
		// {
		// logger.error(e.getMessage(), e);
		// }
		// finally
		// {
		// if (null != tx && tx.isActive())
		// {
		// tx.rollback();
		// }
		// }
		//
		// return null;
	}

	public static DbRestaurant getRestaurant(String id)
	{
		return db.find(DbRestaurant.class, id);
	}

	public static DbRestaurant saveRestaurant(DbRestaurant rest)
	{
		return db.save(rest);
	}

	public static Boolean deleteRestaurant(String id)
	{
		return 0 == Long.compare(0l, db.delete(DbRestaurant.class, id));
	}

	public static List<DbCompany> getDefaultCompanies()
	{
		logger.info("getDefaultCompanies is called");
		return db.find(DbCompany.class, 10);
	}

	public static DbCompany getCompany(String id)
	{
		return db.find(DbCompany.class, id);
	}

	public static DbCompany saveCompany(DbCompany comp)
	{
		return db.save(comp);
	}

	public static Boolean deleteCompany(String id)
	{
		return 0 == Long.compare(0l, db.delete(DbCompany.class, id));
	}

}
