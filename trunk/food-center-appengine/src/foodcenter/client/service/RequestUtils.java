package foodcenter.client.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Image;

import foodcenter.service.FoodCenterRequestFactory;

public class RequestUtils
{
    private static FoodCenterRequestFactory requestFactory = null;
    
    public static FoodCenterRequestFactory getRequestFactory()
    {
        if (null == requestFactory)
        {
            final EventBus eventBus = new SimpleEventBus();
            requestFactory = GWT.create(FoodCenterRequestFactory.class);
            requestFactory.initialize(eventBus);
        }
        return requestFactory;
    }
    
    
    public static Image getImage(byte[] imageByteArray)
	{
//	    String base64 = Base64Utils.toBase64(imageByteArray); 
	    String base64 = "data:image/png;base64,"+imageByteArray.toString();
	    return new Image(base64);
	}
    
    public static Image getImage(List<Byte> imageByteArray)
	{
        if (null == imageByteArray)
        {
            return null;
        }
    	byte[] b = imageByteArray.toArray(new Byte[0]).toString().getBytes();
    	return RequestUtils.getImage(b);
	}
    
}
