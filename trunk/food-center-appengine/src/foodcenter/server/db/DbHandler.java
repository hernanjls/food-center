package foodcenter.server.db;

import java.util.List;

import foodcenter.server.db.modules.DbUserGcm;

public interface DbHandler
{

	public List<String> getMsgs();

	/**
	 * 
	 * @param msg
	 * @return the number of delete rows
	 */
	public long deleteMsg(String msg);

	public void saveMsg(String msg);

	
	public void gcmRegister(String email, String regId);

    public long gcmUnregister(String email, String regId);

    public List<String> getGcmRegistered();
}
