package foodcenter.server.service;

import java.util.ArrayList;
import java.util.List;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.service.enums.ServiceType;

public class RestaurantAdminService extends RestaurantBranchAdminService
{

//    private static UserService userService = UserServiceFactory.getUserService();
//    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    
    
    public static void setIconBytes(DbRestaurant rest, List<Byte> iconBytes)
    {
    	rest.setIconBytes(iconBytes);
    }
    
    public static void addRestaurantBranch(DbRestaurant rest, DbRestaurantBranch branch)
	{
		List<DbRestaurantBranch> branches= rest.getBranches();
		if (null == branches)
		{
			branches = new ArrayList<DbRestaurantBranch>();
			rest.setBranches(branches);
		}
		branches.add(branch);
	}
    
    public static void removeRestaurantBranch(DbRestaurant rest, DbRestaurantBranch branch)
	{
        return;
//		List<DbRestaurantBranch> branches= rest.getBranches();
//		if (null == branches)
//		{
//			return;
//		}
//		if (branches.contains(branch))
//		{
//		    branches.remove(branch);
//		}
	}
    
    public static void addRestaurantAdmin(DbRestaurant rest, String admin)
	{
		rest.getAdmins().add(admin);
	}
	
	public static void removeRestaurantAdmin(DbRestaurant rest, String admin)
	{
		rest.getAdmins().remove(admin);
	}
    
	public static void addRestaurantServiceType(DbRestaurant rest, ServiceType service)
	{
		rest.getServices().add(service);
	}
	
	public static void removeRestaurantServiceType(DbRestaurant rest, ServiceType service)
	{
		rest.getServices().remove(service);
	}
	
    public static DbRestaurant saveRestaurant(DbRestaurant rest)
	{
		DbRestaurant res = DbHandler.save(rest);
		
		return res;
	}

    
}
