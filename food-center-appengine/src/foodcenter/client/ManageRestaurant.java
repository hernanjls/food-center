package foodcenter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.panels.RestaurantPanel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.UserCommonServiceProxy;
import foodcenter.service.proxies.RestaurantProxy;

public class ManageRestaurant implements EntryPoint
{
//    private static final String GWT_CONTINER = "gwtContainer";
    private UserCommonServiceProxy userCommonService = RequestUtils.getRequestFactory().getUserCommonService();

    /**************************************************************************
     * Data Objects
     **************************************************************************/
    // private RestaurantProxy rest = null;

    private Boolean isAdmin;

    /**************************************************************************
     * Panels
     **************************************************************************/

    @Override
    public void onModuleLoad()
    {
        isAdmin = true;
        RestaurantProxy rest = userCommonService.create(RestaurantProxy.class);
        PopupPanel popup = new PopupPanel(false);

        OnSaveRestaurant onSave = new OnSaveRestaurant(popup, rest);
        OnDiscardRestaurant onDiscard = new OnDiscardRestaurant(popup);
        popup.add(new RestaurantPanel(userCommonService, rest, isAdmin, onSave, onDiscard));
        popup.setPopupPosition(10, 80);
        popup.show();
    }

    private class OnSaveRestaurant implements Runnable
    {
        private final PopupPanel popup;
        private final RestaurantProxy rest;

        public OnSaveRestaurant(PopupPanel popup, RestaurantProxy rest)
        {
            this.popup = popup;
            this.rest = rest;
        }

        @Override
        public void run()
        {
            popup.hide();
            PopupPanel loading = new PopupPanel(false);
            loading.setWidget(new Label("Loading..."));
            loading.center();
            userCommonService.saveRestaurant(rest).fire(new AddRestaurantReciever(loading));

        }

    }

    class AddRestaurantReciever extends Receiver<Boolean>
    {
        private final PopupPanel loading;
        
        public AddRestaurantReciever(PopupPanel loading)
        {
            this.loading = loading;
        }
        
        @Override
        public void onSuccess(Boolean response)
        {
            loading.hide();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            loading.hide();
            Window.alert("exception: " + error.getMessage());
        }

    }

    private class OnDiscardRestaurant implements Runnable
    {
        private final PopupPanel popup;

        public OnDiscardRestaurant(PopupPanel popup)
        {
            this.popup = popup;
        }

        @Override
        public void run()
        {
            popup.hide();

        }

    }
}
