package foodcenter.service.request.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.request.AbstractRequestTest;
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

        rest = saveRest(service, rest, true);
        // Validate the restaurant values
        assertNotNull(rest);
        assertNotNull(rest.getMenu());
        assertNotNull(rest.getMenu().getCategories());
        assertEquals(menuCats, rest.getMenu().getCategories().size());

        // Validate the branches values
        assertNotNull(rest.getBranches());
        assertEquals(numBranches, rest.getBranches().size());

        // Validate the branches inner values
        for (int i = 0; i < numBranches; ++i)
        {
            RestaurantBranchProxy branch = rest.getBranches().get(i);
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
    public void addRestaurantBranchServiceTestFailWithoutCommit()
    {
        numBranches = 1;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);

        rest = saveRest(service, rest, true);

        assertNotNull(rest);

        service = rf.getRestaurantAdminService(); // new service (service can be invoked once)
        rest = service.edit(rest); // make the restaurant editable

        RestaurantBranchProxy branch = createRestBranch(service, 0, 0);
        branch.setAddress("Dror");
        MockTestResponse<Void> mock = new MockTestResponse<Void>();
        
        setUpPMF();
        service.addRestaurantBranch(rest, branch).fire(mock);
        tearDownPMF();
        
        rest = getRestById(rest.getId(), true);
        
        assertNotNull(rest);
        assertEquals(numBranches, rest.getBranches().size());
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

        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);

        rest = saveRest(service, rest, true);

        assertNotNull(rest);

        service = rf.getRestaurantAdminService(); // new service (service can be invoked once)
        rest = service.edit(rest); // make the restaurant editable

        // add course to the restaurant menu
        for (int i = 0; i < 2; ++i)
        {
            RestaurantBranchProxy branch = createRestBranch(service, 0, 0);
            branch.setAddress("Dror" + i);
            service.addRestaurantBranch(rest, branch);
            ++numBranches;
        }

        rest = saveRest(service, rest, true);

        assertNotNull(rest);
        assertEquals(numBranches, rest.getBranches().size());
    }

    @Test
    public void delRestMenuCategoryTest()
    {
        menuCats = 2;
        menuCatCourses = 2;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        // create a restaurant for the rest of the test and save it
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        rest = saveRest(service, rest, true);

        // Remove a menu category
        service = rf.getRestaurantAdminService();

        rest = service.edit(rest);
        MenuProxy menu = rest.getMenu();

        MenuCategoryProxy cat = menu.getCategories().get(0);
        // menu.getCategories().remove(cat); this will cause exception on server
        service.removeMenuCategory(menu, cat);

        rest = saveRest(service, rest, false);

        rest = getRestById(rest.getId(), true);

        assertEquals(menuCats - 1, rest.getMenu().getCategories().size());
    }

    /**
     * you can add and delete proxy on the same service req (same transaction)
     */
    @Test
    public void addDelMenuCatOnSingleReqFail()
    {
        menuCats = 2;
        menuCatCourses = 2;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        // create a restaurant for the rest of the test and save it
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        rest = saveRest(service, rest, true);

        // Remove a menu category
        service = rf.getRestaurantAdminService();

        rest = service.edit(rest);
        MenuProxy menu = rest.getMenu();

        MenuCategoryProxy cat = createMenuCat(service, 0);

        menu.getCategories().add(cat); // optional
        service.addMenuCategory(menu, cat);

        menu.getCategories().remove(cat); // optional
        service.removeMenuCategory(menu, cat);

        rest = saveRest(service, rest, true);
        assertNotNull(rest); 
        assertEquals(menuCats, rest.getMenu().getCategories().size());
    }
    
    @Test
    public void delRestMenuCategoryCourseTestFail()
    {
        menuCats = 2;
        menuCatCourses = 2;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        // create a restaurant for the rest of the test and save it
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        rest = saveRest(service, rest, true);

        // Remove a menu category
        service = rf.getRestaurantAdminService();

        rest = service.edit(rest);
        MenuProxy menu = rest.getMenu();

        MenuCategoryProxy cat = menu.getCategories().get(0);
        CourseProxy course = cat.getCourses().get(0);

        cat.getCourses().remove(course); // this will cause unrecoverable exception on server
        rest = saveRest(service, rest, false);
        rest = getRestById(rest.getId(), true);

        assertNull(rest);
    }

    @Test
    public void delRestMenuCategoryCourseTest()
    {
        menuCats = 2;
        menuCatCourses = 2;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        // create a restaurant for the rest of the test and save it
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        rest = saveRest(service, rest, true);

        // Remove a menu category
        service = rf.getRestaurantAdminService();

        rest = service.edit(rest);
        MenuProxy menu = rest.getMenu();

        MenuCategoryProxy cat = menu.getCategories().get(0);
        CourseProxy course = cat.getCourses().get(0);

//        cat.getCourses().remove(course); // this will cause exception on server
        service.removeCategoryCourse(cat, course);

        rest = saveRest(service, rest, false);

        rest = getRestById(rest.getId(), true);

        assertEquals(menuCatCourses - 1, rest.getMenu().getCategories().get(0).getCourses().size());
    }

    @Test
    public void addMenuCategoryCourseToRestaurantTest()
    {
        menuCats = 2;
        menuCatCourses = 2;

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();
        // service can invoke a single fire!

        // create a restaurant for the rest of the test and save it
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        rest = saveRest(service, rest, true);

        // edit the restaurant and add a new course to the menu 1st category
        service = rf.getRestaurantAdminService(); // service can invoke a single fire!
        rest = service.edit(rest); // make the restaurant editable

        MenuCategoryProxy cat = rest.getMenu().getCategories().get(0); // get the 1st cat
        CourseProxy course = createCourse(service, "new", 14.0); // create a new course

        // add course to the restaurant menu and save it
        service.addCategoryCourse(cat, course);
        rest = saveRest(service, rest, true);

        // validate it was added only to the 1st category
        assertEquals(menuCatCourses, rest.getMenu().getCategories().get(1).getCourses().size());
        assertEquals(menuCatCourses + 1, //
                     rest.getMenu().getCategories().get(0).getCourses().size());

    }

    @Test
    public void addRestaurantAdminReqTest()
    {
        menuCats = 2;
        menuCatCourses = 2;

        // service can invoke a single fire!
        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();

        // create a restaurant for the rest of the test

        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);

        rest = saveRest(service, rest, true);

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getAdmins().add("test@example.com");

        rest = saveRest(service, rest, true);

        assertNotNull(rest.getAdmins());
        assertEquals(1, rest.getAdmins().size());
        assertEquals("test@example.com", rest.getAdmins().get(0));

        // Add a second admin
        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getAdmins().add("test2@example.com");

        rest = saveRest(service, rest, true);

        assertNotNull(rest.getAdmins());
        assertEquals(2, rest.getAdmins().size());
        assertEquals("test@example.com", rest.getAdmins().get(0));
        assertEquals("test2@example.com", rest.getAdmins().get(1));
    }

    @Test
    public void delRestAdminRequestTest()
    {
        menuCats = 2;
        menuCatCourses = 2;

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

        rest = saveRest(service, rest, true);
        // service is dead after afire

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getAdmins().remove("admin0@test.com");

        rest = saveRest(service, rest, true);

        assertEquals(1, rest.getAdmins().size());

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getAdmins().remove("admin1@test.com");

        rest = saveRest(service, rest, true);

        assertEquals(0, rest.getAdmins().size());
    }

    @Test
    public void addBranchAdminRequestTest()
    {
        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 1;
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
        rest = saveRest(service, rest, true);

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        RestaurantBranchProxy branch = rest.getBranches().get(0);

        branch.getAdmins().add("admin@test.com");
        rest = saveRest(service, rest, true);

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

        RestaurantAdminServiceRequest service = rf.getRestaurantAdminService();
        RestaurantProxy rest = createRest(service,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);

        rest = saveRest(service, rest, true);

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getServices().add(ServiceType.DELIVERY);
        rest.getServices().add(ServiceType.TAKE_AWAY);

        rest = saveRest(service, rest, true);

        assertNotNull(rest.getServices());
        assertEquals(2, rest.getServices().size());

        service = rf.getRestaurantAdminService();
        rest = service.edit(rest);
        rest.getServices().remove(ServiceType.DELIVERY);

        rest = saveRest(service, rest, true);

        assertNotNull(rest.getServices());
        assertEquals(1, rest.getServices().size());
    }

}
