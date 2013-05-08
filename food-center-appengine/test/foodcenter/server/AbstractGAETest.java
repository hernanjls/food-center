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
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;
import com.google.web.bindery.requestfactory.server.testing.InProcessRequestTransport;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbCourse;
import foodcenter.server.db.modules.DbMenuCategory;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;

public abstract class AbstractGAETest
{
	
	protected final String email = "email@email.com";
	protected final String authDomain = "email.com";

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected DbHandler db = null;
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper( //
	    new LocalDatastoreServiceTestConfig(), //
	    new LocalUserServiceTestConfig()//
	) //
	.setEnvIsAdmin(true) //
	    .setEnvIsLoggedIn(true) //
	    .setEnvAuthDomain(authDomain) //
	    .setEnvEmail(email);
	
	
	@Before
	public void setUp()
	{
		helper.setUp();

		setUpPMF();

		db = new DbHandlerImp();
	}

	
	@After
	public void tearDown()
	{
		tearDownPMF();
		helper.tearDown();
	}
	
	
	protected final <T extends RequestFactory> T createRF(Class<T> requestFactoryClass)
	{
		ServiceLayer serviceLayer = ServiceLayer.create();
		SimpleRequestProcessor processor = new SimpleRequestProcessor(serviceLayer);
		T factory = RequestFactorySource.create(requestFactoryClass);
		factory.initialize(new SimpleEventBus(), new InProcessRequestTransport(processor));
		return factory;
	}
	
	protected final void setUpPMF()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		// pm.getFetchPlan().addGroup(FetchGroup.ALL);
		// pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
		// pm.setDetachAllOnCommit(true);

		ThreadLocalPM.set(pm);
		pm.currentTransaction().begin();

	}

	protected final void tearDownPMF()
	{
		try
		{
			Transaction tx = ThreadLocalPM.get().currentTransaction();
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
			Transaction tx = ThreadLocalPM.get().currentTransaction();
			if (tx.isActive())
			{
				tx.rollback();
			}
			ThreadLocalPM.get().close();
			ThreadLocalPM.set(null);
		}
	}

}
