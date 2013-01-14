package foodcenter.service.gcm;

import foodcenter.server.db.Datastore;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;

public class GCMService
{

	static DbHandler db = new DbHandlerImp();

	public static void register(String regId)
	{
	    Datastore.register(regId);
	}

	public static void unregister(String regId)
	{
	    Datastore.unregister(regId);
	}


}
