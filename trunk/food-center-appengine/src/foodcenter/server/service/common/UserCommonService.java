package foodcenter.server.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

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
}
