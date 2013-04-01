package foodcenter.service.common;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;


@ServiceName(value="foodcenter.server.service.common.UserCommonService")
public interface UserCommonServiceProxy extends RequestContext
{
	
	public Request<LoginInfoProxy> getLoginInfo();
}
