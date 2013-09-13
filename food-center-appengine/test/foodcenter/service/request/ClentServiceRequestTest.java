package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.request.mock.MockTestResponse;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.CompanyAdminServiceRequest;
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

        //FIXME this test failes
        MockTestResponse<RestaurantProxy> restResponse = new MockTestResponse<RestaurantProxy>();
        adminService.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(restResponse);
        tearDownPMF();
        setUpPMF();

        
        ClientServiceRequest service = rf.getClientService();
        MockTestResponse<UserProxy> userResponse = new MockTestResponse<UserProxy>();
        service.login("").fire(userResponse);
        tearDownPMF();
        setUpPMF();

        RestaurantBranchProxy branch = restResponse.response.getBranches().get(0);
        
        service = rf.getClientService();
        OrderProxy order = createOrder(service, branch, numBranchMenuCatCourses);
        MockTestResponse<OrderProxy> orderResponse = new MockTestResponse<OrderProxy>();
        service.makeOrder(order).with(OrderProxy.ORDER_WITH).fire(orderResponse);
        tearDownPMF();
        setUpPMF();

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
        clientService.getDefaultRestaurants().with(RestaurantProxy.REST_WITH)
            .fire(getDefaultResponse);

        tearDownPMF();

        assertNotNull(getDefaultResponse.response);
        assertEquals(2, getDefaultResponse.response.size());

        assertEquals(rest.getName(), getDefaultResponse.response.get(0).getName());
        assertEquals(rest2.getName(), getDefaultResponse.response.get(1).getName());

        for (int i = 0; i < 2; ++i)
        {
            RestaurantProxy r = getDefaultResponse.response.get(i);
            assertEquals(r.isEditable(), true);
            assertNotNull(r.getMenu());
            assertNotNull(r.getMenu().getCategories());
            assertEquals(menuCats, r.getMenu().getCategories().size());

        }
    }

    /**
     * tests that after saving 2 companies we can retrieve them both
     */
    @Test
    public void getDefaultCompRequestTest()
    {
        numBranches = 2;

        CompanyAdminServiceRequest adminService = rf.getCompanyAdminService();

        CompanyProxy comp = createComp(adminService, "comp", numBranches);
        MockTestResponse<CompanyProxy> restResponse = new MockTestResponse<CompanyProxy>();
        adminService.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(restResponse);

        tearDownPMF();
        setUpPMF();

        adminService = rf.getCompanyAdminService();
        CompanyProxy comp2 = createComp(adminService, "comp2", numBranches);
        MockTestResponse<CompanyProxy> restResponse2 = new MockTestResponse<CompanyProxy>();
        adminService.saveCompany(comp2).with(CompanyProxy.COMP_WITH).fire(restResponse2);

        tearDownPMF();
        setUpPMF();

        ClientServiceRequest clientService = rf.getClientService();
        MockTestResponse<List<CompanyProxy>> getDefaultResponse = new MockTestResponse<List<CompanyProxy>>();
        clientService.getDefaultCompanies().with(CompanyProxy.COMP_WITH)
            .fire(getDefaultResponse);

        tearDownPMF();

        assertNotNull(getDefaultResponse.response);
        assertEquals(2, getDefaultResponse.response.size());

        assertEquals(comp.getName(), getDefaultResponse.response.get(0).getName());
        assertEquals(comp2.getName(), getDefaultResponse.response.get(1).getName());

        for (int i = 0; i < 2; ++i)
        {
            CompanyProxy r = getDefaultResponse.response.get(i);
            assertEquals(r.isEditable(), true);
        }
    }

}
