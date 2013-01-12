package foodcenter.service.msg;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "foodcenter.server.db.modules.DbMsg", locator = "foodcenter.service.MsgLocator")
public interface MsgProxy extends ValueProxy
{

	public String getMsg();

	public Long getId();

}
