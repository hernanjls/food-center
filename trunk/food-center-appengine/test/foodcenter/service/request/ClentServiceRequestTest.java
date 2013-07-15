package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.Receiver;

import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.request.mock.MockTestRespone;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

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
		MockTestRespone<UserProxy> userResponse = new MockTestRespone<UserProxy>();
		service.login("").fire(userResponse);
		assertNotNull(userResponse.response);
		assertEquals("", userResponse.response.getGcmKey());

		service = rf.getClientService();
		userResponse.response = null;
		service.login("gcmkey1").fire(userResponse);
		assertNotNull(userResponse.response);
		assertEquals("gcmkey1", userResponse.response.getGcmKey());
	}
	
	@Test
	public void makeOrderRequestTest()
	{
		OrderProxy response; 
		
		menuCats = 3;
		menuCatCourses = 4;
		numBranches = 2;
		numBranchMenuCats = 3;
		numBranchMenuCatCourses = 8;

		RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();
		
		RestaurantProxy rest = createRest(adminService, "rest", menuCats, menuCatCourses, numBranches, numBranchMenuCats, numBranchMenuCatCourses);
		MockTestRespone<RestaurantProxy> restResponse = new MockTestRespone<RestaurantProxy>();
		adminService.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(restResponse);

		// tear down the pmf, because this is going to be a new RF call
		tearDownPMF();

		ClientServiceRequest service = rf.getClientService();
		MockTestRespone<UserProxy> userResponse = new MockTestRespone<UserProxy>();
		
		// setup a new pmf for the new call
		setUpPMF();
		service.login("").fire(userResponse);
		// tear down the pmf, because this is going to be a new RF call
		tearDownPMF();
		
		ClientServiceRequest clientService = rf.getClientService();
		RestaurantBranchProxy branch = restResponse.response.getBranches().get(0);
		OrderProxy order = createOrder(clientService, branch, numBranchMenuCatCourses);
		MockTestRespone<OrderProxy> orderResponse = new MockTestRespone<OrderProxy>();

		// setup a new pmf for the new call
		setUpPMF();
		clientService.makeOrder(order).fire(orderResponse);
	
		response = orderResponse.response; 
		assertNotNull(response);
		assertNotNull(response.getCourses());
		assertEquals(numBranchMenuCatCourses, response.getCourses().size());

	}


}
