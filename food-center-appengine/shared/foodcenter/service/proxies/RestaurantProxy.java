package foodcenter.service.proxies;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.interfaces.AbstractEntityInterface;

@ProxyForName(value = "foodcenter.server.db.modules.DbRestaurant", locator = "foodcenter.server.db.DbObjectLocator")
public interface RestaurantProxy extends EntityProxy, AbstractEntityInterface
{
    public final static String[] REST_WITH = { "menu",
                                              "menu.categories",
                                              "menu.categories.courses",
                                              "iconBytes",
                                              "branches",
                                              "branches.restaurant",
                                              "branches.menu",
                                              "branches.menu.categories",
                                              "branches.menu.categories.courses",
                                              "admins" };

    public String getId();

    public String getName();

    public void setName(String name);

    public MenuProxy getMenu();

    public void setMenu(MenuProxy menu);

    public String getPhone();

    public void setPhone(String phone);

    public List<ServiceType> getServices();

    public void setServices(List<ServiceType> services);

    public List<RestaurantBranchProxy> getBranches();

    public void setBranches(List<RestaurantBranchProxy> branches);

    public List<String> getAdmins();

    public void setAdmins(List<String> admins);

}
