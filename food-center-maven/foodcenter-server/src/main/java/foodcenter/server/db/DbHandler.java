package foodcenter.server.db;

import java.util.List;

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

}
