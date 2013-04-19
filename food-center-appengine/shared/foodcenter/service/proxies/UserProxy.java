package foodcenter.service.proxies;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbUser", locator = "foodcenter.server.db.DbObjectLocator")
public interface UserProxy extends EntityProxy
{
	public String getNickName();
	
	public String getEmail();
	
	public void setEmail(String email);
	
	public String getUserId();
	
	public Boolean isAdmin();
	
	public String getLogoutUrl();
	
	public String getGcmKey();
    
	public void setGcmKey(String gcmKey);
}