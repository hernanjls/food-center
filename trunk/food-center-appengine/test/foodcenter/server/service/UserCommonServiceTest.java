package foodcenter.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.service.RequestUtils;
import foodcenter.server.AbstractGAETest;
import foodcenter.server.db.modules.DbUser;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.UserCommonServiceProxy;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;

public class UserCommonServiceTest extends AbstractGAETest
{

	private FoodCenterRequestFactory rf = null;

	// variables used by tests, each test will set it
	private int menuCats = 0;
	private int menuCatCourses = 0;
	private int numBranches = 0;
	private int numBranchMenuCats = 0;
	private int numBranchMenuCatCourses = 0;
	
	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		rf = createRF(FoodCenterRequestFactory.class);
		
		
		menuCats = 0;
		menuCatCourses = 0;
		numBranches = 0;
		numBranchMenuCats = 0;
		numBranchMenuCatCourses = 0;
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
		String gcmKey = " hila";
		// login with gcm
		DbUser user = UserCommonService.login(gcmKey);
		// check that gcm was returned
		assertEquals(gcmKey, user.getGcmKey());

		String gcmKey2 = "gcmKey2";
		user = UserCommonService.login(gcmKey2);

		// check that gcm was returned
		assertEquals(gcmKey2, user.getGcmKey());

