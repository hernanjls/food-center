package foodcenter.client.panels.restaurant.menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import foodcenter.client.callbacks.PanelCallback;
import foodcenter.client.callbacks.RedrawablePanel;
import foodcenter.client.service.WebRequestUtils;
import foodcenter.service.proxies.CourseProxy;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;
import foodcenter.service.requset.MenuAdminServiceRequest;

/**
 * Panel which represents a {@link MenuProxy}
 */
public class MenuPanel extends FlexTable implements RedrawablePanel
{

    private static final int COLUMN_CATEGORIES = 0;
    private static final int COLUMN_CATEGORIES_ADD_BUTTON = 1;
    private static final int COLUMN_CATEGORIES_DEL_BUTTON = 1;
    private static final int COLUMN_CATEGORY_COURSES = 2;

    private final MenuProxy menu;
    // private final PanelCallback<MenuProxy, MenuAdminServiceRequest> callback;
    private final MenuAdminServiceRequest service;

    private final Boolean isEditMode;
    
    // new courses for existing category
    private final Map<MenuCategoryProxy, List<CourseProxy>> addedCourses;

    // deleted courses for existing category
    private final Map<MenuCategoryProxy, List<CourseProxy>> deletedCourses;

    // new categories for menu
    private final List<MenuCategoryProxy> addedCats;
    // deleted categories for menu
    private final List<MenuCategoryProxy> deletedCats;

    // mapping from courses table to category
    private final Map<RedrawablePanel, MenuCategoryProxy> panelCategory;

    private final CourseCallback coursesListCallback;

    public MenuPanel(MenuProxy menu, MenuAdminServiceRequest service)
    {
        super();

        this.menu = menu;
        this.service = service;
        
        this.isEditMode = (null != service);

        addedCats = new LinkedList<MenuCategoryProxy>();
        deletedCats = new LinkedList<MenuCategoryProxy>();
        addedCourses = new HashMap<MenuCategoryProxy, List<CourseProxy>>();
        deletedCourses = new HashMap<MenuCategoryProxy, List<CourseProxy>>();

        for (MenuCategoryProxy mcp : menu.getCategories())
        {
            addedCourses.put(mcp, new LinkedList<CourseProxy>());
            deletedCourses.put(mcp, new LinkedList<CourseProxy>());
        }

        panelCategory = new HashMap<RedrawablePanel, MenuCategoryProxy>();
        coursesListCallback = new CourseCallback();

        // Draw the Panel's data
        redraw();
    }

    @Override
    public void redraw()
    {
        removeAllRows();
        panelCategory.clear();

        // Print the header row of this table
        printTableHeader();

        int row = getRowCount();

        for (MenuCategoryProxy mcp : menu.getCategories())
        {
            if (!deletedCats.contains(mcp))
            {
                printCategoryTableRow(mcp, row);
                ++row;
            }
        }

        for (MenuCategoryProxy mcp : addedCats)
        {
            if (!deletedCats.contains(mcp))
            {
                printCategoryTableRow(mcp, row);
                ++row;
            }
        }
    }

    @Override
    public void close()
    {
        // this is internal list, should do nothing!
        coursesListCallback.error(this, null, "MenuPanel close was called!!");
    }

    public void setToService()
    {
        for (MenuCategoryProxy cat : addedCats)
        {
            service.addMenuCategory(menu, cat);
        }
        for (MenuCategoryProxy cat: deletedCats)
        {
            service.removeMenuCategory(menu, cat);
        }
        
        for (MenuCategoryProxy cat : addedCourses.keySet())
        {
            List<CourseProxy> courses = addedCourses.get(cat);
            for (CourseProxy course : courses)
            {
                service.addCategoryCourse(cat, course);
            }
        }
        for (MenuCategoryProxy cat : deletedCourses.keySet())
        {
            List<CourseProxy> courses = deletedCourses.get(cat);
            for (CourseProxy course : courses)
            {
                service.removeCategoryCourse(cat, course);
            }
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
            Button addButton = new Button("+Cat", new OnClickAddCategory());
            setWidget(0, COLUMN_CATEGORIES_ADD_BUTTON, addButton);
        }
    }

    /**
     * Adds a new row to the table
     * this row holds the category information
     * 
     * @param menuCatProxy is the category to print as row
     */
    private void printCategoryTableRow(MenuCategoryProxy cat, int row)
    {
        TextBox catTitle = new TextBox();
        catTitle.setText(cat.getCategoryTitle());
        catTitle.setEnabled(isEditMode);
        setWidget(row, COLUMN_CATEGORIES, catTitle);

        if (isEditMode)
        {
            catTitle.addKeyUpHandler(new CategoryTitleKeyUpHandler(cat));

            Button delButton = new Button("-", new OnClickDeleteCategory(cat));
            setWidget(row, COLUMN_CATEGORIES_DEL_BUTTON, delButton);
        }

        MenuCoursesListPanel coursesTable = new MenuCoursesListPanel(cat.getCourses(),
                                                                     addedCourses.get(cat),
                                                                     deletedCourses.get(cat),
                                                                     coursesListCallback,
                                                                     isEditMode);

        // Save the mapping for the callbacks
        panelCategory.put(coursesTable, cat);

        setWidget(row, COLUMN_CATEGORY_COURSES, coursesTable);
    }

