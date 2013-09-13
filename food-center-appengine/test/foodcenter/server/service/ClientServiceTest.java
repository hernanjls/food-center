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
        // get login info
        DbUser user = ClientService.login(null);

        // check that email was returned
        assertEquals(email, user.getEmail());

        // check that the db only contains the user
        List<DbUser> users = DbHandler.find(DbUser.class, null, null, null, null);
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
        ClientService.login(null);
        // get login info
        DbUser user = ClientService.login(null);
        // check that email was returned
        assertEquals(email, user.getEmail());
        // check that there is only 1 user in our db
        List<DbUser> users = DbHandler.find(DbUser.class, null, null, null, null);
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
        DbUser user = ClientService.login(gcmKey);
        // check that gcm was returned
        assertEquals(gcmKey, user.getGcmKey());

        String gcmKey2 = "gcmKey2";
        user = ClientService.login(gcmKey2);

        // check that gcm was returned
        assertEquals(gcmKey2, user.getGcmKey());

        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));

        user = DbHandler.find(DbUser.class, query, params);

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
        DbUser user = ClientService.login(gcmKey);

        // logout
        ClientService.logout();

        String query = "email == emailP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("emailP", email));

        // make sure the GCM key was removed
        user = DbHandler.find(DbUser.class, query, params);
        
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
        RestaurantAdminService.saveRestaurant(rest);
        tearDownPMF();
        setUpPMF();

        DbCompany comp = createComp("comp", numBranches);
        comp.getBranches().get(0).getWorkers().add(AbstractGAETest.email);
        
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
        DbOrder result = ClientService.makeOrder(order);
        assertNull(result);
    }

    @Test
    public void makeOrderWithoutCompanyTest()
    {
        ClientService.login(null);
        tearDownPMF();
        setUpPMF();

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
        RestaurantAdminService.saveRestaurant(rest);
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

        // save the order
        DbOrder result = ClientService.makeOrder(order);
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
        rest = RestaurantAdminService.saveRestaurant(rest);

        tearDownPMF();
        setUpPMF();

        ClientService.login(null);
        tearDownPMF();
        setUpPMF();

        DbCompany comp = createComp("comp", numBranches);
        comp.getBranches().get(0).getWorkers().add(AbstractGAETest.email);
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

        // save the order
        DbOrder result = ClientService.makeOrder(order);
        assertNotNull(result);
    }

    
    @Test
    public void getOrdersTest()
    {
        makeOrderTest();
        tearDownPMF();
        setUpPMF();
        
        List<DbOrder> orders = ClientService.getOrders(0, 100);
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
        RestaurantAdminService.saveRestaurant(rest);

        tearDownPMF();
        setUpPMF();

        DbRestaurant rest2 = createRest("rest2",
                                        numMenuCats,
                                        numMenuCourses,
                                        numBranches,
                                        numBranchMenuCats,
                                        numBranchMenuCourses);
        RestaurantAdminService.saveRestaurant(rest2);

        tearDownPMF();
        setUpPMF();

        List<DbRestaurant> rests = ClientService.getDefaultRestaurants();
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
        CompanyAdminService.saveCompany(c1);

        tearDownPMF();
        setUpPMF();

        DbCompany c2 = createComp("c2", numBranches);
        CompanyAdminService.saveCompany(c2);

        tearDownPMF();
        setUpPMF();

        List<DbCompany> comps = ClientService.getDefaultCompanies();
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
        RestaurantAdminService.saveRestaurant(rest);

        tearDownPMF();
        setUpPMF();

        DbRestaurant rest2 = createRest("dror",
                                        numMenuCats,
                                        numMenuCourses,
                                        numBranches,
                                        numBranchMenuCats,
                                        numBranchMenuCourses);

        rest2.getServices().add(ServiceType.DELIVERY);
        rest2.getServices().add(ServiceType.TAKE_AWAY);
        RestaurantAdminService.saveRestaurant(rest2);

        tearDownPMF();
        setUpPMF();

        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        List<DbRestaurant> rests = ClientService.findRestaurant(rest2.getName(), services);
        assertNotNull(rests);
        assertEquals(1, rests.size());
    }

    @Test
    public void findCompsTest()
    {
        int numBranches = 1;

        DbCompany rest = createComp("c1", numBranches);
        CompanyAdminService.saveCompany(rest);

        tearDownPMF();
        setUpPMF();

        DbCompany comp2 = createComp("dror", numBranches);

        comp2.getServices().add(ServiceType.DELIVERY);
        comp2.getServices().add(ServiceType.TAKE_AWAY);
        CompanyAdminService.saveCompany(comp2);

        tearDownPMF();
        setUpPMF();

        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        List<DbCompany> comps = ClientService.findCompany(comp2.getName(), services);
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
        RestaurantAdminService.saveRestaurant(rest);

        tearDownPMF();
        setUpPMF();

        DbRestaurant rest2 = createRest(name + 2,
                                        numMenuCats,
                                        numMenuCourses,
                                        numBranches,
                                        numBranchMenuCats,
                                        numBranchMenuCourses);

        rest2.getServices().add(ServiceType.DELIVERY);
        rest2.getServices().add(ServiceType.TAKE_AWAY);
        RestaurantAdminService.saveRestaurant(rest2);

        tearDownPMF();
        setUpPMF();

        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        List<DbRestaurant> rests = ClientService.findRestaurant("rest", services);
        assertNotNull(rests);
        assertEquals(2, rests.size());

        rests = ClientService.findRestaurant("FDASDASDA", services);
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
        CompanyAdminService.saveCompany(comp);

        tearDownPMF();
        setUpPMF();

        DbCompany rest2 = createComp(name + 2, numBranches);

        rest2.getServices().add(ServiceType.DELIVERY);
        rest2.getServices().add(ServiceType.TAKE_AWAY);
        CompanyAdminService.saveCompany(rest2);

        tearDownPMF();
        setUpPMF();

        List<ServiceType> services = new ArrayList<ServiceType>();
        services.add(ServiceType.DELIVERY);
        List<DbCompany> comps = ClientService.findCompany(name, services);
        assertNotNull(comps);
        assertEquals(2, comps.size());

        comps = ClientService.findCompany("FDASDASDA", services);
        assertNotNull(comps);
        assertEquals(0, comps.size());
    }


    @Test
    public void findUserCompanyFailTest()
    {    
        // get login info
        DbUser user = ClientService.login(null);
        tearDownPMF();
        setUpPMF();
        
        DbCompanyBranch b = ClientService.findUserCompanyBranch(user.getEmail());
        assertNull(b);
        
        tearDownPMF();
        setUpPMF();

        DbCompany c = ClientService.findCompanyOfBranch(b);
        assertNull(c);
    }

    @Test
    public void findUserCompanyAndBranchTest()
    {    
        // get login info
        DbUser user = ClientService.login(null);
        tearDownPMF();
        setUpPMF();

        
        int numBranches = 1;

        String name = "comp";

        DbCompany comp = createComp(name + 1, numBranches);
        comp.getServices().add(ServiceType.DELIVERY);
        comp.getServices().add(ServiceType.TAKE_AWAY);
        
        comp.getBranches().get(0).getWorkers().add(user.getEmail()); // add the user
        
        CompanyAdminService.saveCompany(comp);

        tearDownPMF();
        setUpPMF();
        
        DbCompanyBranch b = ClientService.findUserCompanyBranch(user.getEmail());
        assertNotNull(b);
        assertEquals(comp.getBranches().get(0).getId(), b.getId());

        tearDownPMF();
        setUpPMF();

        DbCompany c = ClientService.findCompanyOfBranch(b);
        assertNotNull(c);
        assertEquals(comp.getId(), c.getId());
    }

}
