package foodcenter.client;

import java.util.List;

import foodcenter.service.proxies.RestaurantProxy;

public interface RestaurantListShowable
{
	
	public void addRestaurants(List<RestaurantProxy> restaurants);

}
