package foodcenter.client.panels.main;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import foodcenter.client.callbacks.OnClickServiceCheckBox;
import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.callbacks.SearchPanelCallback;
import foodcenter.client.callbacks.search.CompanySearchOptions;
import foodcenter.client.panels.common.EditableImage;
import foodcenter.service.enums.ServiceType;
import foodcenter.service.proxies.CompanyProxy;
import foodcenter.service.requset.CompanyAdminServiceRequest;

public class CompaniesListPanel extends VerticalPanel implements RedrawablePanel
{
    private final static int COLUMN_IMAGE = 0;
    private final static int COLUMN_NAME = 1;
    private final static int COLUMN_DELIVERY = 2;
    private final static int COLUMN_TAKEAWAY = 3;
    private final static int COLUMN_TABLE = 4;
    private final static int COLUMN_NEW_BUTTON = 5;
    private final static int COLUMN_VIEW_BUTTON = 5;
    private final static int COLUMN_EDIT_BUTTON = 6;
    private final static int COLUMN_DELETE_BUTTON = 7;

    private final List<CompanyProxy> comps;
    private final PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback;
    private final SearchPanelCallback<CompanySearchOptions> searchCallback;
    private final boolean isAdmin;

    private final CompanySearchOptions searchOptions;
    private final RestCallback compCallback;
    private final Panel optionsPanel;
    private final FlexTable compsTable;

    public CompaniesListPanel(List<CompanyProxy> comps,
                                PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback,
                                SearchPanelCallback<CompanySearchOptions> searchCallback)
    {
        this(comps, callback, searchCallback, false);
    }

    public CompaniesListPanel(List<CompanyProxy> comps,
                                PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback,
                                SearchPanelCallback<CompanySearchOptions> searchCallback,
                                boolean isAdmin)
    {
        super();

        this.comps = comps;
        this.callback = callback;
        this.searchCallback = searchCallback;
        this.isAdmin = isAdmin;

        searchOptions = new CompanySearchOptions();
        compCallback = new RestCallback();
        optionsPanel = createOptionsPannel();
        add(optionsPanel);

        compsTable = new FlexTable();
        add(compsTable);

        redraw();
    }

    @Override
    public void redraw()
    {
        // Clear all the rows of this table
        compsTable.removeAllRows();

        // Print the header of this table
        printCompanysTableHeader();

        // Print all the branches if exits
        int row = compsTable.getRowCount();
        if (null != comps)
        {
            for (CompanyProxy rp : comps)
            {
                printCompanyTableRow(rp, row);
                row++;
            }
        }
    }

    @Override
    public void close()
    {
        // This is not supported by this internal panel
        callback.error(this, null, "Not Supported!");
    }

    private void printCompanyTableRow(CompanyProxy comp, int row)
    {
        EditableImage img = new EditableImage(comp.getImageUrl());
        compsTable.setWidget(row, COLUMN_IMAGE, img);

        String name = comp.getName();
        compsTable.setText(row, COLUMN_NAME, name);

        String delivery = comp.getServices().contains(ServiceType.DELIVERY) ? "yes" : "no";
        compsTable.setText(row, COLUMN_DELIVERY, delivery);

        String takeAway = comp.getServices().contains(ServiceType.TAKE_AWAY) ? "yes" : "no";
        compsTable.setText(row, COLUMN_TAKEAWAY, takeAway);

        String table = comp.getServices().contains(ServiceType.TABLE) ? "yes" : "no";
        compsTable.setText(row, COLUMN_TABLE, table);

        Button view = new Button("View");
        view.addClickHandler(new OnClickViewCompany(comp));
        compsTable.setWidget(row, COLUMN_VIEW_BUTTON, view);

        if (comp.isEditable())
        {
            Button edit = new Button("Edit");
            edit.addClickHandler(new OnClickEditCompany(comp));
            compsTable.setWidget(row, COLUMN_EDIT_BUTTON, edit);

            if (isAdmin)
            {
                Button delete = new Button("Delete");
                delete.addClickHandler(new OnClickDeleteCompany(comp));
                compsTable.setWidget(row, COLUMN_DELETE_BUTTON, delete);
            }
        }
    }

    private void printCompanysTableHeader()
    {
        compsTable.setText(0, COLUMN_IMAGE, "Image");
        compsTable.setText(0, COLUMN_NAME, "Name");
        compsTable.setText(0, COLUMN_DELIVERY, "Delivery");
        compsTable.setText(0, COLUMN_TAKEAWAY, "Take Away");
        compsTable.setText(0, COLUMN_TABLE, "Table");

        if (isAdmin)
        {
            Button newButton = new Button("New");
            newButton.addClickHandler(new OnClickNewCompany());
            compsTable.setWidget(0, COLUMN_NEW_BUTTON, newButton);
        }

    }

