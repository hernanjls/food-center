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

import foodcenter.client.panels.restaurant.BranchesFlexTable;
import foodcenter.client.panels.restaurant.MenuFlexTable;
import foodcenter.client.panels.restaurant.RestaurantProfilePannel;
import foodcenter.client.panels.restaurant.UsersPannel;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.proxies.UserProxy;

public class RestaurantPanel extends VerticalPanel
{

    private final RequestContext requestContext;
    private final RestaurantProxy rest;
    private final Boolean isAdmin;
    private final Runnable onSave;
    private final Runnable onDiscard;
    
    private final Panel hPanel;
    private final Panel sPanel;

    public RestaurantPanel(RequestContext requestContext, RestaurantProxy rest, Boolean isAdmin, Runnable onSave, Runnable onDiscard)
    {
        super();
        this.requestContext = requestContext;
        this.rest = rest;
        this.isAdmin = isAdmin;
        this.onSave = onSave;
        this.onDiscard = onDiscard;

        
        this.hPanel = createHorizonalPanel();
        add(hPanel);
        
        this.sPanel = createStackPanel();
        add(sPanel);
    }

    private Panel createHorizonalPanel()
    {
        HorizontalPanel hpanel = new HorizontalPanel();
        
        Button saveButton = new Button("save");
        saveButton.addClickHandler(new SaveRestClickHandler());
        saveButton.setEnabled(isAdmin);
        hpanel.add(saveButton);
       
        Button discardButton = new Button("discard");
        discardButton.addClickHandler(new DiscardRestClickHandler());
        discardButton.setEnabled(isAdmin);
        hpanel.add(discardButton);
        
        return hpanel;
    }
    
    private Panel createStackPanel()
    {
        StackPanel stackPanel = new StackPanel();

        // profile pannel
        Panel profilePanel = new RestaurantProfilePannel(rest, isAdmin);
        Panel menuPanel = new MenuFlexTable(requestContext, rest.getMenu(), isAdmin);
        Panel adminsPanel = new UsersPannel(rest.getAdmins(), isAdmin);
        Panel branchesPanel =  new BranchesFlexTable(requestContext, rest, rest.getBranches(), isAdmin);

        // TODO fix order
        stackPanel.add(profilePanel, "Profile");
        stackPanel.add(menuPanel, "Menu");
        stackPanel.add(adminsPanel, "Admins");
        stackPanel.add(branchesPanel, "Braches");

        // requestFactory.getUserCommonService().getRestaurant(0L); //TODO get id from somewhere

        return stackPanel;

    }
        
    
    class SaveRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != onSave)
            {
                onSave.run();
            }
        }
    }

    class DiscardRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != onDiscard)
            {
                onDiscard.run();
            }
        }
    }

}
