package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "foodcenter.server.service.AdminService")
public interface AdminServiceRequest extends RequestContext
{
    public Request<Boolean> deleteRestaurant(String id);
    
}
