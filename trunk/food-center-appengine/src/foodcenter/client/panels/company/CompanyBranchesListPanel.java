package foodcenter.client.panels.company;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.service.proxies.CompanyBranchProxy;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.requset.CompanyBranchAdminServiceRequest;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class CompanyBranchesListPanel extends FlexTable implements RedrawablePanel
{

    private final static int COLUMN_ADDRESS = 0;
    private final static int COLUMN_BUTTON_ADD_BRANCH = 1;
    private final static int COLUMN_BUTTON_VIEW_BRANCH = 1;
    private final static int COLUMN_BUTTON_EDIT_BRANCH = 1;
    private final static int COLUMN_BUTTON_DEL_BRANCH = 2;

    // CTOR variables
    private final List<CompanyBranchProxy> branches;
    private final List<CompanyBranchProxy> addedBranches;
    private final PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback;
    private final boolean isEditMode;
    private final boolean isRestAdmin;

    // Local variables
    private final RestBranchCallback branchCallback; // callback to pass to restaurants

    public CompanyBranchesListPanel(List<CompanyBranchProxy> branches,
                                       List<CompanyBranchProxy> addedBranches,
                                       PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
    {
        this(branches, addedBranches, callback, false, false);
    }

    public CompanyBranchesListPanel(List<CompanyBranchProxy> branches,
                                       List<CompanyBranchProxy> addedBranches,
                                       PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback,
                                       boolean isEditMode,
                                       boolean isRestAdmin)
    {
        super();

        this.branches = branches;
        this.addedBranches = addedBranches;
        this.callback = callback;
        this.isEditMode = isEditMode;
        this.isRestAdmin = isRestAdmin;

        this.branchCallback = new RestBranchCallback();
        redraw();
    }

    @Override
    public final void redraw()
    {
        // Clear all the rows of this table
        removeAllRows();

        // Print the header row of this table
        printTableHeader();

        // Print all the branches if exits
        int row = getRowCount();
        if (null != branches)
        {
            for (CompanyBranchProxy rbp : branches)
            {
                printRestaurntBranchTableRow(rbp, row);
                row++;
            }
        }
        if (null != addedBranches)
        {
            for (CompanyBranchProxy rbp : addedBranches)
            {
                printRestaurntBranchTableRow(rbp, row);
                row++;
            }
        }
    }

    @Override
    public void close()
    {
        Window.alert("Bug! close is not supported...");
    }

    /**
     * Prints (or overrides) the 1st row of the table
     */
    private void printTableHeader()
    {
        // set column 0
        setText(0, COLUMN_ADDRESS, "Address");

        if (isEditMode)
        {
            Button addBranchButton = new Button("Add Branch");
            addBranchButton.addClickHandler(new OnClickAddCompanyBranch());
            setWidget(0, COLUMN_BUTTON_ADD_BRANCH, addBranchButton);
        }
    }

    /**
     * print the branch to the table row
     * 
     * @param row is the row to set
     * @param branch is the category to print as row
     */
    private void printRestaurntBranchTableRow(CompanyBranchProxy branch, int row)
    {
        setText(row, COLUMN_ADDRESS, branch.getAddress());

        Button view = new Button("View");
        view.addClickHandler(new OnClickViewCompanyBranch(branch));
        setWidget(row, COLUMN_BUTTON_VIEW_BRANCH, view);

        if (isEditMode)
        {
            Button edit = new Button("Edit");
            edit.addClickHandler(new OnClickEditCompanyBranch(branch));
            setWidget(row, COLUMN_BUTTON_EDIT_BRANCH, edit);

            if (isRestAdmin)
            {
                Button deleteBranchButton = new Button("Delete");
                deleteBranchButton.addClickHandler(new OnClickDeleteCompanyBranch(branch));
                setWidget(row, COLUMN_BUTTON_DEL_BRANCH, deleteBranchButton);
            }
        }
        else
        {
            if (branch.isEditable())
            {
                Button edit = new Button("Edit");
                edit.addClickHandler(new OnClickEditCompanyBranch(branch));
                setWidget(row, COLUMN_BUTTON_EDIT_BRANCH + 1, edit);
            }
        }

    }

    /* ********************************************************************* */
    /* ************************* Private Classes *************************** */
    /* ********************************************************************* */

    private class RestBranchCallback
                                    implements
                                    PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest>
    {

        @Override
        public void close(RedrawablePanel panel, CompanyBranchProxy proxy)
        {
            if (null != panel)
            {
                panel.close();
            }
            // should redraw this panel :)
            callback.close(CompanyBranchesListPanel.this, proxy);
        }

        @Override
        public void
            save(RedrawablePanel panel,
                 CompanyBranchProxy proxy,
                 PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback,
                 CompanyBranchAdminServiceRequest service)
        {
            // callback === this
            close(panel, proxy);
            CompanyBranchesListPanel.this.callback.save(CompanyBranchesListPanel.this,
                                                           proxy,
                                                           callback,
                                                           service);
        }

        @Override
        public void
            view(RedrawablePanel panel,
                 CompanyBranchProxy proxy,
                 PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            // callback === this

            close(panel, proxy);
            CompanyBranchesListPanel.this.callback.view(CompanyBranchesListPanel.this,
                                                           proxy,
                                                           callback);
        }

        @Override
        public void
            edit(RedrawablePanel panel, // branch panel
                 CompanyBranchProxy proxy,
                 PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            // callback === this

            close(panel, proxy);
            CompanyBranchesListPanel.this.callback.edit(CompanyBranchesListPanel.this,
                                                           proxy,
                                                           callback);
        }

        @Override
        public void
            createNew(RedrawablePanel panel,
                      PanelCallback<CompanyBranchProxy, CompanyBranchAdminServiceRequest> callback)
        {
            // callback === this

            CompanyBranchesListPanel.this.callback.createNew(CompanyBranchesListPanel.this,
                                                                callback);
        }

        @Override
        public void del(RedrawablePanel panel, CompanyBranchProxy proxy)
        {
            close(panel, proxy); // Close the edit panel
            CompanyBranchesListPanel.this.callback.del(CompanyBranchesListPanel.this, proxy);
        }

        @Override
        public void error(RedrawablePanel panel, CompanyBranchProxy proxy, String reason)
        {
            CompanyBranchesListPanel.this.callback.error(panel, proxy, reason);
        }
    }

    /**
     * Handles add category button click
     */
    private class OnClickAddCompanyBranch implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != callback)
            {
                callback.createNew(CompanyBranchesListPanel.this, branchCallback);
            }
        }
    }

    private class OnClickViewCompanyBranch implements ClickHandler
    {
        private final CompanyBranchProxy branch;

        public OnClickViewCompanyBranch(CompanyBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.view(CompanyBranchesListPanel.this, branch, branchCallback);
        }
    }

    private class OnClickEditCompanyBranch implements ClickHandler
    {
        private CompanyBranchProxy branch;

        public OnClickEditCompanyBranch(CompanyBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.edit(null, branch, branchCallback);
        }
    }

    /**
     * Handles delete Branch button
     */
    private class OnClickDeleteCompanyBranch implements ClickHandler
    {
        private final CompanyBranchProxy branch;

        /**
         * @param index - is the index on the list to delete
         */
        public OnClickDeleteCompanyBranch(CompanyBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            // Removes the branch and calls redraw
            if (null != callback)
            {
                callback.del(CompanyBranchesListPanel.this, branch);
            }
        }
    }

}
