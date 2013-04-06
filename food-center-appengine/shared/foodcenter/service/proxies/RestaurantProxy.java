package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;

@ProxyForName(value = "foodcenter.server.db.modules.DbRestaurant", locator = "foodcenter.server.db.DbObjectLocator")
public interface RestaurantProxy extends EntityProxy
{

	public String getId();
	public String getName();
	public MenuProxy getMenu();
	public void setMenu(MenuProxy menu);
	public List<Byte> getIconBytes();
	public String getPhone();
	public List<ServiceType> getServices();
	public List<RestaurantBranchProxy> getBranches();
	public List<UserProxy> getAdmins();
	
}
