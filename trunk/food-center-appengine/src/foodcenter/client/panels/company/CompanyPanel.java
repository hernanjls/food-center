package foodcenter.client.panels.company;

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
import foodcenter.client.panels.company.branch.CompanyBranchPanel;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.requset.CompanyAdminServiceRequest;
import foodcenter.service.requset.CompanyBranchAdminServiceRequest;

public class CompanyPanel extends PopupPanel implements RedrawablePanel
{
    // Variables from CTOR
    private final CompanyProxy comp;
    private final PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback;
    private final CompanyAdminServiceRequest service;

    // Class variables
    private final CompanyBranchCallback branchListCallback;
    private final boolean isEditMode;
    private final VerticalPanel main;
    private final List<CompanyBranchProxy> addedBranches;

    private final Label infoPopupText; // for setting the popup text
    private final PopupPanel infoPopup; // info popup which can be shown whenever needed

    public CompanyPanel(CompanyProxy comp,
                        PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback)
    {
        this(comp, callback, null);
    }

    public CompanyPanel(CompanyProxy comp,
                        PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback,
                        CompanyAdminServiceRequest service)
    {
        super(false);

        this.comp = comp;
        this.callback = callback;
        this.service = service;

        setStyleName("popup-common");

        infoPopup = new PopupPanel(false);
        infoPopupText = new Label();

        this.branchListCallback = new CompanyBranchCallback();
        this.isEditMode = (null != service);
        this.addedBranches = new LinkedList<CompanyBranchProxy>();

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
        main.add(createCompanyDetailsPanel());

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
        else if (comp.isEditable())
        {
            Button editButton = new Button("Edit", new EditRestClickHandler());
            buttonsPannel.add(editButton);
        }

        return buttonsPannel;
    }

    private Widget createCompanyDetailsPanel()
    {
        TabPanel detailsPanel = new TabPanel();
        detailsPanel.setWidth("100%");
        Panel profilePanel = new CompanyProfilePannel(comp, isEditMode);
        detailsPanel.add(profilePanel, "Profile");
        detailsPanel.selectTab(detailsPanel.getTabBar().getTabCount() - 1);
        if (comp.isEditable())
        {
            Panel adminsPanel = new UsersPanel(comp.getAdmins(), isEditMode);
            detailsPanel.add(adminsPanel, "Admins");
        }

        Panel branchesPanel = new CompanyBranchesListPanel(comp.getBranches(),
                                                           addedBranches,
                                                           branchListCallback,
                                                           isEditMode,
                                                           comp.isEditable());
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

    private class CompanyBranchCallback
                                       implements
                                       PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest>
    {
        private final BlockingPopupPanel blockingPopup = new BlockingPopupPanel();

        @Override
        public void close(RedrawablePanel listPanel, CompanyBranchProxy branch)
        {
            blockingPopup.hide();
            listPanel.redraw();
        }

        @Override
        public void
            save(RedrawablePanel listPanel,
                 CompanyBranchProxy branch,
                 PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback,
                 CompanyBranchAdminServiceRequest service)
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

            if (isEditMode) // this also means comp.isEditable()!
            {

                // In edit mode always service = Panel.this.service!
                // (only edit and createNew pass the service.)
                CompanyAdminServiceRequest admin = (CompanyAdminServiceRequest) service;
                if (!comp.getBranches().contains(branch) && !addedBranches.contains(branch))
                {
                    addedBranches.add(branch);
                    admin.addCompanyBranch(comp, branch);
                }
                listPanel.redraw();
                return;
            }

            blockingPopup.show();
            showPopup("Saveing companies ...");
            // Since not in edit mode, branch should save itself!
            service.saveCompanyBranch(branch).fire(new SaveRestReciever(callback));
        }

        @Override
        public void
            view(RedrawablePanel panel,
                 CompanyBranchProxy proxy,
                 PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            blockingPopup.show();
            new CompanyBranchPanel(proxy, callback);
        }

        @Override
        public void
            edit(RedrawablePanel panel, // list panel
                 CompanyBranchProxy branch,
                 PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            if (!branch.isEditable())
            {
                error(panel, branch, "Edit Permission Denied!");
                return;
            }

            CompanyBranchAdminServiceRequest branchService = null;
            if (isEditMode)
            {
                branchService = service;
            }
            else
            {
                branchService = WebRequestUtils.getRequestFactory().getCompanyAdminService();
                branch = branchService.edit(branch);
            }

            blockingPopup.show();
            new CompanyBranchPanel(branch, callback, branchService);
        }

        @Override
        public void del(RedrawablePanel panel, CompanyBranchProxy branch)
        {
            if (!isEditMode)
            {
                // Must be in edit mode to do comp.getBranches().remove(obj)
                error(panel, branch, "Please click edit first.");
                return;
            }

            if (!comp.isEditable()) // not a comp admin
            {
                error(panel, branch, "Permission Denied!");
                return;
            }

            if (comp.getBranches().contains(branch))
            {
                service.removeCompanyBranch(comp, branch); // TODO is this needed ?
                comp.getBranches().remove(branch);
                panel.redraw();
            }
            else if (addedBranches.contains(branch))
            {
                service.removeCompanyBranch(comp, branch); // TODO is this needed ?
                addedBranches.remove(branch);
                panel.redraw();
            }
            else
            {
                error(panel, branch, "Branch doesn't exists....");
                return;
            }
        }

        @Override
        public void error(RedrawablePanel panel, CompanyBranchProxy proxy, String reason)
        {
            callback.error(panel, null, reason);
        }

        @Override
        public void
            createNew(RedrawablePanel pannel,
                      PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            if (!comp.isEditable())
            {
                // Should never be called!
                error(null, null, "Premission Denied");
                return;
            }

            CompanyAdminServiceRequest admin = null;
            if (isEditMode)
            {
                admin = service; // Company Admin service!
            }
            else
            {
                admin = WebRequestUtils.getRequestFactory().getCompanyAdminService(); // CompanyAdmin
                                                                                   // on purpose!
            }

            blockingPopup.show();
            CompanyBranchProxy newBranch = WebRequestUtils.createCompanyBranchProxy(admin);
            new CompanyBranchPanel(newBranch, callback, admin);
        }

    }

    class EditRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(CompanyPanel.this, comp, callback);
        }
    }

    class CloseRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.close(CompanyPanel.this, comp);
        }
    }

    class SaveRestClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.save(CompanyPanel.this, comp, callback, service);
        }
    }

    class SaveRestReciever extends Receiver<CompanyBranchProxy>
    {

        private final PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback;

        public SaveRestReciever(PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            this.callback = callback;
        }

        @Override
        public void onSuccess(CompanyBranchProxy response)
        {
            hidePopup();
            // When in edit mode branch can't fire save itself
            // This occurs only from view mode -> reload the companies in view mode
            CompanyPanel.this.redraw();
            new CompanyBranchPanel(response, callback);
        }

        @Override
        public void onFailure(ServerFailure error)
        {
            hidePopup();
            callback.error(CompanyPanel.this, null, error.getMessage());
        }

    }
}
