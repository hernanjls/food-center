package foodcenter.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.DbHandler.DeclaredParameter;
import foodcenter.server.db.DbHandler.SortOrder;
import foodcenter.server.db.DbHandler.SortOrderDirection;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbOrder;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbTable;

public class RestaurantBranchAdminService extends MenuAdminService
{
    private static final Logger logger = LoggerFactory.getLogger(RestaurantBranchAdminService.class);

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

    public static List<DbOrder> getOrders(String branchId, Date from, Date to)
    {
        logger.info("get orders, branchId=" + branchId
                    + ", from: "
                    + from.toString()
                    + ", to: "
                    + to.toString());
        if (!RestaurantWorkerService.isBranchOrdersPrivilage(null, branchId))
        {
            return null;
        }

        String query = "restBranchId == branchIdP && date >= fromP && date <= toP";

        ArrayList<DeclaredParameter> params = new ArrayList<DeclaredParameter>();
        params.add(new DeclaredParameter("branchIdP", branchId));
        params.add(new DeclaredParameter("fromP", from));
        params.add(new DeclaredParameter("toP", to));

        ArrayList<SortOrder> sort = new ArrayList<SortOrder>();
        sort.add(new SortOrder("date", SortOrderDirection.DESC));

        return DbHandler.find(DbOrder.class, query, params, sort, Integer.MAX_VALUE);
    }

}
