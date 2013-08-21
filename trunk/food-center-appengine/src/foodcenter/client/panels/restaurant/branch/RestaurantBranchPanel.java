package foodcenter.client.panels.restaurant.branch;

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

import foodcenter.client.panels.common.UsersPannel;
import foodcenter.client.panels.restaurant.internal.MenuPanel;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestaurantBranchPanel extends VerticalPanel
{

    private final RestaurantBranchAdminServiceRequest service;
    private final RestaurantBranchProxy branch;
    private final boolean  isEditMode;
    private final Runnable closeCallback;
    private final Runnable saveCallback;
    private final Runnable editCallback;

    private Panel hPanel;
    private final Panel sPanel;

    public RestaurantBranchPanel(RestaurantBranchAdminServiceRequest service,
                                 RestaurantBranchProxy branch,
                                 boolean isEditMode,
                                 Runnable closeCallback,
                                 Runnable saveCallback,
                                 Runnable editCallback)
    {
        super();

        this.service = service;
        this.branch = branch;
        this.isEditMode = isEditMode;
        this.closeCallback = closeCallback;
        this.saveCallback = saveCallback;
        this.editCallback = editCallback;

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

            Button save = new Button("Save");
            save.addClickHandler(new SaveClickHandler());
            res.add(save);
        }
        else if (branch.isEditable())
        {
            Button edit = new Button("Edit");
            edit.addClickHandler(new EditClickHandler());
            res.add(edit);
        }

        return res;
    }

    private Panel createMainStackPanel()
    {
        StackPanel res = new StackPanel();
        
        Panel locationPanel = new RestaurantBranchLocationVerticalPanel(branch, isEditMode);
        res.add(locationPanel, "Location");

        Panel menuPanel = new MenuPanel(service, branch.getMenu(), isEditMode);
        res.add(menuPanel, "Menu");

        if (branch.isEditable())
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
            if (null != closeCallback)
            {
                closeCallback.run();
            }
        }
    }

    private class EditClickHandler extends CloseClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            super.onClick(event);
            
            if (null != editCallback)
            {
                editCallback.run();
            }
            
        }
    }
    
    private class SaveClickHandler extends Receiver<RestaurantBranchProxy> implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != saveCallback)
            {
                saveCallback.run();
                closeMe();
                
                return;
            }
            service.saveRestaurantBranch(branch).fire(this);
        }

        @Override
        public void onSuccess(RestaurantBranchProxy response)
        {
            closeMe();
        }
        
        @Override
        public void onFailure(ServerFailure error)
        {
            closeMe();
            Window.alert("Can't save branch " + error.getMessage());
        }
        
        private void closeMe()
        {
            Window.alert("Please refresh restaurant to see changes!");
            if (null != closeCallback)
            {
                closeCallback.run();
            }

        }

    }

}
