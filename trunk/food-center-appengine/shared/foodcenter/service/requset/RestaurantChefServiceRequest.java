package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "foodcenter.server.service.RestaurantChefService")
public interface RestaurantChefServiceRequest extends RequestContext
{
	public Request<Void> foo();

}
