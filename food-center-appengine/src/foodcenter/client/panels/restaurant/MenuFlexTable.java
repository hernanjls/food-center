package foodcenter.client.panels.restaurant;

import java.util.ArrayList;
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

import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class MenuFlexTable extends FlexTable
{
    
    private final RequestContext requestContext;
    private final MenuProxy menuProxy;
    private final Boolean isAdmin;
    
    public MenuFlexTable(RequestContext requestContext, MenuProxy menuProxy, Boolean isAdmin)
    {
        super();
        this.requestContext = requestContext;
        this.menuProxy = menuProxy;
        this.isAdmin = isAdmin;
        
        
        redraw();
    }
    
    public void redraw()
    {
        // Clear all the rows of this table
        removeAllRows();
        
        // Print the header row of this table
        printTableHeader();   
        
        // Print all the categories if exits
        List<MenuCategoryProxy> cats = menuProxy.getCategories();
        if (null != cats)
        {
	        for (MenuCategoryProxy mcp : cats)
	        {
	            printCategoryTableRow(mcp);
	        }
    	}
    }
    
    /**
     * Prints (or overrides) the 1st row of the table
     * [0] = "categories", [1] = button("add category") 
     */
    private void printTableHeader()
    {
    	// set column 0
        setText(0, 0, "Categories");
        
        Button addCatButton = new Button("Add Category");
        addCatButton.addClickHandler(new AddCategoryClickHandler());
        addCatButton.setEnabled(isAdmin);
        
        // set column 1
        setWidget(0, 1, addCatButton);
    }
    
    /**
     * adds a new blank category
     * the category will be added to the menu proxy, 
     * and to the flex table 
     */
    private void addCategory()
    {
        // create a blank category
        MenuCategoryProxy menuCatProxy = RequestUtils.createMenuCategoryProxy(requestContext);
        
        // add it to the menu proxy
        //FIXME null when there were no categories in the 1st place....
        menuProxy.getCategories().add(menuCatProxy);
        
        // print its table row
        printCategoryTableRow(menuCatProxy);
    }
    
    /**
     * Deletes the category from the table and from the menu proxy
     * @param row is the table row of this category
     */
    private void deleteCategory(int row)
    {
    	// delete it from the menu proxy
        List<MenuCategoryProxy> cats = menuProxy.getCategories();
        cats.remove(row - 1);
        redraw();
    }
    
    /**
     * Adds a new row to the table
     * this row holds the category information
     * 
     * @param menuCatProxy is the category to print as row
     */
    private void printCategoryTableRow(MenuCategoryProxy menuCatProxy)
    {
        int row = this.getRowCount();
        
        TextBox catTitle = new TextBox();
        catTitle.setText(menuCatProxy.getCategoryTitle());
        catTitle.addKeyPressHandler(new CategoryTitleKeyPressHandler(menuCatProxy));
        setWidget(row, 0, catTitle);
        
        Button deleteCatButton = new Button("delete");
        deleteCatButton.addClickHandler(new DeleteCategoryClickHandler(row));
        
        deleteCatButton.setEnabled(isAdmin);
        
        setWidget(row, 1, deleteCatButton);
        
        CoursesFlexTable coursesTable = new CoursesFlexTable(requestContext, menuCatProxy, isAdmin);
        setWidget(row, 2, coursesTable);
    }

    /**
     * Handles add category button click 
     */
    private class AddCategoryClickHandler implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            addCategory();
        }
    }
  
    /**
     * Handles delete category button click
     */
    private class DeleteCategoryClickHandler implements ClickHandler
    {
        private final int row;
        
        /**
         * @param row - is the table row to delete on button click
         */
        public DeleteCategoryClickHandler(int row)
        {
            this.row = row;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            deleteCategory(row);
            redraw();
        }
    }
  
    /**
     * This class will set the title of the category when key is pressed
     */
    private class CategoryTitleKeyPressHandler implements KeyPressHandler
    {
        private final MenuCategoryProxy cat;
        
        /**
         * @param cat is the category to set its title.
         */
        public CategoryTitleKeyPressHandler(MenuCategoryProxy cat)
        {
            this.cat = cat;
        }
        
        @Override
        public void onKeyPress(KeyPressEvent event)
        {
            String s = ((TextBox) event.getSource()).getText();
            cat.setCategoryTitle(s);
        }
    }
}
