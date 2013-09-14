package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.server.AbstractGAETest;
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

        CompanyAdminServiceRequest compService = rf.getCompanyAdminService();
        MockTestResponse<CompanyProxy> compResponse = new MockTestResponse<CompanyProxy>();
        CompanyProxy comp = createComp(compService, "comp", numBranches);
        comp.getBranches().get(0).getWorkers().add(AbstractGAETest.email);
        compService.saveCompany(comp).fire(compResponse);
        tearDownPMF();
        setUpPMF();

        ClientServiceRequest service = rf.getClientService();
        MockTestResponse<UserProxy> userResponse = new MockTestResponse<UserProxy>();
        service.login("").fire(userResponse);
        tearDownPMF();
        setUpPMF();

        MockTestResponse<OrderProxy> orderResponse = new MockTestResponse<OrderProxy>();

        RestaurantBranchProxy branch = restResponse.response.getBranches().get(0);        
        service = rf.getClientService();
        OrderProxy order = createOrder(service, branch, numBranchMenuCatCourses);
        service.makeOrder(order).with(OrderProxy.ORDER_WITH).fire(orderResponse);
        assertNull(orderResponse.response);   // fail - no restId, branchId
        tearDownPMF();
        setUpPMF();

        
        service = rf.getClientService();
        order = createOrder(service, restResponse.response.getBranches().get(0), numBranchMenuCatCourses);
        order.setRestBranchId(restResponse.response.getBranches().get(0).getId());
        service.makeOrder(order).with(OrderProxy.ORDER_WITH).fire(orderResponse);
        assertNull(orderResponse.response);   // fail - no restId
        tearDownPMF();
        setUpPMF();

        for (int i = 0; i< 30; ++i)
        {
            service = rf.getClientService();
            order = createOrder(service, restResponse.response.getBranches().get(0), numBranchMenuCatCourses);
            order.setRestBranchId(restResponse.response.getBranches().get(0).getId());
            order.setRestId(restResponse.response.getId());
            service.makeOrder(order).with(OrderProxy.ORDER_WITH).fire(orderResponse);
            assertNotNull(orderResponse.response);
            assertNotNull(orderResponse.response.getCourses());
            assertEquals(numBranchMenuCatCourses, orderResponse.response.getCourses().size());
            tearDownPMF();
            setUpPMF();
        }
        
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
