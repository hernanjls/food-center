package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompany;
import foodcenter.server.db.modules.DbCompanyBranch;

public class CompanyAdminService extends CompanyBranchAdminService
{
    public static void addCompanyBranch(DbCompany comp, DbCompanyBranch branch)
    {
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
        DbCompany res = DbHandler.save(comp);

        return res;
    }
}
