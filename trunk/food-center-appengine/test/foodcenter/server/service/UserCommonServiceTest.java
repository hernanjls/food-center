package foodcenter.server.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandlerImp;
import foodcenter.server.db.modules.DbUser;

public class UserCommonServiceTest
{

    private final String email = "email@email.com";
    private DbHandler db = null;
    
    private final LocalServiceTestHelper helper = //
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(), new LocalUserServiceTestConfig()) //    
        .setEnvIsAdmin(true) //
        .setEnvIsLoggedIn(true) //
        .setEnvAuthDomain("email.com") //
        .setEnvEmail(email); // 
    @Before
    public void setUp()
    {
        helper.setUp();
        db = new DbHandlerImp();    
    }

    @After
    public void tearDown()
    {
        helper.tearDown();
        
    }

    /**
     * checks that login service works 
     */
    @Test
    public void loginTest()
    {
        // get login info
        DbUser user = UserCommonService.login(null);

        // check that email was returned
        assertEquals(email, user.getEmail());
        
        // check that the db only contains the user
        List<DbUser> users = db.find(DbUser.class, null, null, null, null);
        assertEquals(1, users.size());
        assertEquals(email, user.getEmail());
        
        
    }

    /**
     * checks that gcm key was updated on second login
     */
    @Test
    public void loginTestDoesnDuplicateUsersTest()
    {
        // get login info
        UserCommonService.login(null);
        // get login info
        DbUser user = UserCommonService.login(null);
        // check that email was returned
        assertEquals(email, user.getEmail());
        // check that there is only 1 user in our db
        List<DbUser> users = db.find(DbUser.class, null, null, null, null);
        assertEquals(1, users.size());
        
    }
    
    /**
     * checks if the same user isn't saved to the data base twice
     */
    @Test
    public void loginGcmTest()
    {
        String gcmKey = " hila" ;
        // login with gcm
        DbUser user = UserCommonService.login(gcmKey);
        // check that gcm was returned
        assertEquals(gcmKey , user.getGcmKey());
        
        String gcmKey2 = "gcmKey2" ;
        user = UserCommonService.login(gcmKey2);
        
        //check that gcm was returned
        assertEquals(gcmKey2 , user.getGcmKey());
        
        user = db.find(DbUser.class, "email == emailP", "String emailP", new Object[]{email});
        assertEquals(gcmKey2, user.getGcmKey());
    }

    /**
     * checks that logout removes GCM key from the server
     */
    @Test
    public void logoutTest()
    {
        String gcmKey = "hila";
        // login with GCM key
        DbUser user = UserCommonService.login(gcmKey);
        
        // logout
        UserCommonService.logout();
   
        // make sure the GCM key was removed
        user = db.find(DbUser.class, "email == emailP", "String emailP", new Object[]{email});
        assertNotNull(user);
        assertEquals("" , user.getGcmKey());   
    }
}
    