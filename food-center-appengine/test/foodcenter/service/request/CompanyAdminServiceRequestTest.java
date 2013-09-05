package foodcenter.service.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.request.mock.MockTestResponse;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.CompanyAdminServiceRequest;

public class CompanyAdminServiceRequestTest extends AbstractRequestTest
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
    public void saveNewCompanyServiceTest()
    {
        CompanyProxy response = null;

        numBranches = 2;

        CompanyAdminServiceRequest service = rf.getCompanyAdminService();

        CompanyProxy comp = createComp(service, "comp", numBranches);
        MockTestResponse<CompanyProxy> testResponse = new MockTestResponse<CompanyProxy>();
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(testResponse);
        response = testResponse.response;

        // Validate the branches values
        assertNotNull(response.getBranches());
        assertEquals(numBranches, response.getBranches().size());

        // Validate the branches inner values
        for (int i = 0; i < numBranches; ++i)
        {
            CompanyBranchProxy branch = response.getBranches().get(i);
            assertNotNull(branch);
        }

    }

    /**
     * this test will test and demonstrate the flow: <br>
     * Company admin adds a branch to already exist Company
     * 
     * @throws InterruptedException
     */
    @Test
    public void addCompanyBranchServiceTest()
    {
        numBranches = 1;

        CompanyAdminServiceRequest service = rf.getCompanyAdminService();

        CompanyProxy comp = createComp(service, "comp", numBranches);

        MockTestResponse<CompanyProxy> testResponse = new MockTestResponse<CompanyProxy>();
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(testResponse);
        // the above line is equals to
        // service.saveCompany(comp).with(CompanyProxy.COMP_WITH).to(testResponse);
        // service.fire();

        assertNotNull(testResponse.response);

        // tear down the pmf, because this is going to be a new RF call
        tearDownPMF();

        CompanyAdminServiceRequest adminService = rf.getCompanyAdminService();

        // make the Company editable
        CompanyProxy editable = adminService.edit(testResponse.response);

        // add course to the Company menu

        CompanyBranchProxy branch = createCompBranch(adminService);
        branch.setAddress("Dror");

        CompanyBranchProxy branch2 = createCompBranch(adminService);
        branch.setAddress("Dror2");

        testResponse.response = null;

        // editable.getBranches().add(branch);
        adminService.addCompanyBranch(editable, branch);
        ++numBranches;

        // editable.getBranches().add(branch2);
        adminService.addCompanyBranch(editable, branch2);
        ++numBranches;

        // next 2 lines are equal to
        // adminService.saveCompany(editable).with(CompanyProxy.COMP_WITH).fire(testResponse);
        adminService.saveCompany(editable).with(CompanyProxy.COMP_WITH).to(testResponse);

        // setup a new pmf for the new call
        setUpPMF();
        adminService.fire();

        assertNotNull(testResponse.response);
        assertEquals(numBranches, testResponse.response.getBranches().size());

    }

    @Test
    public void delcompMenuCategoryTest()
    {
        CompanyProxy response = null;

        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        MockTestResponse<CompanyProxy> compResponse = new MockTestResponse<CompanyProxy>();
        // service can invoke a single fire!

        /*
         * create a Company for the comp of the test
         */
        CompanyProxy comp = createComp(service, "comp", numBranches);
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(compResponse);
        // service is dead after a fire

        response = compResponse.response;
        compResponse.response = null;

        tearDownPMF();
        service = rf.getCompanyAdminService();

        comp = service.edit(response);

        setUpPMF();

        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(compResponse);
        // service is dead after a fire

        tearDownPMF();
        setUpPMF();

        response = compResponse.response;
        compResponse.response = null;
        ClientServiceRequest client = rf.getClientService();
        client.getCompanyById(response.getId()).fire(compResponse);
    }

    @Test
    public void addCompanyAdminRequestTest()
    {
        CompanyProxy response = null;

        // service can invoke a single fire!
        CompanyAdminServiceRequest service = rf.getCompanyAdminService();

        // create a Company for the comp of the test

        CompanyProxy comp = createComp(service, "comp", numBranches);
        MockTestResponse<CompanyProxy> testResponse = new MockTestResponse<CompanyProxy>();
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(testResponse);
        // service is dead after a fire

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        response = testResponse.response;
        testResponse.response = null;

        service = rf.getCompanyAdminService();
        response = service.edit(response);
        response.getAdmins().add("test@example.com");

        service.saveCompany(response).with(CompanyProxy.COMP_WITH).fire(testResponse);

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        response = testResponse.response;
        testResponse.response = null;

        assertNotNull(response.getAdmins());
        assertEquals(1, response.getAdmins().size());
        assertEquals("test@example.com", response.getAdmins().get(0));

        // Add a second admin
        service = rf.getCompanyAdminService();
        response = service.edit(response);
        response.getAdmins().add("test2@example.com");

        service.saveCompany(response).with(CompanyProxy.COMP_WITH).fire(testResponse);

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        response = testResponse.response;
        testResponse.response = null;

        assertNotNull(response.getAdmins());
        assertEquals(2, response.getAdmins().size());
        assertEquals("test@example.com", response.getAdmins().get(0));
        assertEquals("test2@example.com", response.getAdmins().get(1));
    }

    @Test
    public void delCompanyAdminRequestTest()
    {
        CompanyProxy response = null;
        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        // service can invoke a single

        /*
         * create a Company for the comp of the test
         */
        CompanyProxy comp = createComp(service, "comp", numBranches);

        comp.getAdmins().add("admin0@test.com");
        comp.getAdmins().add("admin1@test.com");

        MockTestResponse<CompanyProxy> testResponse = new MockTestResponse<CompanyProxy>();
        // service is dead after afire
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(testResponse);
        response = testResponse.response;
        testResponse.response = null;

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        service = rf.getCompanyAdminService();
        response = service.edit(response);
        response.getAdmins().remove("admin0@test.com");
        service.saveCompany(response).with(CompanyProxy.COMP_WITH).fire(testResponse);
        response = testResponse.response;
        testResponse.response = null;

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        assertEquals(1, response.getAdmins().size());

        service = rf.getCompanyAdminService();
        response = service.edit(response);
        response.getAdmins().remove("admin1@test.com");
        service.saveCompany(response).with(CompanyProxy.COMP_WITH).fire(testResponse);
        response = testResponse.response;
        testResponse.response = null;

        tearDownPMF(); // tear down the pmf, because this is going to be a new RF call
        setUpPMF();

        assertEquals(0, response.getAdmins().size());
    }

    @Test
    public void addBranchAdminRequestTest()
    {
        numBranches = 1;

        MockTestResponse<CompanyProxy> response = new MockTestResponse<CompanyProxy>();
        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        CompanyProxy comp = createComp(service, "comp", numBranches);

        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(response);
        comp = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        CompanyBranchProxy branch = comp.getBranches().get(0);

        branch.getAdmins().add("admin@test.com");
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(response);
        comp = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        branch = comp.getBranches().get(0);
        assertNotNull(branch.getAdmins());
        assertEquals(1, branch.getAdmins().size());
    }

    @Test
    public void changeServiceRequestTest()
    {
        numBranches = 1;

        MockTestResponse<CompanyProxy> response = new MockTestResponse<CompanyProxy>();
        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        CompanyProxy comp = createComp(service, "comp", numBranches);

        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(response);
        comp = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getServices().add(ServiceType.DELIVERY);
        comp.getServices().add(ServiceType.TAKE_AWAY);
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(response);
        comp = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        assertNotNull(comp.getServices());
        assertEquals(2, comp.getServices().size());

        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getServices().remove(ServiceType.DELIVERY);
        service.saveCompany(comp).with(CompanyProxy.COMP_WITH).fire(response);
        comp = response.response;
        response.response = null;

        tearDownPMF();
        setUpPMF();

        assertNotNull(comp.getServices());
        assertEquals(1, comp.getServices().size());
    }
}
