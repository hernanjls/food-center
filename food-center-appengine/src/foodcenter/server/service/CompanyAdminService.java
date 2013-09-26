package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.security.UsersManager;

public class CompanyAdminService extends CompanyBranchAdminService
{    
    private static final Logger logger = LoggerFactory.getLogger(CompanyAdminService.class);
    
    public static void addCompanyBranch(DbCompany comp, DbCompanyBranch branch)
    {
        if (!comp.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        
        PMF.makeTransactional();

        List<DbCompanyBranch> branches = comp.getBranches();
        if (null == branches)
        {
            branches = new ArrayList<DbCompanyBranch>();
            comp.setBranches(branches);
        }
        branches.add(branch);
    }

    public static void removeCompanyBranch(DbCompany comp, DbCompanyBranch branch)
    {
        if (!comp.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        
        PMF.makeTransactional();
        
        List<DbCompanyBranch> branches = comp.getBranches();
        if (null == branches)
        {
            return;
        }
        if (branches.contains(branch))
        {
            branches.remove(branch);
        }
    }

    public static DbCompany saveCompany(DbCompany comp)
    {   
        if (!comp.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }

        DbCompany res = DbHandler.save(comp);
        if (null == res)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " save company");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }
        return res;
    }
}
