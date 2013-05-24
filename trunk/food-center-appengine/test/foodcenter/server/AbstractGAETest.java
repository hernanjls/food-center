package foodcenter.server;

import javax.jdo.Transaction;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;
import com.google.web.bindery.requestfactory.server.testing.InProcessRequestTransport;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;

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
		try
		{
			Transaction tx = PMF.get().currentTransaction();
			if (tx.isActive())
			{
				tx.commit();
			}
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
		finally
		{
			Transaction tx = PMF.get().currentTransaction();
			if (tx.isActive())
			{
				tx.rollback();
			}
			PMF.closeThreadLocal();
			
		}
	}

}
