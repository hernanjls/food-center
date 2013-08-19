package foodcenter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.panels.RestaurantsListPanel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.requset.ClientServiceRequest;

public class FoodCenterImp extends Receiver<UserProxy> implements EntryPoint, Runnable
{

    // private static final String GWT_MENU_CONTAINER = "gwtMenuContainer";
    public static final String GWT_CONTINER = "gwtContainer";

    private boolean isAdmin;

    @Override
    public void onModuleLoad()
    {
        ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
        service.login(null).fire(this);

    }

    @Override
    public void onSuccess(UserProxy response)
    {
        isAdmin = response.isAdmin();
        // load the maps api!
        Maps.loadMapsApi(ClientUtils.GOOGLE_API_MAPS_KEY,
                         ClientUtils.GOOGLE_API_MAPS_VER,
                         false,
                         this);

    }
    
    @Override
    public void onFailure(ServerFailure error)
    {
        Window.alert("Can't get user..." + error.getMessage());
    }

    /**
     * Will be called after the maps api is loaded
     * 
     */
    @Override
    public void run()
    {
        Panel restaurants = new RestaurantsListPanel(isAdmin);
        RootPanel.get(FoodCenterImp.GWT_CONTINER).add(restaurants);
    }
}
