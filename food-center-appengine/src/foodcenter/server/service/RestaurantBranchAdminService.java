package foodcenter.server.service;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbTable;

public class RestaurantBranchAdminService extends MenuAdminService
{
    public static void addBranchTable(DbRestaurantBranch branch, DbTable table)
    {
        PMF.makeTransactional();
        
        branch.getTables().add(table);
    }

    public static void removeBranchTable(DbRestaurantBranch branch, DbTable table)
    {
        PMF.makeTransactional();
        
        branch.getTables().remove(table);
    }


    public static DbRestaurantBranch saveRestaurantBranch(DbRestaurantBranch branch)
    {
        return DbHandler.save(branch);
    }


//    public static List<DbOrder> getOrders(DbRestaurantBranch branch, Date from, Date to)
//    {
//        DbUser user = PrivilegeManager.getCurrentUser();
//        UserPrivilege priv = PrivilegeManager.getPrivilege(user);
//        if (UserPrivilege.RestaurantAdmin != priv && UserPrivilege.Admin != priv
//            && UserPrivilege.RestaurantBranchAdmin != priv)
//        {
//            return null;
//        }
//
//        String query = "restBranchId == branchIdP && date >= fromP && date <= toP";
//        
//        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
//        params.add(new DeclaredParameter("branchIdP", branch.getId()));
//        params.add(new DeclaredParameter("fromP", from));
//        params.add(new DeclaredParameter("toP", to));
//        
//        ArrayList<SortOrder> sort = new ArrayList<SortOrder>();
//        sort.add(new SortOrder("date", SortOrderDirection.DESC));
//
//        return DbHandler.find(DbOrder.class, // class
//                              query, // base-query
//                              params,
//                              sort, // sort order
//                              Integer.MAX_VALUE); // no limits...
//
//    }

}
