package foodcenter.client.panels.restaurant.internal;

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
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class MenuPanel extends FlexTable
{

    private static final int COLUMN_CATEGORIES = 0;
    private static final int COLUMN_CATEGORIES_ADD_BUTTON = 1;
    private static final int COLUMN_CATEGORIES_DEL_BUTTON = 1;
    private static final int COLUMN_CATEGORY_COURSES = 2;

    private final RequestContext requestContext;
    private final MenuProxy menuProxy;
    private final Boolean isEditMode;

    public MenuPanel(MenuProxy menuProxy)
    {
        this(menuProxy, null);
    }

    public MenuPanel(MenuProxy menuProxy, RequestContext requestContext)
    {
        super();
        this.requestContext = requestContext;
        this.menuProxy = menuProxy;
        this.isEditMode = (requestContext != null);

        // Draw the Panel's data
        redraw();
    }

    public void redraw()
    {
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

        int row = getRowCount();
        for (MenuCategoryProxy mcp : cats)
        {
            printCategoryTableRow(mcp, row);
            ++row;
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
            Button addButton = new Button("Add Category", new OnClickAddCategory());
            setWidget(0, COLUMN_CATEGORIES_ADD_BUTTON, addButton);
        }
    }

    /**
     * adds a new blank category
     * the category will be added to the menu proxy,
     * and to the flex table
     */
    private void addCategory(int row)
    {
        // create a blank category
        MenuCategoryProxy menuCatProxy = RequestUtils.createMenuCategoryProxy(requestContext);

        // add it to the menu proxy
        menuProxy.getCategories().add(menuCatProxy);

        // print its table row
        printCategoryTableRow(menuCatProxy, row);
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
    private void printCategoryTableRow(MenuCategoryProxy menuCatProxy, int row)
    {
        TextBox catTitle = new TextBox();
        catTitle.setText(menuCatProxy.getCategoryTitle());
        catTitle.setEnabled(isEditMode);
        setWidget(row, COLUMN_CATEGORIES, catTitle);

        if (isEditMode)
        {
            catTitle.addKeyPressHandler(new CategoryTitleKeyPressHandler(menuCatProxy));

            Button delButton = new Button("Delete", new OnClickDeleteCategory(row));
            setWidget(row, COLUMN_CATEGORIES_DEL_BUTTON, delButton);
        }

        MenuCoursesPanel coursesTable = new MenuCoursesPanel(menuCatProxy, requestContext);
        setWidget(row, COLUMN_CATEGORY_COURSES, coursesTable);
    }

    /**
     * Handles add category button click
     */
    private class OnClickAddCategory implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            addCategory(getRowCount());
        }
    }

    /**
     * Handles delete category button click
     */
    private class OnClickDeleteCategory implements ClickHandler
    {
        private final int row;

        /**
         * @param row - is the table row to delete on button click
         */
        public OnClickDeleteCategory(int row)
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
