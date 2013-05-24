package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.Receiver;

import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.request.mock.MockTestRespone;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class RestaurantAdminServiceRequestTest extends AbstractRequestTest
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
	public void saveRestaurantServiceTest()
	{
		menuCats = 3;
		menuCatCourses = 4;
		numBranches = 2;
		numBranchMenuCats = 3;
		numBranchMenuCatCourses = 8;

		RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

		RestaurantProxy rest = createRest(service, "rest", menuCats, menuCatCourses, numBranches, numBranchMenuCats, numBranchMenuCatCourses);
		service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(new Receiver<RestaurantProxy>()
		{

			@Override
			public void onSuccess(RestaurantProxy response)
			{
				assertNotNull(response.getMenu());
				
				assertNotNull(response.getMenu().getCategories());
				assertEquals(menuCats, response.getMenu().getCategories().size());
				
				assertNotNull(response.getBranches());
				assertEquals(numBranches, response.getBranches().size());

			}
		});
	}

	/**
	 * this test will test and demonstrate the flow: <br> 
	 * restaurant admin adds a branch service to already exist branch
	 * @throws InterruptedException 
	 */
	@Test
	public void addRestaurantBranchServiceTest()
	{
		numBranches = 1;

		RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();
		
		RestaurantProxy rest = createRest(//
			service, //
			"rest", //
			menuCats, //
			menuCatCourses, //
			numBranches, //
			numBranchMenuCats, //
			numBranchMenuCatCourses);
		
		MockTestRespone<RestaurantProxy> notifier = new MockTestRespone<RestaurantProxy>();
		service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(notifier);
		assertNotNull(notifier.response);
		
		
		// tear down the pmf, because this is going to be a new RF call
		tearDownPMF();

		RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();
		
		// make the restaurant editable
		RestaurantProxy editable = adminService.edit(notifier.response);

		// add course to the restaurant menu

		RestaurantBranchProxy branch = createRestBranch(adminService, 0, 0);
		branch.setAddress("Dror");

		RestaurantBranchProxy branch2 = createRestBranch(adminService, 0, 0);
		branch.setAddress("Dror2");

		// setup a new pmf for the new call
		setUpPMF();

		notifier.response = null;
		
		adminService.addRestaurantBranch(editable, branch);
		++numBranches;

		adminService.addRestaurantBranch(editable, branch2);
		++numBranches;

		adminService.saveRestaurant(editable).with(RestaurantProxy.REST_WITH).to(notifier);
		adminService.fire();
		
		assertNotNull(notifier.response);
		assertEquals(numBranches, notifier.response.getBranches().size());

	}
	
	
	
	@Test
	public void addMenuCategoryCourseToRestaurantTest()
	{
		menuCats = 2;
		menuCatCourses = 2;
		RestaurantProxy response = null;
		RestaurantAdminServiceRequest service = rf.getRestaurantAdminService(); //service can invoke a single fire!

		/*
		 * create a restaurant for the rest of the test
		 */
		RestaurantProxy rest = createRest(service, "rest", menuCats, menuCatCourses, numBranches, numBranchMenuCats, numBranchMenuCatCourses);
		MockTestRespone<RestaurantProxy> testResponse = new MockTestRespone<RestaurantProxy>();
		service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(testResponse); //service is dead after a fire
		response = testResponse.response;
		testResponse.response = null;
		assertNotNull(response);
		tearDownPMF();									// tear down the pmf, because this is going to be a new RF call
		
		 // edit the restaurant and add a new course to the menu 1st category
		service = rf.getRestaurantAdminService(); 				//service can invoke a single fire!
		RestaurantProxy editable = service.edit(response);		// make the restaurant editable
		response = null;

		MenuCategoryProxy cat = editable.getMenu().getCategories().get(0); 	// get the 1st cat
		CourseProxy course = createCourse(service, "new", 14.0);			// create a new course	
		
		// add course to the restaurant menu and save it
		service.addCategoryCourse(cat, course); 
		service.saveRestaurant(editable).with(RestaurantProxy.REST_WITH).to(testResponse);
		
		// invoke the edit ...
		setUpPMF();								// setup a new pmf for the new call
		service.fire();							//fire all the requests...
		response = testResponse.response;
		testResponse.response = null;
		assertNotNull(response);
		
		// validate it was added only to the 1st category
		assertEquals(menuCatCourses, response.getMenu().getCategories().get(1).getCourses().size());
		assertEquals(menuCatCourses + 1, response.getMenu().getCategories().get(0).getCourses().size());
		
		
	}


}
