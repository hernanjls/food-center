package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.request.mock.MockTestResponse;
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
        MockTestResponse<UserProxy> userResponse = new MockTestResponse<UserProxy>();
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

        RestaurantProxy rest = createRest(adminService,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        MockTestResponse<RestaurantProxy> restResponse = new MockTestResponse<RestaurantProxy>();
        adminService.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(restResponse);

        // tear down the pmf, because this is going to be a new RF call
        tearDownPMF();

        ClientServiceRequest service = rf.getClientService();
        MockTestResponse<UserProxy> userResponse = new MockTestResponse<UserProxy>();

        // setup a new pmf for the new call
        setUpPMF();
        service.login("").fire(userResponse);
        // tear down the pmf, because this is going to be a new RF call
        tearDownPMF();

        ClientServiceRequest clientService = rf.getClientService();
        RestaurantBranchProxy branch = restResponse.response.getBranches().get(0);
        OrderProxy order = createOrder(clientService, branch, numBranchMenuCatCourses);
        MockTestResponse<OrderProxy> orderResponse = new MockTestResponse<OrderProxy>();

        // setup a new pmf for the new call
        setUpPMF();
        clientService.makeOrder(order).fire(orderResponse);

        response = orderResponse.response;
        assertNotNull(response);
        assertNotNull(response.getCourses());
        assertEquals(numBranchMenuCatCourses, response.getCourses().size());
    }

    /**
     * tests that after saving 2 restaurants we can retrieve them both
     */
    @Test
    public void getDefaultRestRequestTest()
    {
        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 2;
        numBranchMenuCats = 3;
        numBranchMenuCatCourses = 8;

        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();

        RestaurantProxy rest = createRest(adminService,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        MockTestResponse<RestaurantProxy> restResponse = new MockTestResponse<RestaurantProxy>();
        adminService.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(restResponse);

        tearDownPMF();
        setUpPMF();

        adminService = rf.getRestaurantAdminService();
        RestaurantProxy rest2 = createRest(adminService,
                                           "rest2",
                                           menuCats,
                                           menuCatCourses,
                                           numBranches,
                                           numBranchMenuCats,
                                           numBranchMenuCatCourses);
        MockTestResponse<RestaurantProxy> restResponse2 = new MockTestResponse<RestaurantProxy>();
        adminService.saveRestaurant(rest2).with(RestaurantProxy.REST_WITH).fire(restResponse2);

        tearDownPMF();
        setUpPMF();

        ClientServiceRequest clientService = rf.getClientService();
        MockTestResponse<List<RestaurantProxy>> getDefaultResponse = new MockTestResponse<List<RestaurantProxy>>();
        clientService.getDefaultRestaurants().with(RestaurantProxy.REST_WITH).fire(getDefaultResponse);
        
        tearDownPMF();

        assertNotNull(getDefaultResponse.response);
        assertEquals(2, getDefaultResponse.response.size());

        assertEquals(rest.getName(), getDefaultResponse.response.get(0).getName());
        assertEquals(rest2.getName(), getDefaultResponse.response.get(1).getName());
        
        for (int i=0; i<2; ++i)
        {
            RestaurantProxy r = getDefaultResponse.response.get(i);
            assertEquals(r.isEditable(), true);
            assertNotNull(r.getMenu());
            assertNotNull(r.getMenu().getCategories());
            assertEquals(menuCats, r.getMenu().getCategories().size());
            
        }
    }

}
