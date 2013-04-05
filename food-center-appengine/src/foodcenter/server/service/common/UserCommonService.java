package foodcenter.server.service.common;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.labs.repackaged.com.google.common.primitives.Bytes;

import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.service.enums.ServiceType;

public class UserCommonService
{

	private static UserService userService = UserServiceFactory.getUserService();
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	private static boolean isDev = SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
	
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
		List<DbRestaurant> res = new LinkedList<DbRestaurant>();
		DbRestaurant a = new DbRestaurant("My Mock Restaurant long name");
		a.setServices(Arrays.asList(ServiceType.values()));
		try
        {
	        byte[] bytes = FileUtils.readFileToByteArray(new File("images/pizza-delivery.png"));
	        List<Byte> list = Bytes.asList(bytes);
	        a.setIconBytes(list);
        }
        catch (Throwable e)
        {
	        logger.error(e.getMessage(), e);
        	// TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		
		
		res.add(a);
		res.add(a);
		return res;
	}
}
