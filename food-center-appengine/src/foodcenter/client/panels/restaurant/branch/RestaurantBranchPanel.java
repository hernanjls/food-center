package foodcenter.client.panels.restaurant.branch;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.panels.common.UsersPannel;
import foodcenter.client.panels.restaurant.internal.MenuPanel;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestaurantBranchPanel extends VerticalPanel
{

    private final RestaurantBranchAdminServiceRequest requestContext;
    private final RestaurantBranchProxy branch;
    private final Boolean isEditMode;
    private final Runnable afterClose;
    private final Runnable afterOk;

    private Panel hPanel;
    private final Panel sPanel;

    public RestaurantBranchPanel(RestaurantBranchAdminServiceRequest requestContext,
                                 RestaurantBranchProxy branch,
                                 Boolean isEditMode,
                                 Runnable afterClose,
                                 Runnable afterOk)
    {
        super();

        this.requestContext = requestContext;
        this.branch = branch;
        this.isEditMode = isEditMode;
        this.afterClose = afterClose;
        this.afterOk = afterOk;

        this.hPanel = createHorizonalButtonsPanel();
        add(hPanel);

        this.sPanel = createMainStackPanel();
        add(sPanel);

    }

    private Panel createHorizonalButtonsPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        Button close = new Button("Close");
        close.addClickHandler(new CloseClickHandler());
        res.add(close);

        if (isEditMode)
        {
            close.setText("Cancel");

            Button save = new Button("Ok");
            save.addClickHandler(new SaveClickHandler());
            res.add(save);
        }

        return res;
    }

    private Panel createMainStackPanel()
    {
        StackPanel res = new StackPanel();

        Panel locationPanel = new RestaurantBranchLocationVerticalPanel(branch, isEditMode);
        res.add(locationPanel, "Location");

        Panel menuPanel = new MenuPanel(requestContext, branch.getMenu(), isEditMode);
        res.add(menuPanel, "Menu");

        if (isEditMode)
        {
            Panel adminsPanel = new UsersPannel(branch.getAdmins(), isEditMode);
            res.add(adminsPanel, "Admins");
            
            Panel waitersPanel = new UsersPannel(branch.getWaiters(), isEditMode);
            res.add(waitersPanel, "Waiters");
            
            Panel chefsPanel = new UsersPannel(branch.getChefs(), isEditMode);
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
            if (null != afterClose)
            {
                afterClose.run();
            }
        }
    }

    private class SaveClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != afterClose)
            {
                afterClose.run();
            }

            if (null != afterOk)
            {
                afterOk.run();
            }
        }

    }

}
