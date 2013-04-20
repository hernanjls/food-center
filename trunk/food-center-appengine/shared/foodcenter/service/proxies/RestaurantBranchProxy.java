package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;

@ProxyForName(value = "foodcenter.server.db.modules.DbRestaurantBranch", locator = "foodcenter.server.db.DbObjectLocator")
public interface RestaurantBranchProxy extends GeoLocationProxy, EntityProxy
{
    public String getId();
    
    public RestaurantProxy getRestaurant();

    public void setRestaurant(RestaurantProxy restaurant);

    public List<UserProxy> getAdmins();

    public void setAdmins(List<UserProxy> admins);

    public List<UserProxy> getWaiters();

    public void setWaiters(List<UserProxy> waiters);

    public List<UserProxy> getChefs();

    public void setChefs(List<UserProxy> chefs);

    public List<TableProxy> getTables();

    public void setTables(List<TableProxy> tables);

    public List<CartProxy> getOrders();

    public void setOrders(List<CartProxy> orders);

    public MenuProxy getMenu();

    public void setMenu(MenuProxy menu);

    public List<ServiceType> getServices();

    public void setServices(List<ServiceType> services);

    public String getPhone();

    public void setPhone(String phone);
    
}
