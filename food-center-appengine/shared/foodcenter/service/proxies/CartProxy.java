package foodcenter.service.proxies;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbCart", locator = "foodcenter.server.db.DbObjectLocator")
public interface CartProxy extends EntityProxy
{

}
