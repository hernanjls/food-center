package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;

public class RestaurantAdminService extends RestaurantBranchAdminService
{           
    public static void addRestaurantBranch(DbRestaurant rest, DbRestaurantBranch branch)
	{
        PMF.makeTransactional();

		List<DbRestaurantBranch> branches = rest.getBranches();
		if (null == branches)
		{
			branches = new ArrayList<DbRestaurantBranch>();
			rest.setBranches(branches);
		}
		branches.add(branch);
	}
    
    public static void removeRestaurantBranch(DbRestaurant rest, DbRestaurantBranch branch)
	{
        PMF.makeTransactional();
        
        List<DbRestaurantBranch> branches = rest.getBranches();
		if (null == branches)
		{
			return;
		}
		if (branches.contains(branch))
		{
		    branches.remove(branch);
		}
	}
        	
    public static DbRestaurant saveRestaurant(DbRestaurant rest)
	{        
        
		DbRestaurant res = DbHandler.save(rest);	
		return res;
	}
}
