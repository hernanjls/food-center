package foodcenter.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.callbacks.SearchPanelCallback;
import foodcenter.client.callbacks.search.CompanySearchOptions;
import foodcenter.client.callbacks.search.RestaurantSearchOptions;
import foodcenter.client.panels.common.BlockingPopupPanel;
import foodcenter.client.panels.common.HeaderProfilePanel;
import foodcenter.client.panels.company.CompanyPanel;
import foodcenter.client.panels.main.CompaniesListPanel;
import foodcenter.client.panels.main.RestaurantsListPanel;
import foodcenter.client.panels.restaurant.RestaurantPanel;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;
import foodcenter.service.requset.AdminServiceRequest;
import foodcenter.service.requset.ClientServiceRequest;
import foodcenter.service.requset.CompanyAdminServiceRequest;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class FoodCenter extends Receiver<UserProxy> implements EntryPoint, Runnable
{

    // private static final String GWT_MENU_CONTAINER = "gwtMenuContainer";
    public static final String GWT_CONTINER = "gwtContainer";

    public static final String TAB_RESTS_NAME = "Restaurants";
    public static final String TAB_COMPS_NAME = "Companies";

    // Data structures!!!
    private boolean isAdmin;
    private UserProxy user;

    // Restaurant staff
    private final List<RestaurantProxy> rests;
    private final PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> restCallback;
    private final SearchPanelCallback<RestaurantSearchOptions> restSearchCallback;

    // Companies staff
    private final List<CompanyProxy> comps;
    private final PanelCallback<CompanyProxy, CompanyAdminServiceRequest> compsCallback;
    private final SearchPanelCallback<CompanySearchOptions> compSearchCallback;

    // Panels
    private HeaderProfilePanel headerProfilePanel;
    private TabPanel tabs; // main panel to hold other panels
    private RestaurantsListPanel restsPanel;
    private CompaniesListPanel compsPanel;

    private final Label infoPopupText; // for setting the popup text
    private final PopupPanel infoPopup; // info popup which can be shown whenever needed

    public FoodCenter()
    {
        infoPopup = new PopupPanel(false);
        infoPopupText = new Label();

        tabs = new TabPanel();

        // Initialize Restaurant List needed variables
        rests = new LinkedList<RestaurantProxy>();
        restCallback = new RestListCallbackImp();
        restSearchCallback = new RestSearchCallback();

        // Initialize Company List needed variables
        comps = new LinkedList<CompanyProxy>();
        compsCallback = new CompListCallbackImp();
        compSearchCallback = new CompSearchCallback();
     
    }

    @Override
    public void onModuleLoad()
    {

        // load the maps api!
        Maps.loadMapsApi(WebClientUtils.BROWSER_API_KEY_MAPS,
                         WebClientUtils.GOOGLE_API_MAPS_VER,
                         false,
                         this);

        showPopup("Loading user data...");

        // Load the user's data
        ClientServiceRequest service = WebRequestUtils.getRequestFactory().getClientService();
        service.login(null).fire(this);
    }

    /** Will be called after the maps api is loaded */
    @Override
    public void run()
    {
        // do nothing!
    }

    public void afterLoadingUserData()
    {
        RootPanel.get(FoodCenter.GWT_CONTINER).add(tabs);
        tabs.setWidth("100%");

        restsPanel = new RestaurantsListPanel(rests, restCallback, restSearchCallback, isAdmin);
        tabs.add(restsPanel, TAB_RESTS_NAME);
        tabs.selectTab(tabs.getTabBar().getTabCount() - 1);

        compsPanel = new CompaniesListPanel(comps, compsCallback, compSearchCallback, isAdmin);
        tabs.add(compsPanel, TAB_COMPS_NAME);

        // Load the restaurants when map loading is done!
        WebRequestUtils.getRequestFactory().getClientService().getDefaultRestaurants()
            .fire(new GetRestListReceiver());
        
        // Load the restaurants when map loading is done!
        WebRequestUtils.getRequestFactory().getClientService().getDefaultCompanies()
            .fire(new GetCompListReceiver());
    }

    @Override
    public void onSuccess(UserProxy response)
    {
        showPopup("Loading Data ...");
        user = response;

        if (null == user)
        {
            Window.alert("Can't get user's info...");
            isAdmin = false;
        }
        else
        {
            isAdmin = user.isAdmin();
        }

        createHeaderProfilePanel();
        afterLoadingUserData();
    }

    @Override
    public void onFailure(ServerFailure error)
    {
        Window.alert("Can't get user data! " + error.getMessage());
        isAdmin = false;
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

            ClientServiceRequest service = WebRequestUtils.getRequestFactory().getClientService();
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

            ClientServiceRequest service = WebRequestUtils.getRequestFactory().getClientService();
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
            RestaurantAdminServiceRequest service = WebRequestUtils.getRequestFactory()
                .getRestaurantAdminService();
            RestaurantProxy rest = WebRequestUtils.createRestaurantProxy(service);
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
            AdminServiceRequest service = WebRequestUtils.getRequestFactory().getAdminService();
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

    /* ************************************************************************** */

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
                service = WebRequestUtils.getRequestFactory().getRestaurantAdminService();
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

    /* ************************************************************************** */

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
            Window.alert("Failed to delete rest: " + error.getMessage());
            hidePopup();
        }
    }

    /* ************************************************************************** */

    private class RestSearchCallback implements SearchPanelCallback<RestaurantSearchOptions>
    {

        @Override
        public void search(RestaurantSearchOptions options)
        {
            showPopup("Loading Restaurants ...");

            WebRequestUtils.getRequestFactory().getClientService()
                .findRestaurant(options.getPattern(), options.getServices())
                .fire(new GetRestListReceiver());
        }
    }

    /* ************************************************************************** */

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

    /* *************************************************************** */

    private class CompListCallbackImp implements
                                     PanelCallback<CompanyProxy, CompanyAdminServiceRequest>
    {

        private final BlockingPopupPanel blockingPopup = new BlockingPopupPanel();

        @Override
        public void close(RedrawablePanel panel, // panel === restsLists
                          CompanyProxy rest)
        {
            blockingPopup.hide();
            panel.redraw();
            // You don't close the list! (or nothing happens :) )
        }

        @Override
        public void save(RedrawablePanel panel, // panel === restsLists
                         CompanyProxy comp,
                         PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback,
                         CompanyAdminServiceRequest service)
        {
            if (!comp.isEditable())
            {
                Window.alert("Premission Denied!");
                return;
            }

            blockingPopup.show();
            showPopup("Saving restaurant ...");

            service.saveCompany(comp).with(CompanyProxy.COMP_WITH)
                .fire(new CompanyReceiver(callback, false));
        }

        @Override
        public void view(RedrawablePanel panel, // panel === restsLists
                         CompanyProxy proxy,
                         PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback)
        {
            blockingPopup.show();
            showPopup("Loading company...");

            ClientServiceRequest service = WebRequestUtils.getRequestFactory().getClientService();
            service.getCompanyById(proxy.getId()).with(CompanyProxy.COMP_WITH)
                .fire(new CompanyReceiver(callback, false));
        }

        @Override
        public void edit(RedrawablePanel panel, // panel === restsLists
                         CompanyProxy comp,
                         PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback)
        {
            if (!comp.isEditable())
            {
                error(panel, comp, "Permission Denied!");
                return;
            }

            blockingPopup.show();
            showPopup("Loading restaurant ...");

            ClientServiceRequest service = WebRequestUtils.getRequestFactory().getClientService();
            service.getCompanyById(comp.getId()).with(CompanyProxy.COMP_WITH)
                .fire(new CompanyReceiver(callback, true));
        }

        @Override
        public void createNew(RedrawablePanel panel, // panel === restsLists
                              PanelCallback<CompanyProxy, //
                              CompanyAdminServiceRequest> callback)
        {
            if (!isAdmin)
            {
                error(panel, null, "Permission Denied!");
                return;
            }

            blockingPopup.show();
            CompanyAdminServiceRequest service = WebRequestUtils.getRequestFactory()
                .getCompanyAdminService();
            CompanyProxy comp = WebRequestUtils.createCompanyProxy(service);
            new CompanyPanel(comp, callback, service);
        }

        @Override
        public void del(RedrawablePanel compsListPanel, // panel === restsLists
                        CompanyProxy comp)
        {
            if (!isAdmin)
            {
                error(compsListPanel, comp, "Permission Denied!");
                return;
            }
            // Was not saved yet
            if (null == comp.getId())
            {
                error(compsListPanel, comp, "Doesn't exists");
            }

            blockingPopup.show();
            showPopup("Deleting company ...");
            AdminServiceRequest service = WebRequestUtils.getRequestFactory().getAdminService();
            service.deleteRestaurant(comp.getId()).fire(new DelCompReceiver(comp));

        }

        @Override
        public void error(RedrawablePanel panel, // is the list panel
                          CompanyProxy proxy,
                          String reason)
        {
            Window.alert("Error: " + reason);
            blockingPopup.hide();
        }

    }

    /* ************************************************************************** */

    private class CompanyReceiver extends Receiver<CompanyProxy>
    {
        private final PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback;
        private final boolean isEditMode;

        public CompanyReceiver(PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback,
                               boolean isEditMode)
        {
            this.callback = callback;
            this.isEditMode = isEditMode;
        }

        @Override
        public void onSuccess(CompanyProxy response)
        {
            hidePopup();

            if (null == response)
            {
                Window.alert("Failed to save rest, returned null!");
                return;
            }

            // Update the old data if exists
            int idx = comps.indexOf(response);
            if (-1 != idx)
            {
                comps.set(idx, response);
            }
            else
            {
                comps.add(response);
            }
            compsPanel.redraw();

            CompanyAdminServiceRequest service = null;
            if (isEditMode)
            {
                service = WebRequestUtils.getRequestFactory().getCompanyAdminService();
                response = service.edit(response);
            }

            new CompanyPanel(response, callback, service);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            Window.alert("Failed to get company: " + error.getMessage());
        }

    }

    /* ************************************************************************** */

    private class DelCompReceiver extends Receiver<Boolean>
    {
        private final CompanyProxy comp;

        public DelCompReceiver(CompanyProxy comp)
        {
            this.comp = comp;
        }

        @Override
        public void onSuccess(Boolean response)
        {
            if (true == response)
            {
                comps.remove(comp);
                compsPanel.redraw();
            }
            hidePopup();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            Window.alert("Failed to delete comp: " + error.getMessage());
        }

    }

    /* ************************************************************************** */

    private class CompSearchCallback implements SearchPanelCallback<CompanySearchOptions>
    {

        @Override
        public void search(CompanySearchOptions options)
        {
            showPopup("Loading Companies ...");

            WebRequestUtils.getRequestFactory().getClientService().findCompany(options.getPattern(),
                                                                            options.getServices())
                .fire(new GetCompListReceiver());
        }
    }

    /* ************************************************************************** */

    private class GetCompListReceiver extends Receiver<List<CompanyProxy>>
    {
        @Override
        public void onSuccess(List<CompanyProxy> response)
        {
            if (null == response)
            {
                hidePopup();
                Window.alert("Get default comp recieved null");
                return;
            }
            comps.clear();
            comps.addAll(response);

            compsPanel.redraw();
            hidePopup();
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            Window.alert("Can't get companies: " + error.getMessage());
        }
    }

}
