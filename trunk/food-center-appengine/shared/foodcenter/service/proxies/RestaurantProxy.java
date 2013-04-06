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
	public void setName(String name);
	public MenuProxy getMenu();
	public void setMenu(MenuProxy menu);
	public List<Byte> getIconBytes();
	public String getPhone();
    public void setPhone(String phone);
	public List<ServiceType> getServices();
	public void setServices(List<ServiceType> services);
	public List<RestaurantBranchProxy> getBranches();
	public List<UserProxy> getAdmins();
	
}
