package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PopupPanel;

import foodcenter.client.handlers.RedrawablePannel;
import foodcenter.client.handlers.RestaurantBranchHandler;
import foodcenter.client.panels.RestaurantBranchPanel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.requset.RestaurantBranchAdminServiceRequest;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class BranchesFlexTable extends FlexTable implements RedrawablePannel
{

    private final static int COLUMN_ADDRESS = 0;
    private final static int COLUMN_BUTTON_ADD_BRANCH = 1;
    private final static int COLUMN_BUTTON_VIEW_BRANCH = 1;
    private final static int COLUMN_BUTTON_DEL_BRANCH = 2;

    private final RestaurantBranchAdminServiceRequest requestContext;
    private final Boolean isEditMode;
    private final List<RestaurantBranchProxy> branches;
    private final List<RestaurantBranchProxy> addedBranches;
    private final RestaurantBranchHandler addBranchHandler;
    private final RestaurantBranchHandler delBranchHandler;

    public BranchesFlexTable(RestaurantBranchAdminServiceRequest requestContext,
                             List<RestaurantBranchProxy> branches,
                             List<RestaurantBranchProxy> addedBranches,
                             Boolean isEditMode,
                             RestaurantBranchHandler addBranchHandler,
                             RestaurantBranchHandler delBranchHandler)
    {
        super();
        this.requestContext = requestContext;
        this.branches = branches;
        this.addedBranches = addedBranches;
        this.isEditMode = isEditMode;
        this.addBranchHandler = addBranchHandler;
        this.delBranchHandler = delBranchHandler;

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
                printRestaurntBranchTableRow(rbp, row);
                row++;
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

        Button viewBranchButton = new Button("View");
        viewBranchButton.addClickHandler(new OnClickViewRestaurantBranch(branch));
        setWidget(row, COLUMN_BUTTON_VIEW_BRANCH, viewBranchButton);

        if (isEditMode)
        {
            viewBranchButton.setText("Edit");

            Button deleteBranchButton = new Button("Delete");
            deleteBranchButton.addClickHandler(new OnClickDeleteRestaurantBranch(branch));
            setWidget(row, COLUMN_BUTTON_DEL_BRANCH, deleteBranchButton);
        }
    }

    /* ********************************************************************* */
    /* ************************* Private Classes *************************** */
    /* ********************************************************************* */

    private abstract class OnClickCommonRestaurantBranch implements ClickHandler
    {
        public void showBranchPannel(RestaurantBranchProxy branch)
        {
            // construct a popup to show the rest branch panel
            PopupPanel popup = new PopupPanel(false); // dont close on outside click

            // construct on close runnable
            AfterCloseEditBranch afterClose = new AfterCloseEditBranch(popup);
            AfterSaveEditBranch afterSave = null;
            
            if (isEditMode)
            {
                afterSave = new AfterSaveEditBranch(branch);
            }
            
            // construct the panel and add it to the popup
            RestaurantBranchPanel branchPanel = new RestaurantBranchPanel(requestContext,
                                                                          branch,
                                                                          isEditMode,
                                                                          afterClose,
                                                                          afterSave);
            popup.add(branchPanel);
            popup.setTitle("Edit Branch");
            popup.setPopupPosition(10, 80);

            // show the new popup content
            popup.show();

        }
    }
    /**
     * Handles add category button click
     */
    private class OnClickAddRestaurantBranch extends OnClickCommonRestaurantBranch
    {
        @Override
        public void onClick(ClickEvent event)
        {

            // construct a new branch, it will be edited by the rest branch panel
            RestaurantBranchProxy branch = RequestUtils.createRestaurantBranchProxy(requestContext);
            showBranchPannel(branch);
        }
    }

    private class OnClickViewRestaurantBranch extends OnClickCommonRestaurantBranch
    {
        private final RestaurantBranchProxy branch;

        public OnClickViewRestaurantBranch(RestaurantBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            showBranchPannel(branch);
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
            if (null != delBranchHandler)
            {
                delBranchHandler.handle(branch, BranchesFlexTable.this);
            }
        }
    }

    private class AfterSaveEditBranch implements Runnable
    {
        private final RestaurantBranchProxy branch;

        public AfterSaveEditBranch(RestaurantBranchProxy branch)
        {
            this.branch = branch;
        }

        @Override
        public void run()
        {
            // redraw (also deals with new branches)!
            if (null != addBranchHandler)
            {
                addBranchHandler.handle(branch, BranchesFlexTable.this);
            }
        }
    }

    private class AfterCloseEditBranch implements Runnable
    {
        private final PopupPanel popup;

        public AfterCloseEditBranch(PopupPanel popup)
        {
            this.popup = popup;
        }

        @Override
        public void run()
        {
            popup.hide();
        }
    }
}
