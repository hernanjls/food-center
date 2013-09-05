package foodcenter.server.service;

import java.util.Date;
import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbCompanyBranch;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.server.db.security.UserPrivilege;

public class CompanyBranchAdminService
{
    public static DbCompanyBranch saveCompanyBranch(DbCompanyBranch branch)
    {
        // TODO save branch logic ? / remove this option ?
        return DbHandler.save(branch);
    }

    public static List<DbOrder> getOrders(DbCompanyBranch branch, Date from, Date to)
    {
        DbUser user = PrivilegeManager.getCurrentUser();
        UserPrivilege priv = PrivilegeManager.getPrivilege(user);
        if (UserPrivilege.CompanyAdmin != priv //
            && UserPrivilege.Admin != priv
            && UserPrivilege.CompanyBranchAdmin != priv)
        {
            return null;
        }

        String branchId = branch.getId();
        return DbHandler.find(DbOrder.class, // class
                              "compBranchId == branchIdP && date >= fromP && date <= toP", // base-query
                              "String branchIdP, Date fromP, Date toP", // declared parameters
                              new Object[] { branchId, from, to }, // values
                              Integer.MAX_VALUE); // no limits...

    }

}
