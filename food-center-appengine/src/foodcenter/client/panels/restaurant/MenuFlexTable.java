package foodcenter.client.panels.restaurant;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.requestfactory.shared.RequestContext;

import foodcenter.client.handlers.RedrawablePannel;
import foodcenter.client.service.RequestUtils;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class MenuFlexTable extends FlexTable implements RedrawablePannel
{

    private static final int COLUMN_CATEGORIES = 0;
    private static final int COLUMN_CATEGORIES_ADD_BUTTON = 1;
    private static final int COLUMN_CATEGORIES_DEL_BUTTON = 1;
    private static final int COLUMN_CATEGORY_COURSES = 2;

    private final RequestContext requestContext;
    private final MenuProxy menuProxy;
    private final Boolean isEditMode;

    public MenuFlexTable(RequestContext requestContext, MenuProxy menuProxy, Boolean isEditMode)
    {
        super();
        this.requestContext = requestContext;
        this.menuProxy = menuProxy;
        this.isEditMode = isEditMode;

        redraw();
    }

    @Override
    public void redraw()
    {
        // Clear all the rows of this table
        removeAllRows();

        // Print the header row of this table
        printTableHeader();

        // Print all the categories if exits
        if (null == menuProxy)
        {
            return;
        }

        List<MenuCategoryProxy> cats = menuProxy.getCategories();
        if (null == cats)
        {
            return;
        }

        for (MenuCategoryProxy mcp : cats)
        {
            printCategoryTableRow(mcp);
        }

    }

    /**
     * Prints (or overrides) the 1st row of the table
     */
    private void printTableHeader()
    {
        setText(0, COLUMN_CATEGORIES, "Categories");
        setText(0, COLUMN_CATEGORY_COURSES, "Courses");
        
        if (isEditMode)
        {
            Button addCatButton = new Button("Add Category");
            addCatButton.addClickHandler(new AddCategoryClickHandler());
            addCatButton.setEnabled(isEditMode);
            setWidget(0, COLUMN_CATEGORIES_ADD_BUTTON, addCatButton);
        }
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
        // FIXME null when there were no categories in the 1st place....
        menuProxy.getCategories().add(menuCatProxy);
        // if (null != rest)
        // {
        // rest.setMenu(menuProxy);
        // }
        // if (null != branch)
        // {
        // branch.setMenu(menuProxy);
        // }

        // print its table row
        printCategoryTableRow(menuCatProxy);
    }

    /**
     * Deletes the category from the table and from the menu proxy
     * 
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
        catTitle.setEnabled(isEditMode);
        
        setWidget(row, COLUMN_CATEGORIES, catTitle);

        if (isEditMode)
        {
            catTitle.addKeyPressHandler(new CategoryTitleKeyPressHandler(menuCatProxy));
            
            Button deleteCatButton = new Button("Delete");
            deleteCatButton.addClickHandler(new DeleteCategoryClickHandler(row));
            setWidget(row, COLUMN_CATEGORIES_DEL_BUTTON, deleteCatButton);
        }
        

        CoursesFlexTable coursesTable = new CoursesFlexTable(requestContext,
                                                             menuCatProxy,
                                                             isEditMode);
        
        setWidget(row, COLUMN_CATEGORY_COURSES, coursesTable);
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
