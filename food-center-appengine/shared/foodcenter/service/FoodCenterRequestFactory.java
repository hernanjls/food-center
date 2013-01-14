package foodcenter.service;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

import foodcenter.service.gcm.GCMServiceProxy;
import foodcenter.service.msg.MsgServiceProxy;

public interface FoodCenterRequestFactory extends RequestFactory
{

	MsgServiceProxy msgService();
	
	GCMServiceProxy gcmService();
}
