package foodcenter.service;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

import foodcenter.service.common.UserCommonServiceProxy;
import foodcenter.service.gcm.GCMServiceProxy;
import foodcenter.service.msg.MsgServiceProxy;


public interface FoodCenterRequestFactory extends RequestFactory
{

    public UserCommonServiceProxy getLoginService();
	
	public MsgServiceProxy msgService();
	
	public GCMServiceProxy gcmService();
}
