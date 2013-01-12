package foodcenter.server.db;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import foodcenter.server.db.modules.DbMsg;

public class DbHandlerImp implements DbHandler
{

	@Override
	public void saveMsg(String msg)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

		DbMsg m = new DbMsg(msg);

		try
		{
			pm.makePersistent(m);
		}
		finally
		{
			pm.close();
		}
	}

	@Override
	public long deleteMsg(String msg)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			Query q = pm.newQuery(DbMsg.class);
			q.setFilter("msg == value");
			q.declareParameters("String value");
			return q.deletePersistentAll(msg);
		}
		finally
		{
			pm.close();
		}
	}

	@Override
	public List<String> getMsgs()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try
		{
			List<String> res = new LinkedList<String>();
			Extent<DbMsg> extent = pm.getExtent(DbMsg.class, false);
			for (DbMsg m : extent)
			{
				res.add(m.getMsg());
			}
			return res;
		}
		finally
		{
			pm.close();
		}
	}
}
