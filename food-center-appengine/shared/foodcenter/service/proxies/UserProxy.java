package foodcenter.service.proxies;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbUser", locator = "foodcenter.server.db.DbObjectLocator")
public interface UserProxy extends EntityProxy
{ 
	public String getUsername();
	public void setUsername(String name);
}
