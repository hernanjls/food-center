package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.TableProxy;

@ServiceName(value = "foodcenter.server.service.RestaurantBranchAdminService")
public interface RestaurantBranchAdminServiceRequest extends MenuAdminServiceRequest, RequestContext
{

	public Request<Void> addBranchAdmin(RestaurantBranchProxy branch, String admin);
	
	public Request<Void> removeBranchAdmin(RestaurantBranchProxy branch, String admin);
	
	public Request<Void> addBranchWaiter(RestaurantBranchProxy branch, String waiter);
	
	public Request<Void> removeBranchWaiter(RestaurantBranchProxy branch, String waiter);
	
	public Request<Void> addBranchChef(RestaurantBranchProxy branch, String chef);
	
	public Request<Void> removeBranchChef(RestaurantBranchProxy branch, String chef);
	
	public Request<Void> addBranchTable(RestaurantBranchProxy branch, TableProxy table);
	
	public Request<Void> removeBranchTable(RestaurantBranchProxy branch, TableProxy table);
	
	public Request<RestaurantBranchProxy> saveRestaurantBranch(RestaurantBranchProxy branch);
}
