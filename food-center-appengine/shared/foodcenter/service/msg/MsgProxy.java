package foodcenter.service.msg;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "foodcenter.server.db.modules.DbMsg", locator = "foodcenter.server.service.DbObjectLocator")
public interface MsgProxy extends EntityProxy
{

	public String getMsg();
	public String getEmail();
	public Long getId();

}
