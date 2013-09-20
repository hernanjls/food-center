package foodcenter.service.request.client;

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
import foodcenter.service.request.AbstractRequestTest;
import foodcenter.service.request.mock.MockTestResponse;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.CompanyAdminServiceRequest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class OrdersRequestTest extends AbstractRequestTest
{

    private RestaurantProxy rest = null;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 2;
        numBranchMenuCats = 3;
        numBranchMenuCatCourses = 8;

        this.rest = addRestaurant();

        addCompany(true);

    }

    @Override
    @After
    public void tearDown()
    {
        rest = null;
        super.tearDown();
    }

    private void login(String gcmKey)
    {
        setUpPMF();

        ClientServiceRequest service = rf.getClientService();
        MockTestResponse<UserProxy> userResponse = new MockTestResponse<UserProxy>();
        service.login(gcmKey).fire(userResponse);

        tearDownPMF();
    }

    private CompanyProxy addCompany(boolean addWorker)
    {
        setUpPMF();

        CompanyAdminServiceRequest compService = rf.getCompanyAdminService();
        MockTestResponse<CompanyProxy> compResponse = new MockTestResponse<CompanyProxy>();
        CompanyProxy comp = createComp(compService, "comp", numBranches);
        if (addWorker)
        {
            comp.getBranches().get(0).getWorkers().add(AbstractGAETest.email);
        }
        compService.saveCompany(comp).fire(compResponse);

        tearDownPMF();
        return compResponse.response;
    }

    private RestaurantProxy addRestaurant()
    {

        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();

        RestaurantProxy rest = createRest(adminService,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);

        return saveRest(adminService, rest, true);
    }

    private OrderProxy
        makeOrder(RestaurantBranchProxy branch, boolean addRestId, boolean addBrachId)
    {
        setUpPMF();

        ClientServiceRequest service = rf.getClientService();
        MockTestResponse<OrderProxy> orderResponse = new MockTestResponse<OrderProxy>();
        OrderProxy order = createOrder(service, branch, numBranchMenuCatCourses);
        if (addRestId)
        {
            order.setRestId(rest.getId());
        }
        if (addBrachId)
        {
            order.setRestBranchId(branch.getId());
        }

        service.makeOrder(order).with(OrderProxy.ORDER_WITH).fire(orderResponse);

        tearDownPMF();

        return orderResponse.response;
    }

    private List<OrderProxy> getOrders(int start, int end)
    {
        setUpPMF();

        ClientServiceRequest service = rf.getClientService();
        MockTestResponse<List<OrderProxy>> ordersResponse = new MockTestResponse<List<OrderProxy>>();

        service.getOrders(start, end).with(OrderProxy.ORDER_WITH).fire(ordersResponse);

        tearDownPMF();

        return ordersResponse.response;
    }

    @Test
    public void makeOrderRequestTestFailNoBranchAndRestId()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        OrderProxy order = makeOrder(branch, false, false); // should fail no rest/branch id
        assertNull(order);
    }

    @Test
    public void makeOrderRequestTestFailNoBranchId()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        OrderProxy order = makeOrder(branch, true, false); // should fail no rest/branch id
        assertNull(order);
    }

    @Test
    public void makeOrderRequestTestFailNoRestId()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        OrderProxy order = makeOrder(branch, false, true); // should fail no rest/branch id
        assertNull(order);
    }

    @Test
    public void makeOrderRegression()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        OrderProxy order;

        for (int i = 0; i < 30; ++i)
        {
            order = makeOrder(branch, true, true);
            assertNotNull(order);
            assertNotNull(order.getCourses());
            assertEquals(numBranchMenuCatCourses, order.getCourses().size());
        }
    }

    @Test
    public void getSingleOrderTest()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        makeOrder(branch, true, true);

        List<OrderProxy> orders = getOrders(0, 20);
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    public void getOrdersTest()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        for (int i = 0; i < 10; ++i)
        {
            OrderProxy order = makeOrder(branch, true, true);
            assertNotNull(order);
        }

        List<OrderProxy> orders = getOrders(0, 20);
        assertNotNull(orders);
        assertEquals(10, orders.size());
    }

    @Test
    public void getNonExistingOrdersTest()
    {
        RestaurantBranchProxy branch = rest.getBranches().get(0);

        for (int i = 0; i < 2; ++i)
        {
            makeOrder(branch, true, true);
        }
        
        List<OrderProxy> orders = getOrders(30, 40);
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    @Test
    public void getOrdersTest2()
    {
        login("");

        RestaurantBranchProxy branch = rest.getBranches().get(0);

        for (int i = 0; i < 30; ++i)
        {
            makeOrder(branch, true, true);
        }

        List<OrderProxy> orders = getOrders(0, 20);
        assertNotNull(orders);
        assertEquals(20, orders.size());
    }

}
