package foodcenter.client.panels.restaurant.branch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.panels.common.UsersPanel;
import foodcenter.client.panels.restaurant.internal.MenuPanel;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantAdminServiceRequest;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestaurantBranchPanel extends PopupPanel implements RedrawablePanel
{

    private final RestaurantBranchAdminServiceRequest service;
    private final RestaurantBranchProxy branch;
    private final PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback;

    private final boolean isEditMode;
    private final VerticalPanel main;

    public RestaurantBranchPanel(RestaurantBranchProxy branch,
                                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
    {
        this(branch, callback, null);
    }

    public RestaurantBranchPanel(RestaurantBranchProxy branch,
                                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback,
                                 RestaurantBranchAdminServiceRequest service)
    {
        super();

        this.branch = branch;
        this.callback = callback;
        this.service = service;

        this.isEditMode = (service != null);

        setStyleName("popup-common");

        this.main = new VerticalPanel();
        main.setStyleName("popup-main-panel");

        // Add the main Panel
        add(main);

        // Show this Panel
        show();

        // Draw the main Panel's data
        redraw();
    }

    @Override
    public void redraw()
    {
        main.clear();

        main.add(createButtonsPanel());
        main.add(createDetailsPanel());

        center();
        setPopupPosition(getAbsoluteLeft(), 60);
    }

    @Override
    public void close()
    {
        removeFromParent();
    }

    private Panel createButtonsPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        Button close = new Button("Close", new CloseClickHandler());
        res.add(close);

        if (isEditMode)
        {
            Button save = new Button("Save", new SaveClickHandler());
            res.add(save);
        }
        else if (branch.isEditable())
        {
            Button edit = new Button("Edit", new EditClickHandler());
            res.add(edit);
        }

        return res;
    }

    private Widget createDetailsPanel()
    {
        TabPanel res = new TabPanel();
        res.setWidth("100%");
//        res.setHeight("250px");

        Panel locationPanel = new RestaurantBranchLocationVerticalPanel(branch, isEditMode);
        res.add(locationPanel, "Location");
        res.selectTab(res.getTabBar().getTabCount() - 1);
        
        Panel menuPanel = new MenuPanel(branch.getMenu(), service);
        res.add(menuPanel, "Menu");

        if (branch.isEditable())
        {
            Panel adminsPanel = new UsersPanel(branch.getAdmins(), isEditMode);
            res.add(adminsPanel, "Admins");

            Panel waitersPanel = new UsersPanel(branch.getWaiters(), isEditMode);
            res.add(waitersPanel, "Waiters");

            Panel chefsPanel = new UsersPanel(branch.getChefs(), isEditMode);
            res.add(chefsPanel, "Chefs");
        }

        // TODO tables res.add(tablesPanel, "Tables");
        // TODO orders res.add(ordersPanel, "Orders");
        return res;
    }

    /* ********************************************************************* */
    /* ************************* Private Classes *************************** */
    /* ********************************************************************* */

    private class CloseClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.close(RestaurantBranchPanel.this, branch);
        }
    }

    private class EditClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(RestaurantBranchPanel.this, branch, callback);
        }
    }

    private class SaveClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.save(RestaurantBranchPanel.this,
                          branch,
                          callback,
                          (RestaurantAdminServiceRequest) service);
        }
    }

}
