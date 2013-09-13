package foodcenter.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
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

        String query = "compBranchId == branchIdP && date >= fromP && date <= toP";
        
        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("branchIdP", branch.getId()));
        params.add(new DeclaredParameter("fromP", from));
        params.add(new DeclaredParameter("toP", to));
        
        ArrayList<SortOrder> sort = new ArrayList<SortOrder>();
        sort.add(new SortOrder("date", SortOrderDirection.DESC));

        return DbHandler.find(DbOrder.class, // class
                              query, // base-query
                              params,
                              sort, // sort order
                              Integer.MAX_VALUE); // no limits...

    }

}
