package foodcenter.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

import foodcenter.service.FoodCenterRequestFactory;

public class RequestUtils
{
    private FoodCenterRequestFactory requestFactory = null;
    
    public FoodCenterRequestFactory getRequestFactory()
    {
        if (null == requestFactory)
        {
            final EventBus eventBus = new SimpleEventBus();
            requestFactory = GWT.create(FoodCenterRequestFactory.class);
            requestFactory.initialize(eventBus);
        }
        return requestFactory;
    }
}
