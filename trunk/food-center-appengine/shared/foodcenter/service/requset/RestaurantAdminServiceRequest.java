package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

@ServiceName(value = "foodcenter.server.service.RestaurantAdminService")
public interface RestaurantAdminServiceRequest extends RestaurantBranchAdminServiceRequest
{
    
    public Request<Void> addRestaurantBranch(RestaurantProxy rest, RestaurantBranchProxy branch);
	
    public Request<Void> removeRestaurantBranch(RestaurantProxy rest, RestaurantBranchProxy branch);
    	
    public Request<RestaurantProxy> saveRestaurant(RestaurantProxy rest);
}
