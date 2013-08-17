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
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.handlers.RedrawablePannel;
import foodcenter.client.handlers.EmailHandler;
import foodcenter.client.handlers.RestaurantBranchHandler;
import foodcenter.client.panels.restaurant.BranchesFlexTable;
import foodcenter.client.panels.restaurant.MenuFlexTable;
import foodcenter.client.panels.restaurant.RestaurantProfilePannel;
import foodcenter.client.panels.restaurant.UsersPannel;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.requset.RestaurantAdminServiceRequest;

public class RestaurantPanel extends VerticalPanel
{

    private final RestaurantAdminServiceRequest requestService;
    private final RestaurantProxy rest;

    private final Boolean isEditMode;

    private final Runnable afterSave;
    private final Runnable afterClose;
    private final Runnable onClickEdit;

    private final Panel hPanel;
    private final Panel sPanel;
    
    private final List<String> addedAdmins;
    private final List<RestaurantBranchProxy> addedBranches;

    public RestaurantPanel(RestaurantAdminServiceRequest requestService,
                           RestaurantProxy rest,
                           Boolean isEditMode,
                           Runnable afterSave,
                           Runnable afterClose,
                           Runnable onClickEdit)
    {
        super();
        
        this.requestService = requestService;
        this.rest = rest;
        this.isEditMode = isEditMode;
        this.afterSave = afterSave;
        this.afterClose = afterClose;
        this.onClickEdit = onClickEdit;

        this.addedAdmins = new LinkedList<String>();
        this.addedBranches = new LinkedList<RestaurantBranchProxy>();
        
        this.hPanel = createHorizonalPanel();
        add(hPanel);

        this.sPanel = createMainStackPanel();
        add(sPanel);
    }

    private Panel createHorizonalPanel()
    {
        HorizontalPanel hpanel = new HorizontalPanel();

        Button discardButton = new Button("Close");
        discardButton.addClickHandler(new CloseRestClickHandler());
        hpanel.add(discardButton);

        if (isEditMode)
        {
            Button saveButton = new Button("Save");
            saveButton.addClickHandler(new SaveRestClickHandler());
            hpanel.add(saveButton);
        }
        else if (rest.isEditable())
        {
            Button editButton = new Button("Edit");
            editButton.addClickHandler(new EditRestClickHandler());
            hpanel.add(editButton);
        }

        return hpanel;
    }

    private Panel createMainStackPanel()
    {
        StackPanel stackPanel = new StackPanel();

        // profile pannel
        Panel profilePanel = new RestaurantProfilePannel(requestService, rest, isEditMode);
        Panel menuPanel = new MenuFlexTable(requestService, rest.getMenu(), isEditMode);
        
        EmailHandler addAdminEmailHandler = null;
        EmailHandler delAdminEmailHandler = null;
    
        RestaurantBranchHandler addBranchHandler = null; 
        RestaurantBranchHandler delBranchHandler = null;
    
        if (isEditMode)
        {
            addAdminEmailHandler = new AddRestAdminEmailHandler();
            delAdminEmailHandler = new DelRestAdminEmailHandler();
            
            addBranchHandler = new AddRestaurantBranchHandler();
            delBranchHandler = new DelRestaurantBranchHandler();
        }
        
        Panel adminsPanel = new UsersPannel(rest.getAdmins(),
                                            addedAdmins,
                                            addAdminEmailHandler,
                                            delAdminEmailHandler);
        
        
        Panel branchesPanel = new BranchesFlexTable(requestService,
                                                    rest.getBranches(),
                                                    addedBranches,
                                                    isEditMode,
                                                    addBranchHandler,
                                                    delBranchHandler);

        stackPanel.add(profilePanel, "Profile");
        stackPanel.add(menuPanel, "Menu");
        stackPanel.add(adminsPanel, "Admins");
        stackPanel.add(branchesPanel, "Braches");

        return stackPanel;

    }

    
    
    /* ********************************************************************* */
    /* *************************** private classes ************************* */
    /* ********************************************************************* */

    private class AddRestaurantBranchHandler implements RestaurantBranchHandler
    {

        @Override
        public void handle(RestaurantBranchProxy branch, RedrawablePannel panel)
        {
            if (!rest.getBranches().contains(branch) && !addedBranches.contains(branch))
            {
                requestService.addRestaurantBranch(rest, branch);
                addedBranches.add(branch);            
            }
            
            panel.redraw();
        }
    }
    
    private class DelRestaurantBranchHandler implements RestaurantBranchHandler
    {
        @Override
        public void handle(RestaurantBranchProxy branch, RedrawablePannel panel)
        {
            if (rest.getBranches().contains(branch))
            {
                requestService.removeRestaurantBranch(rest, branch);
                rest.getBranches().remove(branch);
                panel.redraw();
            }
            else if (addedBranches.contains(branch))
            {
                requestService.removeRestaurantBranch(rest, branch);
                addedBranches.remove(branch);
                panel.redraw();
            }
            else
            {
                Window.alert("Branch doesn't exists....");
            }
        }   
    }
    
    private class AddRestAdminEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel panel)
        {
            // Validate admin is not already defined
            if (rest.getAdmins().contains(email) || addedAdmins.contains(email))
            {
                Window.alert(email + " is already Admin");
                return;
            }
            
            // Add the admin
            requestService.addRestaurantAdmin(rest, email);

            // This is needed because the email will not be retrieved until service fire
            addedAdmins.add(email);
            
            // Redraw the panel to show the new email
            panel.redraw();
        }    
    }

    private class DelRestAdminEmailHandler implements EmailHandler
    {
        @Override
        public void handle(String email, RedrawablePannel pannel)
        {
            
            if (addedAdmins.contains(email))
            {
                addedAdmins.remove(email);
                requestService.removeRestaurantAdmin(rest, email);           
            }
            else if (rest.getAdmins().contains(email))
            {
                rest.getAdmins().remove(email);
                requestService.removeRestaurantAdmin(rest, email);
            }
            else
            {
                Window.alert(email + " isn't Admin");
            }
            pannel.redraw();
        }    
    }

    class CloseRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            // Call after close callback
            if (null != afterClose)
            {
                afterClose.run();
            }
        }
    }

    class SaveRestClickHandler extends CloseRestClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            // Do some closer
            super.onClick(event);

            // Save the restaurant
            requestService.saveRestaurant(rest).with(RestaurantProxy.REST_WITH)
                .fire(new SaveRestaurantReciever());
        }
    }

    private class SaveRestaurantReciever extends Receiver<RestaurantProxy>
    {
        @Override
        public void onSuccess(RestaurantProxy response)
        {
            // Call after save callback
            if (null != afterSave)
            {
                afterSave.run();
            }
        }
        
        @Override
        public void onFailure(ServerFailure error)
        {
            Window.alert(error.getMessage());
        }

    }

    class EditRestClickHandler extends CloseRestClickHandler
    {

        @Override
        public void onClick(ClickEvent event)
        {
            super.onClick(event);

            if (null != onClickEdit)
            {
                onClickEdit.run();
            }

        }

    }

}
