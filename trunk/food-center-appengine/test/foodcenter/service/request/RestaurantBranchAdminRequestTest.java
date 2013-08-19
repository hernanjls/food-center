package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.request.mock.MockTestResponse;
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

        MockTestResponse<RestaurantProxy> restResp = new MockTestResponse<RestaurantProxy>();
        RestaurantAdminServiceRequest adminService = rf.getRestaurantAdminService();
        RestaurantProxy rest = createRest(adminService,
                                          "rest",
                                          menuCats,
                                          menuCatCourses,
                                          numBranches,
                                          numBranchMenuCats,
                                          numBranchMenuCatCourses);
        
        adminService.saveRestaurant(rest).with(RestaurantProxy.REST_WITH).fire(restResp);
        rest = restResp.response;
        restResp.response = null;
        
        tearDownPMF();
        setUpPMF();
        
        RestaurantBranchAdminServiceRequest service = rf.getRestaurantBranchAdminService();
        RestaurantBranchProxy branch = rest.getBranches().get(0);
        
        branch = service.edit(branch);
        branch.getAdmins().add("admin@test.com");
        MockTestResponse<RestaurantBranchProxy> branchResp = new MockTestResponse<RestaurantBranchProxy>();
        service.saveRestaurantBranch(branch).fire(branchResp);
        branch = branchResp.response;
        branchResp.response = null;

        tearDownPMF();
        setUpPMF();

        assertNotNull(branch.getAdmins());
        assertEquals(1, branch.getAdmins().size());
    }

}
