package foodcenter.service.requset;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("foodcenter.server.service.GCMService")
public interface GCMServiceRequest extends RequestContext
{

	Request<Void> register(String regId);

	Request<Void> unregister(String regId);

}
