package foodcenter.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import foodcenter.server.AbstractGAETest;
import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.modules.DbCourse;
import foodcenter.server.db.modules.DbCourseOrder;
import foodcenter.server.db.modules.DbMenu;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbUser;
import foodcenter.service.enums.ServiceType;

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
        setUpPMF();
        // get login info
        DbUser user = ClientService.login(null);
        tearDownPMF();

        // check that email was returned
        assertEquals(email, user.getEmail());

        // check that the db only contains the user
        setUpPMF();
        List<DbUser> users = DbHandler.find(DbUser.class, null, null, null, null);
        tearDownPMF();

        assertEquals(1, users.size());
        assertEquals(email, user.getEmail());

    }

    /**
     * checks that gcm key was updated on second login
     */
    @Test
    public void loginTestDoesnDuplicateUsersTest()
    {
        setUpPMF();
        // get login info
        ClientService.login(null);
        tearDownPMF();

        // get login info
        setUpPMF();
        DbUser user = ClientService.login(null);
        tearDownPMF();

        // check that email was returned
        assertEquals(email, user.getEmail());

        // check that there is only 1 user in our db
        setUpPMF();
        List<DbUser> users = DbHandler.find(DbUser.class, null, null, null, null);
        tearDownPMF();

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
        setUpPMF();
        DbUser user = ClientService.login(gcmKey);
        tearDownPMF();

        // check that gcm was returned
        assertEquals(gcmKey, user.getGcmKey());

        String gcmKey2 = "gcmKey2";
        setUpPMF();
        user = ClientService.login(gcmKey2);
        tearDownPMF();

        // check that gcm was returned
        assertEquals(gcmKey2, user.getGcmKey());

        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));

        setUpPMF();
        user = DbHandler.find(DbUser.class, query, params);
        tearDownPMF();
        
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
        setUpPMF();
        DbUser user = ClientService.login(gcmKey);
        tearDownPMF();
        
        // logout
        setUpPMF();
        ClientService.logout();
        tearDownPMF();

        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));

        // make sure the GCM key was removed
        setUpPMF();
        user = DbHandler.find(DbUser.class, query, params);
        tearDownPMF();

        assertNotNull(user);
        assertEquals("", user.getGcmKey());
    }

    @Test
    public void makeOrderWithoutLoginTest()
    {
        int numMenuCats = 1;
        int numMenuCourses = 2;
        int numBranches = 1;
        int numBranchMenuCats = numMenuCats;
        int numBranchMenuCourses = numMenuCourses;

        DbRestaurant rest = createRest("rest",
                                       numMenuCats,
                                       numMenuCourses,
                                       numBranches,
                                       numBranchMenuCats,
                                       numBranchMenuCourses);

        setUpPMF();
        rest = RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();

        DbCompany comp = createComp("comp", numBranches);
        comp.getBranches().get(0).getWorkers().add(AbstractGAETest.email);

        setUpPMF();
        comp = CompanyAdminService.saveCompany(comp);
        tearDownPMF();
        
        DbMenu branchMenu = rest.getBranches().get(0).getMenu();

        // Create an order and fill it with all the courses from branch menu to the order
        DbOrder order = new DbOrder();
        for (int i = 0; i < numBranchMenuCourses; ++i)
        {
            DbCourse course = branchMenu.getCategories().get(0).getCourses().get(i);
            DbCourseOrder courseOrder = createOrder(course, 1);
            order.getCourses().add(courseOrder);
        }

        // save the order
        setUpPMF();
        DbOrder result = ClientService.makeOrder(order);
        tearDownPMF();
        assertNull(result);
    }

    @Test
    public void makeOrderWithoutCompanyTest()
    {
        setUpPMF();
        ClientService.login(null);
        tearDownPMF();

        int numMenuCats = 1;
        int numMenuCourses = 2;
        int numBranches = 1;
        int numBranchMenuCats = numMenuCats;
        int numBranchMenuCourses = numMenuCourses;

        DbRestaurant rest = createRest("rest",
                                       numMenuCats,
                                       numMenuCourses,
                                       numBranches,
                                       numBranchMenuCats,
                                       numBranchMenuCourses);
        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();
        

        DbMenu branchMenu = rest.getBranches().get(0).getMenu();

        // Create an order and fill it with all the courses from branch menu to the order
        DbOrder order = new DbOrder();
        for (int i = 0; i < numBranchMenuCourses; ++i)
        {
            DbCourse course = branchMenu.getCategories().get(0).getCourses().get(i);
            DbCourseOrder courseOrder = createOrder(course, 1);
            order.getCourses().add(courseOrder);
        }

        // save the order
        setUpPMF();
        DbOrder result = ClientService.makeOrder(order);
        tearDownPMF();
        
        assertNull(result);
    }

    /**
     * test if the user can make an order
     */
    @Test
    public void makeOrderTest()
    {
        int numMenuCats = 1;
        int numMenuCourses = 2;
        int numBranches = 1;
        int numBranchMenuCats = numMenuCats;
        int numBranchMenuCourses = numMenuCourses;

        DbRestaurant rest = createRest("rest",
                                       numMenuCats,
                                       numMenuCourses,
                                       numBranches,
                                       numBranchMenuCats,
                                       numBranchMenuCourses);
        setUpPMF();
        rest = RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();
        
        setUpPMF();
        ClientService.login(null);
        tearDownPMF();

        DbCompany comp = createComp("comp", numBranches);
        comp.getBranches().get(0).getWorkers().add(AbstractGAETest.email);
        
        setUpPMF();
        comp = CompanyAdminService.saveCompany(comp);
        tearDownPMF();
        setUpPMF();

        DbMenu branchMenu = rest.getBranches().get(0).getMenu();

        // Create an order and fill it with all the courses from branch menu to the order
        DbOrder order = new DbOrder();
        for (int i = 0; i < numBranchMenuCourses; ++i)
        {
            DbCourse course = branchMenu.getCategories().get(0).getCourses().get(i);
            DbCourseOrder courseOrder = createOrder(course, 1);

            order.getCourses().add(courseOrder);
        }

        // fail because there is no rest id
        setUpPMF();
        DbOrder result = ClientService.makeOrder(order);
        tearDownPMF();
        assertNull(result);
        
        // fail because there is no rest branch id
        order.setRestId(rest.getId());
        setUpPMF();
        result = ClientService.makeOrder(order);
        tearDownPMF();
        
        assertNull(result);

        // Success
        order.setRestBranchId(rest.getBranches().get(0).getId());
        setUpPMF();
        result = ClientService.makeOrder(order);
        tearDownPMF();
        
        assertNotNull(result);

    }

    @Test
    public void getOrdersTest()
    {
        makeOrderTest();
        
        setUpPMF();
        List<DbOrder> orders = ClientService.getOrders(0, 100);
        tearDownPMF();

        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    /**
     * tests that after saving 2 restaurants we can retrieve them both
     */
    @Test
    public void getDefaultRestTest()
    {
        int numMenuCats = 1;
        int numMenuCourses = 2;
        int numBranches = 1;
        int numBranchMenuCats = numMenuCats;
        int numBranchMenuCourses = numMenuCourses;

        DbRestaurant rest = createRest("rest",
                                       numMenuCats,
                                       numMenuCourses,
                                       numBranches,
                                       numBranchMenuCats,
                                       numBranchMenuCourses);
        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();
        
        DbRestaurant rest2 = createRest("rest2",
                                        numMenuCats,
                                        numMenuCourses,
                                        numBranches,
                                        numBranchMenuCats,
                                        numBranchMenuCourses);
        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest2);
        tearDownPMF();
        
        setUpPMF();
        List<DbRestaurant> rests = ClientService.getDefaultRestaurants();
        tearDownPMF();
        
        assertNotNull(rests);
        assertEquals(2, rests.size());

        assertEquals(rest.getName(), rests.get(0).getName());
        assertEquals(rest2.getName(), rests.get(1).getName());

        for (int i = 0; i < 2; ++i)
        {
            assertEquals(rests.get(i).isEditable(), true);
            assertNotNull(rests.get(i).getMenu());
            assertNotNull(rests.get(i).getMenu().getCategories());
        }
    }

    /**
     * tests that after saving 2 restaurants we can retrieve them both
     */
    @Test
    public void getDefaultCompsTest()
    {
        int numBranches = 1;

        DbCompany c1 = createComp("c1", numBranches);
        setUpPMF();
        CompanyAdminService.saveCompany(c1);
        tearDownPMF();
        
        
        DbCompany c2 = createComp("c2", numBranches);
        setUpPMF();
        CompanyAdminService.saveCompany(c2);
        tearDownPMF();

        setUpPMF();
        List<DbCompany> comps = ClientService.getDefaultCompanies();
        tearDownPMF();
        
        assertNotNull(comps);
        assertEquals(2, comps.size());

        assertEquals(c1.getName(), comps.get(0).getName());
        assertEquals(c2.getName(), comps.get(1).getName());

        for (int i = 0; i < 2; ++i)
        {
            assertEquals(comps.get(i).isEditable(), true);
        }
    }

    @Test
    public void findRestsTest()
    {
        int numMenuCats = 1;
        int numMenuCourses = 2;
        int numBranches = 1;
        int numBranchMenuCats = numMenuCats;
        int numBranchMenuCourses = numMenuCourses;

        DbRestaurant rest = createRest("rest",
                                       numMenuCats,
                                       numMenuCourses,
                                       numBranches,
                                       numBranchMenuCats,
                                       numBranchMenuCourses);
        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();

        DbRestaurant rest2 = createRest("dror",
                                        numMenuCats,
                                        numMenuCourses,
                                        numBranches,
                                        numBranchMenuCats,
                                        numBranchMenuCourses);

        rest2.getServices().add(ServiceType.DELIVERY);
        rest2.getServices().add(ServiceType.TAKE_AWAY);
        
        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest2);
        tearDownPMF();

        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        
        setUpPMF();
        List<DbRestaurant> rests = ClientService.findRestaurant(rest2.getName(), services);
        tearDownPMF();
        
        assertNotNull(rests);
        assertEquals(1, rests.size());
    }

    @Test
    public void findCompsTest()
    {
        int numBranches = 1;

        DbCompany comp = createComp("c1", numBranches);
        
        setUpPMF();
        CompanyAdminService.saveCompany(comp);
        tearDownPMF();
        

        DbCompany comp2 = createComp("dror", numBranches);
        comp2.getServices().add(ServiceType.DELIVERY);
        comp2.getServices().add(ServiceType.TAKE_AWAY);
        
        setUpPMF();
        CompanyAdminService.saveCompany(comp2);
        tearDownPMF();
        
        
        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);

        setUpPMF();
        List<DbCompany> comps = ClientService.findCompany(comp2.getName(), services);
        tearDownPMF();
        
        assertNotNull(comps);
        assertEquals(1, comps.size());
    }

    @Test
    public void findRestsPatternTest()
    {
        int numMenuCats = 1;
        int numMenuCourses = 2;
        int numBranches = 1;
        int numBranchMenuCats = numMenuCats;
        int numBranchMenuCourses = numMenuCourses;
        String name = "rest";

        DbRestaurant rest = createRest(name + 1,
                                       numMenuCats,
                                       numMenuCourses,
                                       numBranches,
                                       numBranchMenuCats,
                                       numBranchMenuCourses);
        rest.getServices().add(ServiceType.DELIVERY);
        rest.getServices().add(ServiceType.TAKE_AWAY);

        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();
        
        
        DbRestaurant rest2 = createRest(name + 2,
                                        numMenuCats,
                                        numMenuCourses,
                                        numBranches,
                                        numBranchMenuCats,
                                        numBranchMenuCourses);

        rest2.getServices().add(ServiceType.DELIVERY);
        rest2.getServices().add(ServiceType.TAKE_AWAY);
        setUpPMF();
        RestaurantAdminService.saveRestaurant(rest2);
        tearDownPMF();
        

        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        
        setUpPMF();
        List<DbRestaurant> rests = ClientService.findRestaurant("rest", services);
        tearDownPMF();
        
        assertNotNull(rests);
        assertEquals(2, rests.size());

        setUpPMF();
        rests = ClientService.findRestaurant("FDASDASDA", services);
        tearDownPMF();
        
        assertNotNull(rests);
        assertEquals(0, rests.size());
    }

    @Test
    public void findCompsPatternTest()
    {
        int numBranches = 1;

        String name = "comp";

        DbCompany comp = createComp(name + 1, numBranches);
        comp.getServices().add(ServiceType.DELIVERY);
        comp.getServices().add(ServiceType.TAKE_AWAY);
        setUpPMF();
        CompanyAdminService.saveCompany(comp);
        tearDownPMF();
        

        DbCompany rest2 = createComp(name + 2, numBranches);
        rest2.getServices().add(ServiceType.DELIVERY);
        rest2.getServices().add(ServiceType.TAKE_AWAY);
        
        setUpPMF();
        CompanyAdminService.saveCompany(rest2);
        tearDownPMF();
        
        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        setUpPMF();
        List<DbCompany> comps = ClientService.findCompany(name, services);
        tearDownPMF();
        
        assertNotNull(comps);
        assertEquals(2, comps.size());

        setUpPMF();
        comps = ClientService.findCompany("FDASDASDA", services);
        tearDownPMF();
        
        assertNotNull(comps);
        assertEquals(0, comps.size());
    }

    @Test
    public void findUserCompanyFailTest()
    {
        // get login info
        setUpPMF();
        DbUser user = ClientService.login(null);
        tearDownPMF();
        
        setUpPMF();
        DbCompanyBranch b = ClientService.findUserCompanyBranch(user.getEmail());
        tearDownPMF();
        
        assertNull(b);
    }

    @Test
    public void findUserCompanyAndBranchTest()
    {
        // get login info
        setUpPMF();
        DbUser user = ClientService.login(null);
        tearDownPMF();

        int numBranches = 1;

        String name = "comp";
        DbCompany comp = createComp(name + 1, numBranches);
        comp.getServices().add(ServiceType.DELIVERY);
        comp.getServices().add(ServiceType.TAKE_AWAY);

        name = "comp2";
        DbCompany comp2 = createComp(name + 1, numBranches);
        comp.getServices().add(ServiceType.DELIVERY);
        comp.getServices().add(ServiceType.TAKE_AWAY);

        comp.getBranches().get(0).getWorkers().add(user.getEmail()); // add the user

        
        setUpPMF();
        CompanyAdminService.saveCompany(comp);
        CompanyAdminService.saveCompany(comp2);
        tearDownPMF();
        
        setUpPMF();
        DbCompanyBranch b = ClientService.findUserCompanyBranch(user.getEmail());
        DbCompany c = b.getCompany();
        tearDownPMF();
        
        assertNotNull(b);
        assertEquals(comp.getBranches().get(0).getId(), b.getId());        
        assertNotNull(c);
        assertEquals(comp.getId(), c.getId());
        
    }

}
