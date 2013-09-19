package foodcenter.server;

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
    protected static final String email = "email@email.com";
    protected static final String authDomain = "email.com";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Make the datastore a high-replication datastore, but with all jobs applying
     * immediately (simplifies tests that use eventually-consistent queries).
     */
    private LocalDatastoreServiceTestConfig dataConfig = new LocalDatastoreServiceTestConfig()
    // TODO if you want to set cross-group transactions //.setApplyAllHighRepJobPolicy()
    ;
    private LocalUserServiceTestConfig userConfig = new LocalUserServiceTestConfig();

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(dataConfig, userConfig).setEnvIsAdmin(true)
        .setEnvIsLoggedIn(true)
        .setEnvAuthDomain(authDomain)
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

        menuCats = 0;
        menuCatCourses = 0;
        numBranches = 0;
        numBranchMenuCats = 0;
        numBranchMenuCatCourses = 0;
    }

    @After
    public void tearDown()
    {
        helper.tearDown();
    }

    protected final void setUpPMF()
    {
        PMF.initThreadLocal();
    }

    protected final void tearDownPMF()
    {
        PMF.closeThreadLocal();
    }

}
