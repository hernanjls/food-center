package foodcenter.server.service;

import java.util.Date;
import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbTable;
import foodcenter.server.db.modules.DbUser;
import foodcenter.server.db.security.PrivilegeManager;
import foodcenter.server.db.security.UserPrivilege;

public class RestaurantBranchAdminService extends MenuAdminService
{

    public static void addBranchAdmin(DbRestaurantBranch branch, String admin)
    {
        branch.getAdmins().add(admin);
    }

    public static void removeBranchAdmin(DbRestaurantBranch branch, String admin)
    {
        branch.getAdmins().remove(admin);
    }

    public static void addBranchWaiter(DbRestaurantBranch branch, String waiter)
    {
        branch.getWaiters().add(waiter);
    }

    public static void removeBranchWaiter(DbRestaurantBranch branch, String waiter)
    {
        branch.getWaiters().remove(waiter);
    }

    public static void addBranchChef(DbRestaurantBranch branch, String chef)
    {
        branch.getChefs().add(chef);
    }

    public static void removeBranchChef(DbRestaurantBranch branch, String chef)
    {
        branch.getChefs().remove(chef);
    }

    public static void addBranchTable(DbRestaurantBranch branch, DbTable table)
    {
        branch.getTables().add(table);
    }

    public static void removeBranchTable(DbRestaurantBranch branch, DbTable table)
    {
        branch.getTables().remove(table);
    }


    public static DbRestaurantBranch saveRestaurantBranch(DbRestaurantBranch branch)
    {
        // TODO save branch logic ? / remove this option ?
        return DbHandler.save(branch);
    }

    public static List<DbOrder> getOrders(DbRestaurantBranch branch, Date from, Date to)
    {
        DbUser user = PrivilegeManager.getCurrentUser();
        UserPrivilege priv = PrivilegeManager.getPrivilege(user);
        if (UserPrivilege.RestaurantAdmin != priv && UserPrivilege.Admin != priv
            && UserPrivilege.RestaurantBranchAdmin != priv)
        {
            return null;
        }

        String branchId = branch.getId();
        return DbHandler.find(DbOrder.class, // class
                              "restBranchId == branchIdP && date >= fromP && date <= toP", // base-query
                              "String branchIdP, Date fromP, Date toP", // declared parameters
                              new Object[] { branchId, from, to }, // values
                              Integer.MAX_VALUE); // no limits...

    }

}
