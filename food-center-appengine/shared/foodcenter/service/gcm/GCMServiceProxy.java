package foodcenter.service.gcm;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName("foodcenter.service.gcm.GCMService")
public interface GCMServiceProxy extends RequestContext
{

	Request<Void> register(String email, String regId);

	Request<Void> unregister(String email, String regId);

}
