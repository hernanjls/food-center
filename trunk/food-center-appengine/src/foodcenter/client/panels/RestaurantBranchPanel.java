package foodcenter.client.panels;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.client.panels.restaurant.MenuFlexTable;
import foodcenter.client.panels.restaurant.UsersPannel;
import foodcenter.client.panels.restaurant.branch.RestaurantBranchLocationVerticalPanel;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.UserProxy;

public class RestaurantBranchPanel extends VerticalPanel
{

    private final RequestContext requestContext;
    private final RestaurantBranchProxy branch;
    private final Boolean isAdmin;
    private final Runnable onClose;
    
    private Panel hPanel;
    private final Panel sPanel;

    public RestaurantBranchPanel(RequestContext requestContext, RestaurantBranchProxy branch, Boolean isAdmin, Runnable onClose)
    {
        super();

        this.requestContext = requestContext;
        this.branch = branch;
        this.isAdmin = isAdmin;
        this.onClose = onClose;
        
        if (null != onClose)
        {
            this.hPanel = createHorizonalButtonsPanel();
            add(hPanel);
        }
        
        this.sPanel = createMainStackPanel();
        add(sPanel);

    }

    private Panel createHorizonalButtonsPanel()
    {
        HorizontalPanel res = new HorizontalPanel();

        Button save = new Button("Close");
        save.addClickHandler(new CloseClickHandler());
        
        res.add(save);
        return res;
    }

    private Panel createMainStackPanel()
    {
        StackPanel res = new StackPanel();
       
        Panel locationPanel = new RestaurantBranchLocationVerticalPanel(branch);
        Panel menuPanel = new MenuFlexTable(requestContext, branch.getMenu(), isAdmin);
        Panel adminsPanel = new UsersPannel(branch.getAdmins(), isAdmin);;
        Panel waitersPanel = new UsersPannel(branch.getWaiters(), isAdmin);
        Panel chefsPanel = new UsersPannel(branch.getChefs(), isAdmin);

        res.add(locationPanel, "Location");
        res.add(menuPanel, "Menu");
        res.add(adminsPanel, "Admins");
        res.add(waitersPanel, "Waiters");
        res.add(chefsPanel, "Chefs");
       
        //TODO tables res.add(tablesPanel, "Tables");
        //TODO orders res.add(ordersPanel, "Orders");
        return res;

    }
    
    private class CloseClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != onClose)
            {
                onClose.run();
            }
            
        }
        
    }

}
