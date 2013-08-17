package foodcenter.service.requset;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;

@ServiceName(value = "foodcenter.server.service.ClientService")
public interface ClientServiceRequest extends RequestContext
{
	public Request<Void> logout();

    public Request<UserProxy> login(String gcmKey);


    //services the user need for restaurant
    
    public Request<List<RestaurantProxy>> getDefaultRestaurants();
    
    public Request<List<RestaurantProxy>> findRestaurant(String pattern);
    
    public Request<RestaurantProxy> getRestaurantById(String id);
    
    public Request<OrderProxy> makeOrder(OrderProxy order);
    
    // services the user need for companies
    
    public Request<List<CompanyProxy>> getDefaultCompanies();
    
    public Request<List<CompanyProxy>> findCompany(String pattern);
}
