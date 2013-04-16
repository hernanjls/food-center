package foodcenter.client.panels.restaurant;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.service.proxies.GeoLocationProxy;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.proxies.RestaurantBranchProxy;
import foodcenter.service.proxies.RestaurantProxy;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class BranchesFlexTable extends FlexTable
{
    
    private final RequestContext requestContext;
    RestaurantProxy restaurnat;
    private final Boolean isAdmin;
    List<RestaurantBranchProxy> branches;
    
    public BranchesFlexTable(RequestContext requestContext, RestaurantProxy restaurnat, Boolean isAdmin)
    {
        super();
        this.requestContext = requestContext;
        this.restaurnat = restaurnat;
        this.isAdmin = isAdmin;
        this.branches = this.restaurnat.getBranches();
        if (null == this.branches)
        {
            this.branches = new LinkedList<RestaurantBranchProxy>();
            this.restaurnat.setBranches(branches);
        }
        redraw();
    }
    
    public final void redraw()
    {
        // Clear all the rows of this table
        removeAllRows();
        
        // Print the header row of this table
        printTableHeader();   
        
        // Print all the branches if exits
        List<RestaurantBranchProxy> branches = restaurnat.getBranches();
        int idx = 0;
        for (RestaurantBranchProxy rbp : branches)
        {
            printRestaurntBranchTableRow(rbp, idx);
            idx++;
        }
    }
    
    /**
     * Prints (or overrides) the 1st row of the table
     * [0] = "categories", [1] = button("add category") 
     */
    private void printTableHeader()
    {
    	// set column 0
        setText(0, 0, "Branch Name");
        
        // set column 1
        setText(0, 1, "latitude");
        setText(0, 2, "longitude");
        
        Button addBranchButton = new Button("Add Branch");
        addBranchButton.addClickHandler(new AddBranchClickHandler());
        addBranchButton.setEnabled(isAdmin);
        
        // set column 3
        setWidget(0, 3, addBranchButton);
    }
    
    /**
     * adds a new blank category
     * the category will be added to the menu proxy, 
     * and to the flex table 
     */
    private void addBranch()
    {
    	
        // create an empty branch
        RestaurantBranchProxy restBranchProxy = requestContext.create(RestaurantBranchProxy.class);
        
        // fill the branch with default values
        restBranchProxy.setRestaurant(restaurnat);
//        restBranchProxy.setMenu(restaurnat.getMenu()); //TODO FIXME clone the menu!!!
        restBranchProxy.setServices(restaurnat.getServices());
        
        // add it to the menu proxy
        int idx = branches.size();
        branches.add(restBranchProxy);
        
        // print its table row
        printRestaurntBranchTableRow(restBranchProxy, idx);
    }
    
    /**
     * Deletes the category from the table and from the menu proxy
     * @param index is item index in the list
     */
    private void deleteBranch(int index)
    {
    	// delete it from the branches list
        branches.remove(index);
    }
    
    /**
     * Adds a new row to the table
     * this row holds the category information
     * 
     * @param restBranchProxy is the category to print as row
     */
    private void printRestaurntBranchTableRow(RestaurantBranchProxy restBranchProxy, int idx)
    {
        int row = getRowCount();
        
        TextBox branchName = new TextBox();
        branchName.addKeyPressHandler(new BranchNameKeyPressHandler(restBranchProxy));
        setWidget(row, 0, branchName);
        
        TextBox branchLat = new TextBox();
        branchLat.addKeyPressHandler(new GeoKeyPressHandler(restBranchProxy, false));
        setWidget(row, 1, branchLat);
        
        TextBox branchLng = new TextBox();
        branchLng.addKeyPressHandler(new GeoKeyPressHandler(restBranchProxy, true));
        setWidget(row, 2, branchLng);
        
        
        Button editBranchButton = new Button("edit");
        editBranchButton.addClickHandler(new EditRestaurantBranchClickHandler(idx));
        editBranchButton.setEnabled(isAdmin);
        setWidget(row, 3, editBranchButton);
        
        
        Button deleteBranchButton = new Button("X");
        deleteBranchButton.addClickHandler(new DeleteRestaurantBranchClickHandler(idx));
        deleteBranchButton.setEnabled(isAdmin);
        setWidget(row, 4, deleteBranchButton);
        
        
        
    }

    /**
     * Handles add category button click 
     */
    private class AddBranchClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            addBranch();
        }
    }
  
    private class EditRestaurantBranchClickHandler implements ClickHandler
    {
        private final int index;
        
        /**
         * @param index - is the index on the list to delete
         */
        public EditRestaurantBranchClickHandler(int index)
        {
            this.index = index;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
          //TODO edit the branch - redirect to edit page
            return;
        }
    }
    
    /**
     * Handles delete category button click
     */
    private class DeleteRestaurantBranchClickHandler implements ClickHandler
    {
        private final int index;
        
        /**
         * @param index - is the index on the list to delete
         */
        public DeleteRestaurantBranchClickHandler(int index)
        {
            this.index = index;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            deleteBranch(index);
            redraw();
        }
    }
  
    /**
     * This class will set the title of the category when key is pressed
     */
    private class BranchNameKeyPressHandler implements KeyPressHandler
    {
        private final RestaurantBranchProxy branch;
        
        /**
         * @param cat is the category to set its title.
         */
        public BranchNameKeyPressHandler(RestaurantBranchProxy branch)
        {
            this.branch = branch;
        }
        
        @Override
        public void onKeyPress(KeyPressEvent event)
        {
            String s = ((TextBox) event.getSource()).getText();
            branch.setName(s);
        }
    }
    
    private class GeoKeyPressHandler implements KeyPressHandler
    {
        private final GeoLocationProxy geoProxy;
        private final int idx;
        /**
         * 
         * @param geoProxy is the restaurant to set
         * @param isLng on true lng, otherwise lat
         */
        public GeoKeyPressHandler(GeoLocationProxy geoProxy, Boolean isLng)
        {
            this.geoProxy = geoProxy;
            idx = isLng ? 1 : 0;
        }
        
        @Override
        public void onKeyPress(KeyPressEvent event)
        {
            List<Double> latLng = geoProxy.getGeoLocation();
            try
            {
                String s = ((TextBox) event.getSource()).getText();
                Double val = Double.parseDouble(s);
                latLng.set(idx, val);
            }
            catch (Throwable e)
            {
                // casting exception
            }
        }
    }
}
