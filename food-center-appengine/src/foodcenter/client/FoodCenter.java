package foodcenter.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.callbacks.SearchPanelCallback;
import foodcenter.client.callbacks.search.RestaurantSearchOptions;
import foodcenter.client.panels.common.BlockingPopupPanel;
import foodcenter.client.panels.common.HeaderProfilePanel;
import foodcenter.client.panels.main.RestaurantsListPanel;
import foodcenter.client.panels.restaurant.RestaurantPanel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.requset.AdminServiceRequest;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class FoodCenter extends Receiver<UserProxy> implements EntryPoint, Runnable
{

    // private static final String GWT_MENU_CONTAINER = "gwtMenuContainer";
    public static final String GWT_CONTINER = "gwtContainer";

    // Data structures!!!
    private boolean isAdmin;
    private UserProxy user;
    private final List<RestaurantProxy> rests;

    // Callbacks
    private final PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> restCallback;
    private final SearchPanelCallback<RestaurantSearchOptions> restSearchCallback;

    // Panels
    private RestaurantsListPanel restsPanel;
    private HeaderProfilePanel headerProfilePanel;

    private final Label infoPopupText; // for setting the popup text
    private final PopupPanel infoPopup; // info popup which can be shown whenever needed

    public FoodCenter()
    {
        // Initialize Restaurant List needed variables
        rests = new LinkedList<RestaurantProxy>();
        restCallback = new RestListCallbackImp();
        restSearchCallback = new RestSearchCallback();
        infoPopup = new PopupPanel(false);
        infoPopupText = new Label();

    }

    @Override
    public void onModuleLoad()
    {

        // load the maps api!
        Maps.loadMapsApi(ClientUtils.GOOGLE_API_MAPS_KEY,
                         ClientUtils.GOOGLE_API_MAPS_VER,
                         false,
                         this);

        showPopup("Loading user data...");

        // Load the user's data
        ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
        service.login(null).fire(this);
    }

    /**
     * Will be called after the maps api is loaded
     * 
     */
    @Override
    public void run()
    {
        // do nothing!
    }

    @Override
    public void onSuccess(UserProxy response)
    {
        showPopup("Loading Data ...");
        user = response;

        if (user == null)
        {
            Window.alert("Can't get user's info...");
            isAdmin = false;
        }
        else
        {
            isAdmin = user.isAdmin();
        }

        createHeaderProfilePanel();
        createRestsListPanel();

        // Load the restaurants when map loading is done!
        RequestUtils.getRequestFactory().getClientService().getDefaultRestaurants()
            .fire(new GetRestListReceiver());

    }

    @Override
    public void onFailure(ServerFailure error)
    {
        Window.alert("Can't get user data! " + error.getMessage());
        isAdmin = false;
        createRestsListPanel();
    }

    private void showPopup(String msg)
    {
        infoPopupText.setText(msg);
        infoPopup.setWidget(infoPopupText);
        infoPopup.center();
        infoPopup.show();
    }

    private void hidePopup()
    {
        infoPopup.clear();
        infoPopup.hide();
    }

    private void createHeaderProfilePanel()
    {
        if (null != user)
        {
            headerProfilePanel = new HeaderProfilePanel(user);
            RootPanel.get(FoodCenter.GWT_CONTINER).add(headerProfilePanel);
        }
    }

    private void createRestsListPanel()
    {
        restsPanel = new RestaurantsListPanel(rests, restCallback, restSearchCallback, isAdmin);
        RootPanel.get(FoodCenter.GWT_CONTINER).add(restsPanel);
    }

    /* ************************************************************************** */
    /* ************************ Private Classes ********************************* */
    /* ************************************************************************** */
    private class RestListCallbackImp implements
                                     PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest>
    {

        private final BlockingPopupPanel blockingPopup = new BlockingPopupPanel();

        @Override
        public void close(RedrawablePanel panel, // panel === restsLists
                          RestaurantProxy rest)
        {
            blockingPopup.hide();
            panel.redraw();
            // You don't close the list! (or nothing happens :) )
        }

        @Override
        public void save(RedrawablePanel panel, // panel === restsLists
                         RestaurantProxy rest,
                         PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback,
                         RestaurantAdminServiceRequest service)
        {
            if (!rest.isEditable())
            {
                Window.alert("Premission Denied!");
                return;
            }

            blockingPopup.show();
            showPopup("Saving restaurant ...");

            service.saveRestaurant(rest).with(RestaurantProxy.REST_WITH)
                .fire(new RestaurantReceiver(callback, false));
        }

        @Override
        public void view(RedrawablePanel panel, // panel === restsLists
                         RestaurantProxy proxy,
                         PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback)
        {
            blockingPopup.show();
            showPopup("Loading restaurant...");

            ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
            service.getRestaurantById(proxy.getId()).with(RestaurantProxy.REST_WITH)
                .fire(new RestaurantReceiver(callback, false));
        }

        @Override
        public void edit(RedrawablePanel panel, // panel === restsLists
                         RestaurantProxy rest,
                         PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback)
        {
            if (!rest.isEditable())
            {
                error(panel, rest, "Permission Denied!");
                return;
            }

            blockingPopup.show();
            showPopup("Loading restaurant ...");

            ClientServiceRequest service = RequestUtils.getRequestFactory().getClientService();
            service.getRestaurantById(rest.getId()).with(RestaurantProxy.REST_WITH)
                .fire(new RestaurantReceiver(callback, true));
        }

        @Override
        public void
            createNew(RedrawablePanel panel, // panel === restsLists
                      PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback)
        {
            if (!isAdmin)
            {
                error(panel, null, "Permission Denied!");
                return;
            }

            blockingPopup.show();
            RestaurantAdminServiceRequest service = RequestUtils.getRequestFactory()
                .getRestaurantAdminService();
            RestaurantProxy rest = RequestUtils.createRestaurantProxy(service);
            new RestaurantPanel(rest, callback, service);
        }

        @Override
        public void del(RedrawablePanel restListPanel, // panel === restsLists
                        RestaurantProxy rest)
        {
            if (!isAdmin)
            {
                error(restListPanel, rest, "Permission Denied!");
                return;
            }
            // Was not saved yet
            if (null == rest.getId())
            {
                error(restListPanel, rest, "Doesn't exists");
            }

            blockingPopup.show();
            showPopup("Deleting restaurant ...");
            AdminServiceRequest service = RequestUtils.getRequestFactory().getAdminService();
            service.deleteRestaurant(rest.getId()).fire(new DelRestReceiver(rest));

        }

        @Override
        public void error(RedrawablePanel panel, // is the list panel
                          RestaurantProxy proxy,
                          String reason)
        {
            Window.alert("Error: " + reason);
            blockingPopup.hide();
        }

    }

    private class RestaurantReceiver extends Receiver<RestaurantProxy>
    {
        private final PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback;
        private final boolean isEditMode;

        public RestaurantReceiver(PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback,
                                  boolean isEditMode)
        {
            this.callback = callback;
            this.isEditMode = isEditMode;
        }

        @Override
        public void onSuccess(RestaurantProxy response)
        {
            hidePopup();

            if (null == response)
            {
                Window.alert("Failed to save rest, returned null!");
                return;
            }

            // Update the old data if exists
            int idx = rests.indexOf(response);
            if (-1 != idx)
            {
                rests.set(idx, response);
            }
            else
            {
                rests.add(response);
            }
            restsPanel.redraw();

            RestaurantAdminServiceRequest service = null;
            if (isEditMode)
            {
                service = RequestUtils.getRequestFactory().getRestaurantAdminService();
                response = service.edit(response);
            }

            new RestaurantPanel(response, callback, service);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            Window.alert("Failed to save rest: " + error.getMessage());
        }

    }

    private class DelRestReceiver extends Receiver<Boolean>
    {
        private final RestaurantProxy rest;

        public DelRestReceiver(RestaurantProxy rest)
        {
            this.rest = rest;
        }

        @Override
        public void onSuccess(Boolean response)
        {
            if (true == response)
            {
                rests.remove(rest);
                restsPanel.redraw();
            }
            hidePopup();

        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
        }

    }

    private class RestSearchCallback implements SearchPanelCallback<RestaurantSearchOptions>
    {

        @Override
        public void search(RestaurantSearchOptions options)
        {
            showPopup("Loading Restaurants ...");

            RequestUtils.getRequestFactory().getClientService()
                .findRestaurant(options.getPattern(), options.getServices())
                .fire(new GetRestListReceiver());
        }
    }

    private class GetRestListReceiver extends Receiver<List<RestaurantProxy>>
    {

        @Override
        public void onSuccess(List<RestaurantProxy> response)
        {
            if (null == response)
            {
                hidePopup();
                Window.alert("Get default rest recieved null");
                return;
            }
            rests.clear();
            rests.addAll(response);

            restsPanel.redraw();
            hidePopup();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            Window.alert("Can't get restaurants: " + error.getMessage());
        }
    }
}