		user = db.find(DbUser.class, "email == emailP", "String emailP", new Object[] { email });
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
		user = db.find(DbUser.class, "email == emailP", "String emailP", new Object[] { email });
		assertNotNull(user);
		assertEquals("", user.getGcmKey());
	}

	@Test
	public void loginServiceTest()
	{
		UserCommonServiceProxy service = rf.getUserCommonService();
		service.login("").fire(new Receiver<UserProxy>()
		{
			@Override
			public void onSuccess(UserProxy response)
			{
				assertNotNull(response);
				assertEquals("", response.getGcmKey());
			}
		});

		service = rf.getUserCommonService();
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

	@Test
	public void saveRestaurantServiceTest()
	{
		menuCats = 3;
		menuCatCourses = 4;
		numBranches = 2;
		numBranchMenuCats = 3;
		numBranchMenuCatCourses = 8;

		FoodCenterRequestFactory rf = createRF(FoodCenterRequestFactory.class);
		UserCommonServiceProxy service = rf.getUserCommonService();

		RestaurantProxy rest = createRest(service, "rest", menuCats, menuCatCourses, numBranches, numBranchMenuCats, numBranchMenuCatCourses);
		service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(new Receiver<RestaurantProxy>()
		{

			@Override
			public void onSuccess(RestaurantProxy response)
			{
				assertNotNull(response.getMenu());
				assertNotNull(response.getMenu().getCategories());
				assertEquals(menuCats, response.getMenu().getCategories().size());

			}
		});
	}

	@Test
	public void addRestaurantBranchServiceTest()
	{	
		numBranches = 1;
		
		UserCommonServiceProxy service = rf.getUserCommonService();

		RestaurantProxy rest = createRest(service, "rest", menuCats, menuCatCourses, numBranches, numBranchMenuCats, numBranchMenuCatCourses);
		
		service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(new Receiver<RestaurantProxy>()
		{
			@Override
			public void onSuccess(RestaurantProxy response)
			{
				// tear down the pmf, because this is going to be a new RF call
				tearDownPMF();
				
				UserCommonServiceProxy service = rf.getUserCommonService();
				assertNotNull(response.getBranches());
				assertEquals(numBranches, response.getBranches().size());

				// make the restaurant editable
				RestaurantProxy editable = service.edit(response);

				// add course to the restaurant menu

				RestaurantBranchProxy branch = createRestBranch(service, 0, 0);
				branch.setAddress("Dror");
				
				RestaurantBranchProxy branch2 = createRestBranch(service, 0, 0);
				branch.setAddress("Dror2");

				// setup a new pmf for the new call
				setUpPMF();

				
				service.addRestaurantBranch(editable, branch);
				++numBranches;
				
				service.addRestaurantBranch(editable, branch2);
				++numBranches;
				
				service.saveRestaurant(editable).with(RestaurantProxy.REST_WITH).to(new Receiver<RestaurantProxy>()
				{
					@Override
					public void onSuccess(RestaurantProxy response2)
					{
						assertEquals(numBranches, response2.getBranches().size());
					}

					@Override
					public void onFailure(ServerFailure error)
					{
						throw new RuntimeException("failure");
					}
				});
				service.fire();

			}
			
		});
	}

	@Test
	public void addMenuCategoryCourseToRestaurantServiceTest()
	{
		int menuCats = 1;
		int menuCatCourses = 1;
		int numBranches = 0;
		int numBranchMenuCats = 0;
		int numBranchMenuCatCourses = 0;

		FoodCenterRequestFactory rf = createRF(FoodCenterRequestFactory.class);
		UserCommonServiceProxy service = rf.getUserCommonService();

		RestaurantProxy rest = createRest(service, "rest", menuCats, menuCatCourses, numBranches, numBranchMenuCats, numBranchMenuCatCourses);
		service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(new Receiver<RestaurantProxy>()
		{
			@Override
			public void onSuccess(RestaurantProxy response)
			{
				// tear down the pmf, because this is going to be a new RF call
				tearDownPMF();
				FoodCenterRequestFactory rf = createRF(FoodCenterRequestFactory.class);
				UserCommonServiceProxy service = rf.getUserCommonService();
				assertEquals(1, response.getMenu().getCategories().get(0).getCourses().size());

				// make the restaurant editable
				RestaurantProxy editable = service.edit(response);

				// add course to the restaurant menu
				CourseProxy course = service.create(CourseProxy.class);
				course.setName("new");
				course.setPrice(12.2);
				editable.getMenu().getCategories().get(0).getCourses().add(course);
				assertEquals(2, editable.getMenu().getCategories().get(0).getCourses().size());

				// setup a new pmf for the new call
				setUpPMF();

				service.saveRestaurant(editable).fire(new Receiver<RestaurantProxy>()
				{
					@Override
					public void onSuccess(RestaurantProxy response2)
					{
						assertEquals(2, response2.getMenu().getCategories().get(0).getCourses().size());
					}

					@Override
					public void onFailure(ServerFailure error)
					{
						super.onFailure(error);
					}
				});

			}
		});
	}

	private RestaurantBranchProxy createRestBranch(UserCommonServiceProxy service, //
	    int numBranchMenuCats, //
	    int numBranchMenuCatCourses)
	{
		RestaurantBranchProxy branch = RequestUtils.createRestaurantBranchProxy(service);
//		branch.setAddress("addr" + Math.random());
//		for (int j = 0; j < numBranchMenuCats; ++j)
//		{
//
//			MenuCategoryProxy category = RequestUtils.createMenuCategoryProxy(service);
//			category.setCategoryTitle("branch" + Math.random());
//
//			branch.getMenu().getCategories().add(category);
//			for (int k = 0; k < numBranchMenuCatCourses; ++k)
//			{
//				CourseProxy course = service.create(CourseProxy.class);
//				course.setName("branch_course" + Math.random());
//				course.setPrice(12.2 + 10 * Math.random());
//				category.getCourses().add(course);
//			}
//		}

		return branch;
	}

	private RestaurantProxy createRest(//
	    UserCommonServiceProxy service, //
	    String name, //
	    int menuCats, //
	    int menuCatCourses, //
	    int numBranches, //
	    int numBranchMenuCats, //
	    int numBranchMenuCatCourses)
	{

		RestaurantProxy r = RequestUtils.createRestaurantProxy(service);

		for (int i = 0; i < menuCats; ++i)
		{
			MenuCategoryProxy category = RequestUtils.createMenuCategoryProxy(service);
			category.setCategoryTitle("rest" + Math.random());

			r.getMenu().getCategories().add(category);

			for (int j = 0; j < menuCatCourses; ++j)
			{
				CourseProxy course = service.create(CourseProxy.class);
				course.setName("course" + Math.random());
				course.setPrice(12.2 + 10 * Math.random());
				category.getCourses().add(course);
			}
		}

		for (int i = 0; i < numBranches; ++i)
		{
			RestaurantBranchProxy branch = createRestBranch(//
			    service, //
			    numBranchMenuCats, //
			    numBranchMenuCatCourses);

			r.getBranches().add(branch);
//			branch.setRestaurant(r);
		}

		return r;
	}

}
