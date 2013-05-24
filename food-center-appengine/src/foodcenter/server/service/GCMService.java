package foodcenter.server.service;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.db.DbHandler;

public class GCMService
{


	public static void register(String regId)
	{
		UserService userService = UserServiceFactory.getUserService();
		String email = userService.getCurrentUser().getEmail(); 
		DbHandler.gcmRegister(email, regId);
	}

	public static void unregister(String regId)
	{
		UserService userService = UserServiceFactory.getUserService(); 
		String email = userService.getCurrentUser().getEmail(); 
		DbHandler.gcmUnregister(email, regId);
	}


}
