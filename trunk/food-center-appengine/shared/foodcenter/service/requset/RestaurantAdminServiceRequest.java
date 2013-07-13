package foodcenter.service.requset;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

@ServiceName(value = "foodcenter.server.service.RestaurantAdminService")
public interface RestaurantAdminServiceRequest extends RestaurantBranchAdminServiceRequest
{

    public Request<Void> setIconBytes(RestaurantProxy rest, List<Byte> iconBytes);
    
    public Request<Void> addRestaurantBranch(RestaurantProxy rest, RestaurantBranchProxy branch);
	
    public Request<Void> removeRestaurantBranch(RestaurantProxy rest, RestaurantBranchProxy branch);
    
    public Request<Void> addRestaurantAdmin(RestaurantProxy rest, String admin);
	
	public Request<Void> removeRestaurantAdmin(RestaurantProxy rest, String admin);
    
	public Request<Void> addRestaurantServiceType(RestaurantProxy rest, ServiceType service);
	
	public Request<Void> removeRestaurantServiceType(RestaurantProxy rest, ServiceType service);
	
    public Request<RestaurantProxy> saveRestaurant(RestaurantProxy rest);
    

}
