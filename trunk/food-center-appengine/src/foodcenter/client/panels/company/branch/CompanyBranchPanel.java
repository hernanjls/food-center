package foodcenter.client.panels.company.branch;

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
import foodcenter.client.panels.common.BranchOrdersHistoryPanel;
import foodcenter.client.panels.common.UsersPanel;
import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.requset.CompanyAdminServiceRequest;
import foodcenter.service.requset.CompanyBranchAdminServiceRequest;

public class CompanyBranchPanel extends PopupPanel implements RedrawablePanel
{

    private final CompanyBranchAdminServiceRequest service;
    private final CompanyBranchProxy branch;
    private final PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback;

    private final boolean isEditMode;
    private final VerticalPanel main;

    public CompanyBranchPanel(CompanyBranchProxy branch,
                              PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
    {
        this(branch, callback, null);
    }

    public CompanyBranchPanel(CompanyBranchProxy branch,
                              PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback,
                              CompanyBranchAdminServiceRequest service)
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
        // res.setHeight("250px");

        Panel locationPanel = new CompanyBranchLocationVerticalPanel(branch, isEditMode);
        res.add(locationPanel, "Location");
        res.selectTab(res.getTabBar().getTabCount() - 1);

        if (branch.isEditable())
        {
            Panel adminsPanel = new UsersPanel(branch.getAdmins(), isEditMode);
            res.add(adminsPanel, "Admins");

            Panel workersPanel = new UsersPanel(branch.getWorkers(), isEditMode);
            res.add(workersPanel, "Workers");
            
            Panel orders = new BranchOrdersHistoryPanel(branch.getId(), false);
            res.add(orders, "Orders");
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
            callback.close(CompanyBranchPanel.this, branch);
        }
    }

    private class EditClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(CompanyBranchPanel.this, branch, callback);
        }
    }

    private class SaveClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.save(CompanyBranchPanel.this,
                          branch,
                          callback,
                          (CompanyAdminServiceRequest) service);
        }
    }

}