    private Panel createOptionsPannel()
    {
        HorizontalPanel result = new HorizontalPanel();

        TextBox searchBox = new TextBox();
        searchBox.addKeyUpHandler(new SearchKeyUpHandler());
        result.add(searchBox);

        CheckBox delivery = createServiceCheckBox(ServiceType.DELIVERY);
        result.add(delivery);

        CheckBox takeAway = createServiceCheckBox(ServiceType.TAKE_AWAY);
        result.add(takeAway);

        CheckBox table = createServiceCheckBox(ServiceType.TABLE);
        result.add(table);

        Button searchButton = new Button("Search");
        searchButton.addClickHandler(new OnClickSearchRests());
        result.add(searchButton);

        return result;
    }

    private CheckBox createServiceCheckBox(ServiceType service)
    {
        CheckBox res = new CheckBox(service.getName());
        res.setValue(true);
        res.addClickHandler(new OnClickServiceCheckBox(searchOptions.getServices()));
        
        return res;
    }

    /* **************************************************************** */
    /* **************** private classes ********************* */

    private class RestCallback implements
                              PanelCallback<CompanyProxy, CompanyAdminServiceRequest>
    {

        @Override
        public void close(RedrawablePanel panel, CompanyProxy proxy)
        {
            // super gets this panel, and not the requested panel
            if (null != panel)
            {
                panel.close();
            }
            CompaniesListPanel.this.callback.close(CompaniesListPanel.this, proxy);
        }

        @Override
        public void save(RedrawablePanel panel,
                         CompanyProxy proxy,
                         PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback,
                         CompanyAdminServiceRequest service)
        {
            close(panel, proxy);
            CompaniesListPanel.this.callback.save(CompaniesListPanel.this,
                                                    proxy,
                                                    callback,
                                                    service);
        }

        @Override
        public void view(RedrawablePanel panel,
                         CompanyProxy proxy,
                         PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback)
        {
            close(panel, proxy);
            CompaniesListPanel.this.callback.view(CompaniesListPanel.this, proxy, callback);
        }

        @Override
        public void edit(RedrawablePanel panel,
                         CompanyProxy proxy,
                         PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback)
        {
            close(panel, proxy);
            CompaniesListPanel.this.callback.edit(CompaniesListPanel.this, proxy, callback);
        }

        @Override
        public void
            createNew(RedrawablePanel panel,
                      PanelCallback<CompanyProxy, CompanyAdminServiceRequest> callback)
        {
            CompaniesListPanel.this.callback.createNew(CompaniesListPanel.this, callback);
        }

        @Override
        public void del(RedrawablePanel panel, CompanyProxy proxy)
        {
            close(panel, proxy); // Close the edit panel
            CompaniesListPanel.this.callback.del(CompaniesListPanel.this, proxy);
        }

        @Override
        public void error(RedrawablePanel panel, CompanyProxy proxy, String reason)
        {
            callback.error(panel, proxy, reason);
        }

    }

    /* ************************************************************************** */

    private class OnClickViewCompany implements ClickHandler
    {
        private final CompanyProxy rest;

        public OnClickViewCompany(CompanyProxy rest)
        {
            super();

            this.rest = rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.view(CompaniesListPanel.this, rest, compCallback);
        }
    }

    /* ************************************************************************** */

    private class OnClickEditCompany implements ClickHandler
    {
        private final CompanyProxy rest;

        public OnClickEditCompany(CompanyProxy rest)
        {
            super();
            this.rest = rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(CompaniesListPanel.this, rest, compCallback);
        }
    }

    /* ************************************************************************** */

    private class OnClickDeleteCompany implements ClickHandler
    {
        private final CompanyProxy rest;

        public OnClickDeleteCompany(CompanyProxy rest)
        {
            this.rest = rest;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.del(CompaniesListPanel.this, rest);
        }
    }

    /* ************************************************************************** */

    private class OnClickNewCompany implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            callback.createNew(CompaniesListPanel.this, compCallback);
        }
    }

    /* ************************************************************************** */

    private class OnClickSearchRests implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            searchCallback.search(searchOptions);
        }
    }

    /* ************************************************************************** */

    private class SearchKeyUpHandler implements KeyUpHandler
    {
        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
            {
                searchCallback.search(searchOptions);
            }
            TextBox tb = (TextBox)event.getSource();
            searchOptions.setPattern(tb.getText());
            
        }

    }

}
