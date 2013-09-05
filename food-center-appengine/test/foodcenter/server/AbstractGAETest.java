package foodcenter.server;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import foodcenter.server.db.PMF;

public abstract class AbstractGAETest
{
	
	protected final String email = "email@email.com";
	protected final String authDomain = "email.com";

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper( //
	    new LocalDatastoreServiceTestConfig(), //
	    new LocalUserServiceTestConfig()//
	) //
	.setEnvIsAdmin(true) //
	    .setEnvIsLoggedIn(true) //
	    .setEnvAuthDomain(authDomain) //
	    .setEnvEmail(email);
	
	
	// variables used by tests, each test will set it
	protected int menuCats = 0;
	protected int menuCatCourses = 0;
	protected int numBranches = 0;
	protected int numBranchMenuCats = 0;
	protected int numBranchMenuCatCourses = 0;

	
	@Before
	public void setUp()
	{
		helper.setUp();
		setUpPMF();

		menuCats = 0;
		menuCatCourses = 0;
		numBranches = 0;
		numBranchMenuCats = 0;
		numBranchMenuCatCourses = 0;
	}

	
	@After
	public void tearDown()
	{
		tearDownPMF();
		helper.tearDown();
	}
		
	protected final void setUpPMF()
	{
		PMF.initThreadLocal().currentTransaction().begin();
	}

	protected final void tearDownPMF()
	{
	    PersistenceManager pmf = PMF.get();
        if (null == pmf)
        {
            return;
        }
		try
		{
			Transaction tx = pmf.currentTransaction();
			if (tx.isActive())
			{
				tx.rollback();
			}
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
		finally
		{
		    
			PMF.closeThreadLocal();
			
		}
	}

}
