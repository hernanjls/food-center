package foodcenter.service.requset;

import java.util.Date;
import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.TableProxy;

@ServiceName(value = "foodcenter.server.service.RestaurantBranchAdminService")
public interface RestaurantBranchAdminServiceRequest extends MenuAdminServiceRequest
{
	
    public Request<Void> removeBranchTable(RestaurantBranchProxy branch, TableProxy table);

	public Request<Void> addBranchTable(RestaurantBranchProxy branch, TableProxy table);
		
	public Request<RestaurantBranchProxy> saveRestaurantBranch(RestaurantBranchProxy branch);

	public Request<List<OrderProxy>> getOrders(String branchId, Date from, Date to);

}
