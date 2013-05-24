package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.Receiver;

import foodcenter.service.proxies.UserProxy;
import foodcenter.service.requset.ClientServiceRequest;

public class ClentServiceRequestTest extends AbstractRequestTest
{

	@Override
	@Before
	public void setUp()
	{
	    super.setUp();
	}
	
	@Override
	@After
	public void tearDown()
	{
	    super.tearDown();
	}
	
	@Test
	public void loginRequestTest()
	{
		ClientServiceRequest service = rf.getClientService();
		service.login("").fire(new Receiver<UserProxy>()
		{
			@Override
			public void onSuccess(UserProxy response)
			{
				assertNotNull(response);
				assertEquals("", response.getGcmKey());
			}
		});

		service = rf.getClientService();
		service.login("gcmkey1").fire(new Receiver<UserProxy>()
		{
			@Override
			public void onSuccess(UserProxy response)
			{
				assertNotNull(response);
				assertEquals("gcmkey1", response.getGcmKey());
			}
		});
	}


}
