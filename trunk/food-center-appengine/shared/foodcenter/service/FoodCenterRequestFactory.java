package foodcenter.service;

import com.google.web.bindery.requestfactory.shared.RequestFactory;



public interface FoodCenterRequestFactory extends RequestFactory
{

    public UserCommonServiceProxy getUserCommonService();
	
	public MsgServiceProxy msgService();
	
	public GCMServiceProxy gcmService();
}
