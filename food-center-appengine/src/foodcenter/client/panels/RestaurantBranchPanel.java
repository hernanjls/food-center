package foodcenter.client.panels;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.handlers.EmailHandler;
import foodcenter.client.handlers.RedrawablePannel;
import foodcenter.client.panels.restaurant.MenuFlexTable;
import foodcenter.client.panels.restaurant.UsersPannel;
import foodcenter.client.panels.restaurant.branch.RestaurantBranchLocationVerticalPanel;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestaurantBranchPanel extends VerticalPanel
{

    private final RestaurantBranchAdminServiceRequest requestContext;
    private final RestaurantBranchProxy branch;
    private final Boolean isEditMode;
    private final Runnable afterClose;
    private final Runnable afterSave;

    private Panel hPanel;
    private final Panel sPanel;
    
    private final List<String> addedAdmins;
    private final List<String> addedWaiters;
    private final List<String> addedChefs;

    public RestaurantBranchPanel(RestaurantBranchAdminServiceRequest requestContext,
                                 RestaurantBranchProxy branch,
                                 Boolean isEditMode,
                                 Runnable afterClose,
                                 Runnable afterSave)
    {
        super();

        this.requestContext = requestContext;
        this.branch = branch;
        this.isEditMode = isEditMode;
        this.afterClose = afterClose;
        this.afterSave = afterSave;

        this.addedAdmins = new LinkedList<String>();
        this.addedWaiters = new LinkedList<String>();
        this.addedChefs = new LinkedList<String>();
        
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
        Panel menuPanel = new MenuFlexTable(requestContext, branch.getMenu(), isEditMode);
        
        
        EmailHandler adminAddHandler = null;
        EmailHandler adminDelHandler = null;

        EmailHandler waiterAddHandler = null;
        EmailHandler waiterDelHandler = null;

        EmailHandler chefAddHandler = null;
        EmailHandler chefDelHandler = null;

        if (isEditMode)
        {
            adminAddHandler = new AddAdminEmailHandler();
            adminDelHandler = new DelAdminEmailHandler();
            
            waiterAddHandler = new AddWaiterEmailHandler();
            waiterDelHandler = new DelWaiterEmailHandler();
            
            chefAddHandler = new AddChefEmailHandler();
            chefDelHandler = new DelChefEmailHandler();
        }
        
        Panel adminsPanel = new UsersPannel(branch.getAdmins(),
                                            addedAdmins,
                                            adminAddHandler,
                                            adminDelHandler);

        Panel waitersPanel = new UsersPannel(branch.getWaiters(),
                                             addedWaiters,
                                             waiterAddHandler,
                                             waiterDelHandler);

        Panel chefsPanel = new UsersPannel(branch.getChefs(),
                                           addedChefs,
                                           chefAddHandler,
                                           chefDelHandler);

        res.add(locationPanel, "Location");
        res.add(menuPanel, "Menu");
        res.add(adminsPanel, "Admins");
        res.add(waitersPanel, "Waiters");
        res.add(chefsPanel, "Chefs");

        // TODO tables res.add(tablesPanel, "Tables");
        // TODO orders res.add(ordersPanel, "Orders");
        return res;

    }

    
    
    
    
    
    
    
    
    /* ********************************************************************* */
    /* ************************* Private Classes *************************** */
    /* ********************************************************************* */
    
    private class AddAdminEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel panel)
        {            
            // Validate admin is not already defined
            if (branch.getAdmins().contains(email) || addedAdmins.contains(email))
            {
                Window.alert(email + " is already Admin");
                return;
            }
            
            // Add the admin
            requestContext.addBranchAdmin(branch, email);

            // This is needed because the email will not be retrieved until service fire
            addedAdmins.add(email);
            
            // Redraw the panel to show the new email
            panel.redraw();
        }    
    }

    private class DelAdminEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel pannel)
        {
            
            if (addedAdmins.contains(email))
            {
                addedAdmins.remove(email);
                requestContext.removeBranchAdmin(branch, email);           
            }
            else if (branch.getAdmins().contains(email))
            {
                branch.getAdmins().remove(email);
                requestContext.removeBranchAdmin(branch, email);
            }
            else
            {
                Window.alert(email + " isn't Admin");
            }
            pannel.redraw();
        }    
    }


    private class AddWaiterEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel panel)
        {
            // Validate admin is not already defined
            if (branch.getWaiters().contains(email) || addedWaiters.contains(email))
            {
                Window.alert(email + " is already Waiter");
                return;
            }
            
            // Add the admin
            requestContext.addBranchWaiter(branch, email);

            // This is needed because the email will not be retrieved until service fire
            addedWaiters.add(email);
            
            // Redraw the panel to show the new email
            panel.redraw();
        }    
    }

    private class DelWaiterEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel pannel)
        {
            
            if (addedWaiters.contains(email))
            {
                addedWaiters.remove(email);
                requestContext.removeBranchWaiter(branch, email);           
            }
            else if (branch.getWaiters().contains(email))
            {
                branch.getWaiters().remove(email);
                requestContext.removeBranchWaiter(branch, email);
            }
            else
            {
                Window.alert(email + " isn't Waiter");
            }
            pannel.redraw();
        }    
    }

    private class AddChefEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel panel)
        {
            // Validate admin is not already defined
            if (branch.getChefs().contains(email) || addedChefs.contains(email))
            {
                Window.alert(email + " is already Chef");
                return;
            }
            
            // Add the admin
            requestContext.addBranchChef(branch, email);

            // This is needed because the email will not be retrieved until service fire
            addedChefs.add(email);
            
            // Redraw the panel to show the new email
            panel.redraw();
        }    
    }

    private class DelChefEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel pannel)
        {
            
            if (addedChefs.contains(email))
            {
                addedChefs.remove(email);
                requestContext.removeBranchChef(branch, email);           
            }
            else if (branch.getChefs().contains(email))
            {
                branch.getChefs().remove(email);
                requestContext.removeBranchChef(branch, email);
            }
            else
            {
                Window.alert(email + " isn't Chef");
            }
            pannel.redraw();
        }    
    }

    
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

            if (null != afterSave)
            {
                afterSave.run();
            }
        }

    }

}
