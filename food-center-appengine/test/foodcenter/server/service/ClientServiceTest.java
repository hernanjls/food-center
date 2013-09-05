package foodcenter.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCourse;
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

        user = DbHandler.find(DbUser.class,
                              "email == emailP",
                              "String emailP",
                              new Object[] { email });
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

        // make sure the GCM key was removed
        user = DbHandler.find(DbUser.class,
                              "email == emailP",
                              "String emailP",
                              new Object[] { email });
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

        DbMenu branchMenu = rest.getBranches().get(0).getMenu();

        // Create an order and fill it with all the courses from branch menu to the order
        DbOrder order = new DbOrder();
        for (int i = 0; i < numBranchMenuCourses; ++i)
        {
            DbCourse course = branchMenu.getCategories().get(0).getCourses().get(i);
            order.getCourses().add(course.getId());
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
        RestaurantAdminService.saveRestaurant(rest);

        tearDownPMF();
        setUpPMF();

        String gcmKey = "hila";
        // login with GCM key
        DbUser user = ClientService.login(gcmKey);
        assertNotNull(user);

        tearDownPMF();
        setUpPMF();

        DbMenu branchMenu = rest.getBranches().get(0).getMenu();

        // Create an order and fill it with all the courses from branch menu to the order
        DbOrder order = new DbOrder();
        for (int i = 0; i < numBranchMenuCourses; ++i)
        {
            DbCourse course = branchMenu.getCategories().get(0).getCourses().get(i);

            order.getCourses().add(course.getId());
        }

        // save the order
        DbOrder result = ClientService.makeOrder(order);
        assertNotNull(result);

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
        
        for (int i=0; i<2; ++i)
        {
            assertEquals(rests.get(i).isEditable(), true);
            assertNotNull(rests.get(i).getMenu());
            assertNotNull(rests.get(i).getMenu().getCategories());
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
}
