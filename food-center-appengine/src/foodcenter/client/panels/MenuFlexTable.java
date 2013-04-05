package foodcenter.client.panels;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import foodcenter.service.FoodCenterRequestFactory;
import foodcenter.service.proxies.MenuCategoryProxy;
import foodcenter.service.proxies.MenuProxy;

public class MenuFlexTable extends FlexTable
{
    
    private final FoodCenterRequestFactory requestFactory;
    
    private final MenuProxy menuProxy;
    
    public MenuFlexTable(FoodCenterRequestFactory requestFactory, MenuProxy menuProxy)
    {
        super();
        this.requestFactory = requestFactory;
        this.menuProxy = menuProxy;
        
        createHeader();   
        List<MenuCategoryProxy> catProxies = menuProxy.getCategories();
        if (null == catProxies)
        {
            catProxies = new LinkedList<MenuCategoryProxy>();
            menuProxy.setCategories(catProxies);
        }
        
        for (MenuCategoryProxy mcp : catProxies)
        {
            printMenuCategoryRow(mcp);
        }
    }
    
    private void createHeader()
    {
        this.removeAllRows();
        this.setText(0, 0, "categories");
        Button addCatButton = new Button("Add Category");
//        addCatButton.addClickHandler(new AddCategoryClickHandler(menu)); //TODO add click handler

        this.setWidget(0, 1, addCatButton);
        
    }
    
    public void addCategory()
    {
        MenuCategoryProxy menuCatProxy = requestFactory.getUserCommonService().create(MenuCategoryProxy.class);
        
        List<MenuCategoryProxy> cats = menuProxy.getCategories();
        cats.add(menuCatProxy);
        printMenuCategoryRow(menuCatProxy);
    }
    
    public void deleteCategory(int row)
    {
        List<MenuCategoryProxy> cats = menuProxy.getCategories();
        cats.remove(row - 1);
        removeRow(row);
    }
    
    public void printMenuCategoryRow(MenuCategoryProxy menuCatProxy)
    {
        int row = this.getRowCount();
        
        TextBox catTitle = new TextBox();
        catTitle.addKeyPressHandler(new CategoryTitleKeyPressHandler(catTitle, menuCatProxy));
        setWidget(row, 0, catTitle);
        
        Button deleteCatButton = new Button("delete");
        deleteCatButton.addClickHandler(new DeleteCategoryClickHandler(row));
        setWidget(row, 1, deleteCatButton);
        
        CoursesFlexTable coursesTable = new CoursesFlexTable(requestFactory, menuCatProxy);
        setWidget(row, 2, coursesTable);
    }
    
    class DeleteCategoryClickHandler implements ClickHandler
    {

        private final int row;
        
        public DeleteCategoryClickHandler(int row)
        {
            this.row = row;
        }
        
        @Override
        public void onClick(ClickEvent event)
        {
            deleteCategory(row);
        }
    }
    
    class CategoryTitleKeyPressHandler implements KeyPressHandler
    {
        private final TextBox titleBox;
        private final MenuCategoryProxy cat;
        
        public CategoryTitleKeyPressHandler(TextBox titleBox, MenuCategoryProxy cat)
        {
            this.titleBox = titleBox;
            this.cat = cat;
        }
        
        @Override
        public void onKeyPress(KeyPressEvent event)
        {
            cat.setCategoryTitle(titleBox.getText());
        }
        
    }
}
