package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.PMF;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.security.UsersManager;

public class RestaurantAdminService extends RestaurantBranchAdminService
{           
    private final static Logger logger = LoggerFactory.getLogger(RestaurantBranchAdminService.class);
    
    public static void addRestaurantBranch(DbRestaurant rest, DbRestaurantBranch branch)
	{
        if (!rest.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        
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
        if (!rest.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        
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
        if (!rest.isEditable())
        {
            logger.warn(ServiceError.PREMISSION_DENIED + " " + UsersManager.getUser().getEmail());
            throw new ServiceError(ServiceError.PREMISSION_DENIED);
        }
        
		DbRestaurant res = DbHandler.save(rest);
		if (null == res)
        {
            logger.error(ServiceError.DATABASE_ISSUE + " save rest");
            throw new ServiceError(ServiceError.DATABASE_ISSUE);
        }
		return res;
	}
}
