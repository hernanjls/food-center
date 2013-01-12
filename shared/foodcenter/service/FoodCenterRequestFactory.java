package foodcenter.service;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

import foodcenter.service.msg.MsgServiceRequest;

public interface FoodCenterRequestFactory extends RequestFactory
{

	MsgServiceRequest msgRequest();

}
