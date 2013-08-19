package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.request.mock.MockTestResponse;
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
    public void saveNewRestaurantServiceTest()
    {
        RestaurantProxy response = null;

        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 2;
        numBranchMenuCats = 3;
        numBranchMenuCatCourses = 8;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        MockTestResponse<RestaurantProxy> testResponse = new MockTestResponse<RestaurantProxy>();
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(testResponse);
        response = testResponse.response;

        // Validate the restaurant values
        assertNotNull(response.getMenu());
        assertNotNull(response.getMenu().getCategories());
        assertEquals(menuCats, response.getMenu().getCategories().size());

        // Validate the branches values
        assertNotNull(response.getBranches());
        assertEquals(numBranches, response.getBranches().size());

        // Validate the branches inner values
        for (int i = 0; i < numBranches; ++i)
        {
            RestaurantBranchProxy branch = response.getBranches().get(i);
            assertNotNull(branch.getMenu());
            assertNotNull(branch.getMenu().getCategories());
            assertEquals(numBranchMenuCats, branch.getMenu().getCategories().size());
            for (int j = 0; j < numBranchMenuCats; ++j)
            {
                MenuCategoryProxy cat = branch.getMenu().getCategories().get(j);
                assertNotNull(cat.getCourses());
                assertEquals(numBranchMenuCatCourses, cat.getCourses().size());
            }
        }

    }

    /**
     * this test will test and demonstrate the flow: <br>
     * restaurant admin adds a branch to already exist restaurant
     * 
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

        MockTestResponse<RestaurantProxy> testResponse = new MockTestResponse<RestaurantProxy>();
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(testResponse);
        // the above line is equals to
        // service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).to(testResponse);
        // service.fire();

        assertNotNull(testResponse.response);

        // tear down the pmf, because this is going to be a new RF call
        tearDownPMF();

        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();

        // make the restaurant editable
        RestaurantProxy editable = adminService.edit(testResponse.response);

        // add course to the restaurant menu

        RestaurantBranchProxy branch = createRestBranch(adminService, 0, 0);
        branch.setAddress("Dror");

        RestaurantBranchProxy branch2 = createRestBranch(adminService, 0, 0);
        branch.setAddress("Dror2");

        testResponse.response = null;

        // editable.getBranches().add(branch);
        adminService.addRestaurantBranch(editable, branch);
        ++numBranches;

        // editable.getBranches().add(branch2);
        adminService.addRestaurantBranch(editable, branch2);
        ++numBranches;

        // next 2 lines are equal to
        // adminService.saveRestaurant(editable).with(RestaurantProxy.REST_WITH).fire(testResponse);
        adminService.saveRestaurant(editable).with(RestaurantProxy.REST_WITH).to(testResponse);

        // setup a new pmf for the new call
        setUpPMF();
        adminService.fire();

        assertNotNull(testResponse.response);
        assertEquals(numBranches, testResponse.response.getBranches().size());

    }

    @Test
    public void addMenuCategoryCourseToRestaurantTest()
    {
        menuCats = 2;
        menuCatCourses = 2;
        RestaurantProxy response = null;
        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService(); // service can
                                                                                // invoke a single
                                                                                // fire!

        /*
         * create a restaurant for the rest of the test
         */
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        MockTestResponse<RestaurantProxy> testResponse = new MockTestResponse<RestaurantProxy>();
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(testResponse); // service
                                                                                         // is dead
                                                                                         // after a
                                                                                         // fire
        response = testResponse.response;
        testResponse.response = null;
        assertNotNull(response);
        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call

        // edit the restaurant and add a new course to the menu 1st category
        service = rf.getRestaurantAdminService(); // service can invoke a single fire!
        RestaurantProxy editable = service.edit(response); // make the restaurant editable
        response = null;

        MenuCategoryProxy cat = editable.getMenu().getCategories().get(0); // get the 1st cat
        CourseProxy course = createCourse(service, "new", 14.0); // create a new course

        // add course to the restaurant menu and save it
        service.addCategoryCourse(cat, course);
        service.saveRestaurant(editable).with(RestaurantProxy.REST_WITH).to(testResponse);

        // invoke the edit ...
        setUpPMF(); // setup a new pmf for the new call
        service.fire(); // fire all the requests...
        response = testResponse.response;
        testResponse.response = null;
        assertNotNull(response);

        // validate it was added only to the 1st category
        assertEquals(menuCatCourses, response.getMenu().getCategories().get(1).getCourses().size());
        assertEquals(menuCatCourses + 1, response.getMenu().getCategories().get(0).getCourses()
            .size());

    }

    /**
     * adds a new category with courses to already existing restaurant branch menu
     */
    @Test
    public void addBranchMenuCategoryToBranchTest()
    {

    }

    /**
     * adds a new course to already existing restaurant branch menu category
     */
    @Test
    public void addBranchMenuCategoryCourseToBranchTest()
    {

    }

    @Test
    public void addRestaurantAdminRequestTest()
    {
        menuCats = 2;
        menuCatCourses = 2;
        RestaurantProxy response = null;
        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService(); // service can
                                                                                // invoke a single
                                                                                // fire!

        /*
         * create a restaurant for the rest of the test
         */
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        MockTestResponse<RestaurantProxy> testResponse = new MockTestResponse<RestaurantProxy>();
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(testResponse); // service
                                                                                         // is dead
                                                                                         // after a
                                                                                         // fire

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        response = testResponse.response;
        testResponse.response = null;

        service = rf.getRestaurantAdminService();
        response = service.edit(response);
        response.getAdmins().add("test@example.com");

        service.saveRestaurant(response).with(RestaurantProxy.REST_WITH).fire(testResponse);

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        response = testResponse.response;
        testResponse.response = null;

        assertNotNull(response.getAdmins());
        assertEquals(1, response.getAdmins().size());
        assertEquals("test@example.com", response.getAdmins().get(0));

        // Add a second admin
        service = rf.getRestaurantAdminService();
        response = service.edit(response);
        response.getAdmins().add("test2@example.com");

        service.saveRestaurant(response).with(RestaurantProxy.REST_WITH).fire(testResponse);

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        response = testResponse.response;
        testResponse.response = null;

        assertNotNull(response.getAdmins());
        assertEquals(2, response.getAdmins().size());
        assertEquals("test@example.com", response.getAdmins().get(0));
        assertEquals("test2@example.com", response.getAdmins().get(1));
    }

    @Test
    public void delRestaurantAdminRequestTest()
    {
        menuCats = 2;
        menuCatCourses = 2;
        RestaurantProxy response = null;
        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService(); // service can
                                                                                // invoke a single
        /*
         * create a restaurant for the rest of the test
         */
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);

        rest.getAdmins().add("admin0@test.com");
        rest.getAdmins().add("admin1@test.com");

        MockTestResponse<RestaurantProxy> testResponse = new MockTestResponse<RestaurantProxy>();
        // service is dead after afire
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(testResponse);
        response = testResponse.response;
        testResponse.response = null;

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        service = rf.getRestaurantAdminService();
        response = service.edit(response);
        response.getAdmins().remove("admin0@test.com");
        service.saveRestaurant(response).with(RestaurantProxy.REST_WITH).fire(testResponse);
        response = testResponse.response;
        testResponse.response = null;

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        assertEquals(1, response.getAdmins().size());

        service = rf.getRestaurantAdminService();
        response = service.edit(response);
        response.getAdmins().remove("admin1@test.com");
        service.saveRestaurant(response).with(RestaurantProxy.REST_WITH).fire(testResponse);
        response = testResponse.response;
        testResponse.response = null;

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        assertEquals(0, response.getAdmins().size());
    }
    
    @Test
    public void addBranchAdminRequestTest()
    {
        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 1;
        numBranchMenuCats = 3;
        numBranchMenuCatCourses = 8;

        MockTestResponse<RestaurantProxy> response = new MockTestResponse<RestaurantProxy>();
        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(response);
        rest = response.response;
        response.response = null;
        
        tearDownPMF();
        setUpPMF();
        
        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        RestaurantBranchProxy branch = rest.getBranches().get(0);
        
        branch.getAdmins().add("admin@test.com");
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(response);
        rest = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        branch = rest.getBranches().get(0);
        assertNotNull(branch.getAdmins());
        assertEquals(1, branch.getAdmins().size());
    }


    @Test
    public void changeServiceRequestTest()
    {
        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 1;
        numBranchMenuCats = 3;
        numBranchMenuCatCourses = 8;

        MockTestResponse<RestaurantProxy> response = new MockTestResponse<RestaurantProxy>();
        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(response);
        rest = response.response;
        response.response = null;
        
        tearDownPMF();
        setUpPMF();
        
        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getServices().add(ServiceType.DELIVERY);
        rest.getServices().add(ServiceType.TAKE_AWAY);
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(response);
        rest = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        assertNotNull(rest.getServices());
        assertEquals(2, rest.getServices().size());
        

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getServices().remove(ServiceType.DELIVERY);
        service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(response);
        rest = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        assertNotNull(rest.getServices());
        assertEquals(1, rest.getServices().size());
    }

}
