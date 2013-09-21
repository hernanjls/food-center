package foodcenter.client.panels.restaurant;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.panels.common.BlockingPopupPanel;
import foodcenter.client.panels.common.UsersPanel;
import foodcenter.client.panels.restaurant.branch.RestaurantBranchPanel;
import foodcenter.client.panels.restaurant.menu.MenuPanel;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;
import foodcenter.service.requset.RestaurantAdminServiceRequest;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

public class RestaurantPanel extends PopupPanel implements RedrawablePanel
{
    // Variables from CTOR
    private final RestaurantProxy rest;
    private final PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback;
    private final RestaurantAdminServiceRequest service;

    // Class variables
    private final RestaurantBranchCallback branchListCallback;
    private final boolean isEditMode;
    private final VerticalPanel main;
    private final List<RestaurantBranchProxy> addedBranches;
    private final List<RestaurantBranchProxy> deletedBranches;

    private final Label infoPopupText; // for setting the popup text
    private final PopupPanel infoPopup; // info popup which can be shown whenever needed

    private MenuPanel menuPanel = null;
    
    public RestaurantPanel(RestaurantProxy rest,
                           PanelCallback<RestaurantProxy, RestaurantAdminServiceRequest> callback,
                           RestaurantAdminServiceRequest service)
    {
        super(false);

        this.rest = rest;
        this.callback = callback;
        this.service = service;

        setStyleName("popup-common");

        infoPopup = new PopupPanel(false);
        infoPopupText = new Label();

        branchListCallback = new RestaurantBranchCallback();
        isEditMode = (null != service);
        addedBranches = new LinkedList<RestaurantBranchProxy>();
        deletedBranches = new LinkedList<RestaurantBranchProxy>();

        // Add the holder Panel
        this.main = new VerticalPanel();
        main.setStyleName("popup-main-panel");

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
        main.add(createRestaurantDetailsPanel());

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
        HorizontalPanel buttonsPannel = new HorizontalPanel();

        Button close = new Button("Close", new CloseRestClickHandler());
        buttonsPannel.add(close);

        if (isEditMode)
        {
            Button saveButton = new Button("Save", new SaveRestClickHandler());
            buttonsPannel.add(saveButton);
        }
        else if (rest.isEditable())
        {
            Button editButton = new Button("Edit", new EditRestClickHandler());
            buttonsPannel.add(editButton);
        }

        return buttonsPannel;
    }

