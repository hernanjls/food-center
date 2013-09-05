package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.request.mock.MockTestResponse;
import foodcenter.service.requset.CompanyAdminServiceRequest;
import foodcenter.service.requset.CompanyBranchAdminServiceRequest;

public class CompanyBranchAdminRequestTest extends AbstractRequestTest
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

        MockTestResponse<CompanyProxy> compResp = new MockTestResponse<CompanyProxy>();
        CompanyAdminServiceRequest adminService = rf.getCompanyAdminService();
        CompanyProxy comp = createComp(adminService,
                                          "comp",
                                          numBranches);
        
        adminService.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(compResp);
        comp = compResp.response;
        compResp.response = null;
        
        tearDownPMF();
        setUpPMF();
        
        CompanyBranchAdminServiceRequest service = rf.getCompanyBranchAdminService();
        CompanyBranchProxy branch = comp.getBranches().get(0);
        
        branch = service.edit(branch);
        branch.getAdmins().add("admin@test.com");
        MockTestResponse<CompanyBranchProxy> branchResp = new MockTestResponse<CompanyBranchProxy>();
        service.saveCompanyBranch(branch).fire(branchResp);
        branch = branchResp.response;
        branchResp.response = null;

        tearDownPMF();
        setUpPMF();

        assertNotNull(branch.getAdmins());
        assertEquals(1, branch.getAdmins().size());
    }

}
