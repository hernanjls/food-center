package foodcenter.client.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.server.db.modules.AbstractDbGeoObject;
import foodcenter.server.db.modules.DbRestaurant;
import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.OrderProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

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
	    String base64 = "data:image/png;base64," + imageByteArray.toString();
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
    
    public static MenuProxy createMenuProxy(RequestContext rContext)
    {
    	MenuProxy res = rContext.create(MenuProxy.class);
    	res.setCategories(new ArrayList<MenuCategoryProxy>());
    	return res;
    }
    
    public static MenuCategoryProxy createMenuCategoryProxy(RequestContext rContext)
    {
    	MenuCategoryProxy res = rContext.create(MenuCategoryProxy.class);
    	res.setCourses(new ArrayList<CourseProxy>());
    	return res;
    }
    
    public static RestaurantBranchProxy createRestaurantBranchProxy(RequestContext rContext)
    {
    	RestaurantBranchProxy res = rContext.create(RestaurantBranchProxy.class);
    	res.setMenu(createMenuProxy(rContext));

        res.setAdmins(new ArrayList<String>());
        res.setWaiters(new ArrayList<String>());
        res.setChefs(new ArrayList<String>());
        
    	res.setLat(AbstractDbGeoObject.GOOGLE_API_DEFAULT_LAT);
    	res.setLng(AbstractDbGeoObject.GOOGLE_API_DEFAULT_LNG);
    	res.setAddress(AbstractDbGeoObject.GOOGLE_API_DEFAULT_ADDR);
    	
    	return res;
    }
    
    public static RestaurantProxy createRestaurantProxy(RequestContext rContext)
    {
    	RestaurantProxy res = rContext.create(RestaurantProxy.class);
    	res.setAdmins(new ArrayList<String>());
    	res.setBranches(new ArrayList<RestaurantBranchProxy>());
//    	TODO res.setIconBytes()
    	res.setMenu(createMenuProxy(rContext));
    	res.setServices(new ArrayList<ServiceType>());
    	res.setImageUrl(DbRestaurant.DEFAULT_ICON_PATH);
    	
    	return res;
    }
    
    public static OrderProxy createOrder(RequestContext rContext)
    {
    	OrderProxy res = rContext.create(OrderProxy.class);
    	res.setCourses(new ArrayList<String>());
    	return res;
    }
}
