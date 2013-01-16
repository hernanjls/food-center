package foodcenter.server.service.gcm;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;

public class GCMService
{

	static DbHandler db = new DbHandlerImp();

	public static void register(String email, String regId)
	{
	    db.gcmRegister(email, regId);
	}

	public static void unregister(String email, String regId)
	{
	    db.gcmUnregister(email, regId);
	}


}
