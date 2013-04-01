package foodcenter.service.proxies;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbRestaurantBranch", locator = "foodcenter.server.db.DbObjectLocator")
public interface RestaurantBranchProxy extends EntityProxy
{

}
