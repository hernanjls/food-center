package foodcenter.service.request.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.request.AbstractRequestTest;
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
        
        setUpPMF();
        service.login("").fire(userResponse);
        tearDownPMF();
        
        assertNotNull(userResponse.response);
        assertEquals("", userResponse.response.getGcmKey());

        service = rf.getClientService();
        userResponse.response = null;
        
        setUpPMF();
        service.login("gcmkey1").fire(userResponse);
        tearDownPMF();
        
        assertNotNull(userResponse.response);
        assertEquals("gcmkey1", userResponse.response.getGcmKey());
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
        rest = saveRest(adminService, rest, true);

        adminService = rf.getRestaurantAdminService();
        RestaurantProxy rest2 = createRest(adminService,
                                           "rest2",
                                           menuCats,
                                           menuCatCourses,
                                           numBranches,
                                           numBranchMenuCats,
                                           numBranchMenuCatCourses);
        rest2 = saveRest(adminService, rest2, true);
        
        
        ClientServiceRequest clientService = rf.getClientService();
        MockTestResponse<List<RestaurantProxy>> getDefaultResponse = new MockTestResponse<List<RestaurantProxy>>();
        
        setUpPMF();
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
        comp = saveComp(adminService, comp, true);
        
        adminService = rf.getCompanyAdminService();
        CompanyProxy comp2 = createComp(adminService, "comp2", numBranches);
        comp2 = saveComp(adminService, comp2, true);

        
        ClientServiceRequest clientService = rf.getClientService();
        MockTestResponse<List<CompanyProxy>> getDefaultResponse = new MockTestResponse<List<CompanyProxy>>();
        
        setUpPMF();
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
