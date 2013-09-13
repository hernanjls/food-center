package foodcenter.server.db;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * PMF is the persistance manager as can be seen in google examples. <br>
 * in addition it has a Thread local holder for implementing <br>
 * "Open Session per view" as needed by RequestFactory
 * 
 * @author Dror
 * 
 * @see {@link ThreadLocal} to understand how it works
 *
 */
public final class PMF
{
	private static final PersistenceManagerFactory pmfInstance = //
	JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private static Logger logger = LoggerFactory.getLogger(PMF.class);
	
	private static ThreadLocal<PersistenceManager> holder = new ThreadLocal<PersistenceManager>();

	public static PersistenceManagerFactory getPmfInstance()
	{
		return pmfInstance;
	}
	
	/**
	 * initialize an instance for the local thread. <br>
	 * 
	 * "one session in view" approach is required for request factory. <br>
	 * call this method in a your RF servlet filter.
	 * 
	 * it will use {@link PMF#getPmfInstance()} to use a single PM Factory.
	 * 
	 * @return the thread local PersistenceManager
	 * 
	 * @see {@link #closeThreadLocal()} to close PM
	 * @see {@link #get()} to get the current thread PM
	 */
	public static PersistenceManager initThreadLocal()
	{
		logger.info("starting PersistenceManager");
		
		PersistenceManager pm = holder.get();
		if (null != pm)
		{
			return pm;
		}
		
		pm = getPmfInstance().getPersistenceManager();
		pm.currentTransaction().begin();
		holder.set(pm);
		
		return pm;
	}
	
	/**
	 * @return the local thread PM it must be initialize 1st
	 * 
	 * @see {@link #initThreadLocal()} for initializing the thread PM
	 *  @see {@link #closeThreadLocal()} for closing the thread PM
	 */
	public static PersistenceManager get()
	{
		return holder.get();
	}
	
	/**
	 * destroy the instance for the local thread. <br>
	 * "one session in view" approach for request factory is required.
	 * call this method in a servlet filter doChain(...)
	 * 
	 * @see {@link #initThreadLocal()} to initialize the thread PM
	 */
	public static void closeThreadLocal()
	{
		logger.info("closing PersistenceManager");
		PersistenceManager pm = get();
		if (null == pm)
		{
			return;
		}
		
		Transaction tx = pm.currentTransaction(); 
		if (tx.isActive())
		{
		    tx.rollback();
		}
		
		pm.close();
		holder.set(null);
	}
	
	
}