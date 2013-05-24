package foodcenter.server.service;

import foodcenter.server.db.DbHandler;
import foodcenter.server.db.modules.DbRestaurantBranch;
import foodcenter.server.db.modules.DbTable;
import foodcenter.service.enums.ServiceType;

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
	
	public static void addRestaurantBranchServiceType(DbRestaurantBranch branch, ServiceType service)
	{
		branch.getServices().add(service);
	}
	
	public static void removeRestaurantBranchServiceType(DbRestaurantBranch branch, ServiceType service)
	{
		branch.getServices().remove(service);
	}
	
	
	public static DbRestaurantBranch saveRestaurantBranch(DbRestaurantBranch branch)
	{
		//TODO save logic
		return DbHandler.save(branch);
		
	}
	
}
