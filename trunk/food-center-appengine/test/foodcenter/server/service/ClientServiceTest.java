package foodcenter.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbUser;

public class ClientServiceTest extends AbstractServiceTest
{
	
	@Override
	@Before
	public void setUp()
	{
		super.setUp();		
	}

	/**
	 * checks that login service works
	 */
	@Test
	public void loginTest()
	{
		// get login info
		DbUser user = ClientService.login(null);

		// check that email was returned
		assertEquals(email, user.getEmail());

		// check that the db only contains the user
		List<DbUser> users = DbHandler.find(DbUser.class, null, null, null, null);
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
		ClientService.login(null);
		// get login info
		DbUser user = ClientService.login(null);
		// check that email was returned
		assertEquals(email, user.getEmail());
		// check that there is only 1 user in our db
		List<DbUser> users = DbHandler.find(DbUser.class, null, null, null, null);
		assertEquals(1, users.size());

	}

	/**
	 * checks if the same user isn't saved to the data base twice
	 */
	@Test
	public void loginGcmTest()
	{
		String gcmKey = " hila";
		// login with gcm
		DbUser user = ClientService.login(gcmKey);
		// check that gcm was returned
		assertEquals(gcmKey, user.getGcmKey());

		String gcmKey2 = "gcmKey2";
		user = ClientService.login(gcmKey2);

		// check that gcm was returned
		assertEquals(gcmKey2, user.getGcmKey());

		user = DbHandler.find(DbUser.class, "email == emailP", "String emailP", new Object[] { email });
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
		DbUser user = ClientService.login(gcmKey);

		// logout
		ClientService.logout();

		// make sure the GCM key was removed
		user = DbHandler.find(DbUser.class, "email == emailP", "String emailP", new Object[] { email });
		assertNotNull(user);
		assertEquals("", user.getGcmKey());
	}


}