    /* ******************************************************************************************* */
    private class CourseCallback implements PanelCallback<CourseProxy, MenuAdminServiceRequest>
    {

        @Override
        public void close(RedrawablePanel coursesPanel, CourseProxy proxy)
        {
            error(coursesPanel, proxy, "Close Courses is not supported");
        }

        @Override
        public void save(RedrawablePanel coursesPanel,
                         CourseProxy proxy,
                         PanelCallback<CourseProxy, MenuAdminServiceRequest> callback,
                         MenuAdminServiceRequest service)
        {
            error(coursesPanel, proxy, "Save Course is not supported");
        }

        @Override
        public void view(RedrawablePanel coursesPanel,
                         CourseProxy proxy,
                         PanelCallback<CourseProxy, MenuAdminServiceRequest> callback)
        {
            error(coursesPanel, proxy, "View Course is not supported");

        }

        @Override
        public void edit(RedrawablePanel coursesPanel,
                         CourseProxy proxy,
                         PanelCallback<CourseProxy, MenuAdminServiceRequest> callback)
        {
            error(coursesPanel, proxy, "Edit Course is not supported");
        }

        @Override
        public void createNew(RedrawablePanel coursesPanel,
                              PanelCallback<CourseProxy, MenuAdminServiceRequest> callback)
        {
            CourseProxy course = WebRequestUtils.createCourseProxy(service);
            MenuCategoryProxy cat = panelCategory.get(coursesPanel);

            if (addedCats.contains(cat))
            {
                // for new cat there is no need to add it to the added courses list
                cat.getCourses().add(course);
            }
            else
            {
                addedCourses.get(cat).add(course);
            }
            
            coursesPanel.redraw();
        }

        @Override
        public void del(RedrawablePanel coursesPanel, CourseProxy course)
        {
            // for each of the existing categories
            for (MenuCategoryProxy cat : menu.getCategories())
            {
                
                if (cat.getCourses().contains(course))
                {
                    // if the course is already exists in the category
                    deletedCourses.get(cat).add(course);
                    coursesPanel.redraw();
                    return;
                }
                else if (addedCourses.get(cat).contains(course))
                {
                    //if the course is a new one added to an existing category
                    addedCourses.get(cat).remove(course);
                    coursesPanel.redraw();
                    return;
                }
            }

            // do for every added category
            for (MenuCategoryProxy cat : addedCats)
            {
                if (cat.getCourses().contains(course))
                {
                    cat.getCourses().remove(course);
                    coursesPanel.redraw();
                    return;
                }
            }
        }

        @Override
        public void error(RedrawablePanel coursesPanel, CourseProxy proxy, String reason)
        {
            Window.alert("Error: " + reason);
        }
    }

    /**
     * Handles add category button click
     */
    private class OnClickAddCategory implements ClickHandler
    {
        @Override
        public void onClick(ClickEvent event)
        {
            MenuCategoryProxy cat = WebRequestUtils.createMenuCategoryProxy(service);

            addedCats.add(cat);
            
            addedCourses.put(cat, new LinkedList<CourseProxy>());
            deletedCourses.put(cat, new LinkedList<CourseProxy>());

            // service.addMenuCategory(menu, cat);
            redraw();
        }
    }

    /**
     * Handles delete category button click
     */
    private class OnClickDeleteCategory implements ClickHandler
    {
        private final MenuCategoryProxy cat;

        public OnClickDeleteCategory(MenuCategoryProxy cat)
        {
            this.cat = cat;
        }

        @Override
        public void onClick(ClickEvent event)
        {
            
            if (menu.getCategories().contains(cat))
            {
                deletedCats.add(cat);
                // // menu.getCategories().remove(cat); // this is illegal
                // service.removeMenuCategory(menu, cat);
                redraw();
            }
            else if (addedCats.contains(cat))
            {
                addedCats.remove(cat);

                // deletedCats.add(cat);
                // // menu.getCategories().remove(cat); // this is illegal
                // service.removeMenuCategory(menu, cat);
                redraw();
            }
        }
    }

    /**
     * This class will set the title of the category when key is pressed
     */
    private class CategoryTitleKeyUpHandler implements KeyUpHandler
    {
        private final MenuCategoryProxy cat;

        /**
         * @param cat is the category to set its title.
         */
        public CategoryTitleKeyUpHandler(MenuCategoryProxy cat)
        {
            this.cat = cat;
        }

        @Override
        public void onKeyUp(KeyUpEvent event)
        {
            String s = ((TextBox) event.getSource()).getText();
            cat.setCategoryTitle(s);
        }
    }
}
