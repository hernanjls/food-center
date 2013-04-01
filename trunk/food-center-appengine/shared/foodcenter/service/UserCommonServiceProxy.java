package foodcenter.service;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.LoginInfoProxy;
import foodcenter.service.proxies.RestaurantProxy;


@ServiceName(value="foodcenter.server.service.common.UserCommonService")
public interface UserCommonServiceProxy extends RequestContext
{
	
	public Request<LoginInfoProxy> getLoginInfo();
	
	public Request<List<RestaurantProxy>> getDefaultRestaurants();
	
}
