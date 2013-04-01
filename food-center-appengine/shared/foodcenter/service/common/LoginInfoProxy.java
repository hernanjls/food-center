package foodcenter.service.common;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.service.common.LoginInfo", locator = "foodcenter.server.db.DbObjectLocator")
public interface LoginInfoProxy extends EntityProxy
{
	public String getNickName();
	public String getEmail();
	public String getUserId();
	public Boolean isAdmin();
	public String getLogoutUrl();
}