package foodcenter.service;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;


@ServiceName(value="foodcenter.server.service.UserCommonService")
public interface UserCommonServiceProxy extends RequestContext
{
	
    public Request<UserProxy> getDbUser(String email);
    
	public Request<UserProxy> login(String gcmKey);
	
	public Request<Void> logout();
	
	public Request<List<RestaurantProxy>> getDefaultRestaurants();
	
	public Request<RestaurantProxy> getRestaurant(String id);
	
	public Request<RestaurantProxy> saveRestaurant(RestaurantProxy rest);
	
	public Request<Boolean> deleteRestaurant(String id);
}
