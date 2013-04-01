package foodcenter.service.common;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.service.common.RestaurantBaseInfo", locator = "foodcenter.server.db.DbObjectLocator")
public interface RestaurantBaseInfoProxy extends EntityProxy
{

	public Long getId();
	public String getName();
	public String getImagePath();
	public List<String> getServices();
	
}