    private Widget createRestaurantDetailsPanel()
    {
        TabPanel detailsPanel = new TabPanel();
        detailsPanel.setWidth("100%");
        Panel profilePanel = new RestaurantProfilePannel(rest, isEditMode);
        detailsPanel.add(profilePanel, "Profile");

        menuPanel = new MenuPanel(rest.getMenu(), service);
        detailsPanel.add(menuPanel, "Menu");
        detailsPanel.selectTab(detailsPanel.getTabBar().getTabCount() - 1);

        if (rest.isEditable())
        {
            Panel adminsPanel = new UsersPanel(rest.getAdmins(), isEditMode);
            detailsPanel.add(adminsPanel, "Admins");
        }

        Panel branchesPanel = new RestaurantBranchesListPanel(rest.getBranches(),
                                                              addedBranches,
                                                              deletedBranches,
                                                              branchListCallback,
                                                              isEditMode,
                                                              rest.isEditable());
        detailsPanel.add(branchesPanel, "Branches");

        return detailsPanel;
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

    /* ********************************************************************* */
    /* *************************** private classes ************************* */
    /* ********************************************************************* */

    private class RestaurantBranchCallback
                                          implements
                                          PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest>
    {
        private final BlockingPopupPanel blockingPopup = new BlockingPopupPanel();

        @Override
        public void close(RedrawablePanel listPanel, RestaurantBranchProxy branch)
        {
            blockingPopup.hide();
            listPanel.redraw();
        }

        @Override
        public void
            save(RedrawablePanel listPanel,
                 RestaurantBranchProxy branch,
                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback,
                 RestaurantBranchAdminServiceRequest service)
        {
            if (!branch.isEditable())
            {
                Window.alert("Premission Denied!");
                return;
            }

            if (null == branch.getId() && !isEditMode)
            {
                error(listPanel, branch, "New branch can be add only from edit mode!!");
                return;
            }

            if (isEditMode) // enter to edit branch from here this also means rest.isEditable()!
            {

                // In edit mode always service = Panel.this.service!
                // (only edit and createNew pass the service.)
                RestaurantAdminServiceRequest admin = (RestaurantAdminServiceRequest) service;
                if (!rest.getBranches().contains(branch) && !addedBranches.contains(branch))
                {
                    addedBranches.add(branch);
                    admin.addRestaurantBranch(rest, branch);
                }
                listPanel.redraw();
                return;
            }

            blockingPopup.show();
            showPopup("Saveing branch ...");
            // Since not in edit mode, branch should save itself!
            service.saveRestaurantBranch(branch)
                .with(RestaurantBranchProxy.BRANCH_WITH)
                .fire(new SaveRestReciever(callback));
        }

        @Override
        public void
            view(RedrawablePanel panel,
                 RestaurantBranchProxy proxy,
                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            blockingPopup.show();
            new RestaurantBranchPanel(proxy, callback);
        }

        @Override
        public void
            edit(RedrawablePanel panel, // list panel
                 RestaurantBranchProxy branch,
                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            if (!branch.isEditable())
            {
                error(panel, branch, "Edit Permission Denied!");
                return;
            }

            RestaurantBranchAdminServiceRequest branchService = null;
            if (isEditMode)
            {
                branchService = service;
            }
            else
            {
                branchService = WebRequestUtils.getRequestFactory().getRestaurantAdminService();
                branch = branchService.edit(branch);
            }

            blockingPopup.show();
            new RestaurantBranchPanel(branch, callback, branchService);
        }

        @Override
        public void del(RedrawablePanel panel, RestaurantBranchProxy branch)
        {
            if (!isEditMode)
            {
                // Must be in edit mode to do rest.getBranches().remove(obj)
                error(panel, branch, "Please click edit first.");
                return;
            }

            if (!rest.isEditable()) // not a rest admin
            {
                error(panel, branch, "Permission Denied!");
                return;
            }

            if (rest.getBranches().contains(branch))
            {
                service.removeRestaurantBranch(rest, branch);
                // rest.getBranches().remove(branch); // illegal - throws exception!
                deletedBranches.add(branch);
                panel.redraw();
            }
            else if (addedBranches.contains(branch))
            {
                service.removeRestaurantBranch(rest, branch);
                addedBranches.remove(branch);
                deletedBranches.add(branch);
                panel.redraw();
            }
            else
            {
                error(panel, branch, "Branch doesn't exists....");
                return;
            }
        }

        @Override
        public void error(RedrawablePanel panel, RestaurantBranchProxy proxy, String reason)
        {
            callback.error(panel, null, reason);
        }

        @Override
        public void
            createNew(RedrawablePanel pannel,
                      PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            if (!rest.isEditable())
            {
                // Should never be called!
                error(null, null, "Premission Denied");
                return;
            }

            RestaurantAdminServiceRequest admin = null;
            if (isEditMode)
            {
                admin = service; // Restaurant Admin service!
            }
            else
            {
                admin = WebRequestUtils.getRequestFactory().getRestaurantAdminService(); // RestaurantAdmin
                                                                                         // on
                                                                                         // purpose!
            }

            blockingPopup.show();
            RestaurantBranchProxy newBranch = WebRequestUtils.createRestaurantBranchProxy(admin);
            new RestaurantBranchPanel(newBranch, callback, admin);
        }

    }

    class EditRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(RestaurantPanel.this, rest, callback);
        }
    }

    class CloseRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.close(RestaurantPanel.this, rest);
        }
    }

    class SaveRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            menuPanel.setToService();
            callback.save(RestaurantPanel.this, rest, callback, service);
        }
    }

    class SaveRestReciever extends Receiver<RestaurantBranchProxy>
    {

        private final PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback;

        public SaveRestReciever(PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            this.callback = callback;
        }

        @Override
        public void onSuccess(RestaurantBranchProxy response)
        {
            hidePopup();
            // When in edit mode branch can't fire save itself
            // This occurs only from view mode -> reload the restaurant in view mode

            // TODO known issue, after saving the branch need to update the restaurant branches
            // because reopen the branch opens its old version
            RestaurantPanel.this.redraw();
            new RestaurantBranchPanel(response, callback);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            callback.error(RestaurantPanel.this, null, error.getMessage());
        }

    }
}
