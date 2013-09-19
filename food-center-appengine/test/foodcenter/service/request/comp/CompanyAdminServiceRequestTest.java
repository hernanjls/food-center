package foodcenter.service.request.comp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.request.AbstractRequestTest;
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

        numBranches = 2;

        CompanyAdminServiceRequest service = rf.getCompanyAdminService();

        CompanyProxy comp = createComp(service, "comp", numBranches);
        comp = saveComp(service, comp, true);

        // Validate the branches values
        assertNotNull(comp.getBranches());
        assertEquals(numBranches, comp.getBranches().size());

        // Validate the branches inner values
        for (int i = 0; i < numBranches; ++i)
        {
            CompanyBranchProxy branch = comp.getBranches().get(i);
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
        comp = saveComp(service, comp, true);

        assertNotNull(comp);

        CompanyAdminServiceRequest adminService = rf.getCompanyAdminService();

        // make the Company editable
        comp = adminService.edit(comp);

        // add branch to the Company menu
        for (int i = 0; i < 2; ++i)
        {
            CompanyBranchProxy branch = createCompBranch(adminService);
            branch.setAddress("Dror" + i);
            adminService.addCompanyBranch(comp, branch);
            ++numBranches;
        }

        comp = saveComp(adminService, comp, true);

        assertNotNull(comp);
        assertEquals(numBranches, comp.getBranches().size());

    }

//    @Test
//    public void delcompMenuCategoryTest()
//    {
//        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
//
//        
//         // create a Company for the comp of the test
//        CompanyProxy comp = createComp(service, "comp", numBranches);
//        comp = saveComp(service, comp, true);
//
//        service = rf.getCompanyAdminService();
//        comp = service.edit(comp);
//        comp = saveComp(service, comp, true);
//        
//        comp = getCompById(comp.getId(), true);
//    }

    @Test
    public void addCompanyAdminRequestTest()
    {
        // service can invoke a single fire!
        CompanyAdminServiceRequest service = rf.getCompanyAdminService();

        // create a Company for the comp of the test

        CompanyProxy comp = createComp(service, "comp", numBranches);
        saveComp(service, comp, true);
        
     
        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getAdmins().add("test@example.com");
        comp = saveComp(service, comp, true);
        
        assertNotNull(comp.getAdmins());
        assertEquals(1, comp.getAdmins().size());
        assertEquals("test@example.com", comp.getAdmins().get(0));

        // Add a second admin
        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getAdmins().add("test2@example.com");

        comp = saveComp(service, comp, true);
        
        assertNotNull(comp.getAdmins());
        assertEquals(2, comp.getAdmins().size());
        assertEquals("test@example.com", comp.getAdmins().get(0));
        assertEquals("test2@example.com", comp.getAdmins().get(1));
    }

    @Test
    public void delCompanyAdminRequestTest()
    {
        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        // service can invoke a single

        /*
         * create a Company for the comp of the test
         */
        CompanyProxy comp = createComp(service, "comp", numBranches);

        comp.getAdmins().add("admin0@test.com");
        comp.getAdmins().add("admin1@test.com");
        comp = saveComp(service, comp, true);
        
        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getAdmins().remove("admin0@test.com");
        comp = saveComp(service, comp, true);
        
        assertEquals(1, comp.getAdmins().size());

        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getAdmins().remove("admin1@test.com");
        comp = saveComp(service, comp, true);
        
        assertEquals(0, comp.getAdmins().size());
    }

    @Test
    public void addBranchAdminRequestTest()
    {
        numBranches = 1;

        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        CompanyProxy comp = createComp(service, "comp", numBranches);
        comp = saveComp(service, comp, true);
        
        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        CompanyBranchProxy branch = comp.getBranches().get(0);

        branch.getAdmins().add("admin@test.com");
        comp = saveComp(service, comp, true);
        
        branch = comp.getBranches().get(0);
        assertNotNull(branch.getAdmins());
        assertEquals(1, branch.getAdmins().size());
    }

    @Test
    public void changeServiceRequestTest()
    {
        numBranches = 1;

        CompanyAdminServiceRequest service = rf.getCompanyAdminService();
        CompanyProxy comp = createComp(service, "comp", numBranches);
        comp = saveComp(service, comp, true);
        
        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getServices().add(ServiceType.DELIVERY);
        comp.getServices().add(ServiceType.TAKE_AWAY);
        comp = saveComp(service, comp, true);
        
        assertNotNull(comp.getServices());
        assertEquals(2, comp.getServices().size());

        service = rf.getCompanyAdminService();
        comp = service.edit(comp);
        comp.getServices().remove(ServiceType.DELIVERY);
        comp = saveComp(service, comp, true);
        
        assertNotNull(comp.getServices());
        assertEquals(1, comp.getServices().size());
    }
}
