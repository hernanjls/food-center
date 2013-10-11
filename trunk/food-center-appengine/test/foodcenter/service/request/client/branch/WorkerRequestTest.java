package foodcenter.service.request.client.branch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.request.AbstractRequestTest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class WorkerRequestTest extends AbstractRequestTest
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
    }

    @Override
    @After
    public void tearDown()
    {
        super.tearDown();
    }

    @Test
    public void checkEnvNonAdmin()
    {
        // make sure rest is editable on start
        rest = getRestById(rest.getId(), true);
        assertTrue(rest.isEditable());

        // change env to be non admin user
        setEnvIsAdmin(false);

        // make sure rest doesn't have user as admin
        rest = getRestById(rest.getId(), true);
        assertFalse(rest.isEditable());
    }

    @Test
    public void checkRestAdmin()
    {
        // make sure rest is editable on start
        rest = getRestById(rest.getId(), true);
        assertTrue(rest.isEditable());

        // add the admin and make sure rest contains it
        rest = addAdmin(rest, email);
        assertTrue(rest.getAdmins().contains(email));

        // change env to be non admin user
        setEnvIsAdmin(false);

        // make sure rest has user as admin
        rest = getRestById(rest.getId(), true);
        assertTrue(rest.getAdmins().contains(email));
        assertTrue(rest.isEditable());
    }

    @Test
    public void checkRestBranchAdmin()
    {
        // make sure rest is editable on start
        rest = getRestById(rest.getId(), true);
        assertTrue(rest.isEditable());

        // add the branch admin and make sure restbranch contains it
        rest = addBranchAdmin(rest, 0, email);
        assertTrue(rest.getBranches().get(0).getAdmins().contains(email));

        // change env to be non admin user
        setEnvIsAdmin(false);

        // make sure rest doesn't hold the user as admin
        rest = getRestById(rest.getId(), true);
        assertFalse(rest.getAdmins().contains(email));
        assertFalse(rest.isEditable());

        // make sure branch has user as admin
        assertTrue(rest.getBranches().get(0).getAdmins().contains(email));
        assertTrue(rest.getBranches().get(0).isEditable());
    }

    @Test
    public void checkRestBranchChef()
    {
        // make sure rest is editable on start
        rest = getRestById(rest.getId(), true);
        assertTrue(rest.isEditable());

        // add the branch chef and make sure restbranch contains it
        rest = addBranchChef(rest, 0, email);
        assertTrue(rest.getBranches().get(0).getChefs().contains(email));

        // change env to be non admin user
        setEnvIsAdmin(false);

        // make sure rest isn't editable
        rest = getRestById(rest.getId(), true);
        assertFalse(rest.isEditable());

        // make sure branch isn't editable
        assertFalse(rest.getBranches().get(0).isEditable());

        // check chefs contains new added email
        assertTrue(rest.getBranches().get(0).getChefs().contains(email));
        assertTrue(rest.getBranches().get(0).isChef());
    }

    /** adds a chef to the branchnum of restaurant */
    private RestaurantProxy addBranchChef(RestaurantProxy r, int branchNum, String email)
    {
        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();
        RestaurantProxy editable = adminService.edit(r);
        editable.getBranches().get(branchNum).getChefs().add(email);
        return saveRest(adminService, editable, true);
    }

    /** adds an admin to the branchnum of restaurant */
    private RestaurantProxy addBranchAdmin(RestaurantProxy r, int branchNum, String restEmail)
    {
        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();
        RestaurantProxy editable = adminService.edit(r);
        editable.getBranches().get(branchNum).getAdmins().add(restEmail);
        return saveRest(adminService, editable, true);
    }

    /** adds an admin to the restaurant */
    private RestaurantProxy addAdmin(RestaurantProxy r, String restEmail)
    {
        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();
        RestaurantProxy editable = adminService.edit(r);
        editable.getAdmins().add(restEmail);
        return saveRest(adminService, editable, true);
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
}
