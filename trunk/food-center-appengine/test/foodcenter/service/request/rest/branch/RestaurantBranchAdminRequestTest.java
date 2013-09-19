package foodcenter.service.request.rest.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.request.AbstractRequestTest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestaurantBranchAdminRequestTest extends AbstractRequestTest
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
    public void addBranchAdminRequestTest()
    {
        menuCats = 3;
        menuCatCourses = 4;
        numBranches = 1;
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
        
        rest = saveRest(adminService, rest, true);
        
        RestaurantBranchProxy branch = rest.getBranches().get(0);

        RestaurantBranchAdminServiceRequest service = rf.getRestaurantBranchAdminService();
        branch = service.edit(branch);
        branch.getAdmins().add("admin@test.com");
        
        branch = saveRestBranch(service, branch, false);
        assertNotNull(branch.getAdmins());
        assertEquals(1, branch.getAdmins().size());
    }

}
