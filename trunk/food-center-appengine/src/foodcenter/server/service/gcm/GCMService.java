package foodcenter.server.service.gcm;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;

public class GCMService
{

	static DbHandler db = new DbHandlerImp();

	public static void register(String regId)
	{
		UserService userService = UserServiceFactory.getUserService();
		String email = userService.getCurrentUser().getEmail(); 
	    db.gcmRegister(email, regId);
	}

	public static void unregister(String regId)
	{
		UserService userService = UserServiceFactory.getUserService(); 
		String email = userService.getCurrentUser().getEmail(); 
	    db.gcmUnregister(email, regId);
	}


}
