package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class RestaurantBranchesListPanel extends FlexTable implements RedrawablePanel
{

    private final static int COLUMN_ADDRESS = 0;
    private final static int COLUMN_BUTTON_ADD_BRANCH = 1;
    private final static int COLUMN_BUTTON_VIEW_BRANCH = 1;
    private final static int COLUMN_BUTTON_EDIT_BRANCH = 1;
    private final static int COLUMN_BUTTON_DEL_BRANCH = 2;

    // CTOR variables
    private final List<RestaurantBranchProxy> branches;
    private final List<RestaurantBranchProxy> addedBranches;
    private final List<RestaurantBranchProxy> deletedBranches;
    private final PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback;
    private final boolean isEditMode;
    private final boolean isRestAdmin;

    // Local variables
    private final RestBranchCallback branchCallback; // callback to pass to restaurants

    public RestaurantBranchesListPanel(List<RestaurantBranchProxy> branches,
                                       List<RestaurantBranchProxy> addedBranches,
                                       List<RestaurantBranchProxy> deletedBranches,
                                       PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
    {
        this(branches, addedBranches, deletedBranches, callback, false, false);
    }

    public RestaurantBranchesListPanel(List<RestaurantBranchProxy> branches,
                                       List<RestaurantBranchProxy> addedBranches,
                                       List<RestaurantBranchProxy> deletedBranches,
                                       PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback,
                                       boolean isEditMode,
                                       boolean isRestAdmin)
    {
        super();

        this.branches = branches;
        this.addedBranches = addedBranches;
        this.deletedBranches = deletedBranches;
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
            for (RestaurantBranchProxy rbp : branches)
            {
                if (!deletedBranches.contains(rbp))
                {
                    printRestaurntBranchTableRow(rbp, row);
                    row++;
                }
            }
        }
        if (null != addedBranches)
        {
            for (RestaurantBranchProxy rbp : addedBranches)
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
            addBranchButton.addClickHandler(new OnClickAddRestaurantBranch());
            setWidget(0, COLUMN_BUTTON_ADD_BRANCH, addBranchButton);
        }
    }

    /**
     * print the branch to the table row
     * 
     * @param row is the row to set
     * @param branch is the category to print as row
     */
    private void printRestaurntBranchTableRow(RestaurantBranchProxy branch, int row)
    {
        setText(row, COLUMN_ADDRESS, branch.getAddress());

        Button view = new Button("View");
        view.addClickHandler(new OnClickViewRestaurantBranch(branch));
        setWidget(row, COLUMN_BUTTON_VIEW_BRANCH, view);

        if (isEditMode)
        {
            Button edit = new Button("Edit");
            edit.addClickHandler(new OnClickEditRestaurantBranch(branch));
            setWidget(row, COLUMN_BUTTON_EDIT_BRANCH, edit);

            if (isRestAdmin)
            {
                Button deleteBranchButton = new Button("Delete");
                deleteBranchButton.addClickHandler(new OnClickDeleteRestaurantBranch(branch));
                setWidget(row, COLUMN_BUTTON_DEL_BRANCH, deleteBranchButton);
            }
        }
        else
        {
            if (branch.isEditable())
            {
                Button edit = new Button("Edit");
                edit.addClickHandler(new OnClickEditRestaurantBranch(branch));
                setWidget(row, COLUMN_BUTTON_EDIT_BRANCH + 1, edit);
            }
        }

    }

    /* ********************************************************************* */
    /* ************************* Private Classes *************************** */
    /* ********************************************************************* */

    private class RestBranchCallback
                                    implements
                                    PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest>
    {

        @Override
        public void close(RedrawablePanel panel, RestaurantBranchProxy proxy)
        {
            if (null != panel)
            {
                panel.close();
            }
            // should redraw this panel :)
            callback.close(RestaurantBranchesListPanel.this, proxy);
        }

        @Override
        public void
            save(RedrawablePanel panel,
                 RestaurantBranchProxy proxy,
                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback,
                 RestaurantBranchAdminServiceRequest service)
        {
            // callback === this
            close(panel, proxy);
            RestaurantBranchesListPanel.this.callback.save(RestaurantBranchesListPanel.this,
                                                           proxy,
                                                           callback,
                                                           service);
        }

        @Override
        public void
            view(RedrawablePanel panel,
                 RestaurantBranchProxy proxy,
                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            // callback === this

            close(panel, proxy);
            RestaurantBranchesListPanel.this.callback.view(RestaurantBranchesListPanel.this,
                                                           proxy,
                                                           callback);
        }

        @Override
        public void
            edit(RedrawablePanel panel, // branch panel
                 RestaurantBranchProxy proxy,
                 PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            // callback === this

            close(panel, proxy);
            RestaurantBranchesListPanel.this.callback.edit(RestaurantBranchesListPanel.this,
                                                           proxy,
                                                           callback);
        }

        @Override
        public void
            createNew(RedrawablePanel panel,
                      PanelCallback<RestaurantBranchProxy, RestaurantBranchAdminServiceRequest> callback)
        {
            // callback === this

            RestaurantBranchesListPanel.this.callback.createNew(RestaurantBranchesListPanel.this,
                                                                callback);
        }

        @Override
        public void del(RedrawablePanel panel, RestaurantBranchProxy proxy)
        {
            close(panel, proxy); // Close the edit panel
            RestaurantBranchesListPanel.this.callback.del(RestaurantBranchesListPanel.this, proxy);
        }

        @Override
        public void error(RedrawablePanel panel, RestaurantBranchProxy proxy, String reason)
        {
            RestaurantBranchesListPanel.this.callback.error(panel, proxy, reason);
        }
    }

    /**
     * Handles add category button click
     */
    private class OnClickAddRestaurantBranch implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            if (null != callback)
            {
                callback.createNew(RestaurantBranchesListPanel.this, branchCallback);
            }
        }
    }

    private class OnClickViewRestaurantBranch implements ClickHandler
    {
        private final RestaurantBranchProxy branch;

        public OnClickViewRestaurantBranch(RestaurantBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            callback.view(RestaurantBranchesListPanel.this, branch, branchCallback);
        }
    }

    private class OnClickEditRestaurantBranch implements ClickHandler
    {
        private RestaurantBranchProxy branch;

        public OnClickEditRestaurantBranch(RestaurantBranchProxy branch)
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
    private class OnClickDeleteRestaurantBranch implements ClickHandler
    {
        private final RestaurantBranchProxy branch;

        /**
         * @param index - is the index on the list to delete
         */
        public OnClickDeleteRestaurantBranch(RestaurantBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            // Removes the branch and calls redraw
            if (null != callback)
            {
                callback.del(RestaurantBranchesListPanel.this, branch);
            }
        }
    }

}
